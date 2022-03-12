package com.example.bluetoothmcu

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.companion.CompanionDeviceManager
import android.content.pm.PackageManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import android.os.Handler
import java.util.*
private const val BLUETOOTH_CONNECT_REQUEST = 3

const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

class getDevices : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val uuid: UUID = UUID.fromString("7d9c03ce-5930-4467-ac85-140d9a47fcf4")
    private var device : BluetoothDevice? = null
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var  tv: TextView
    private lateinit var  button: Button
    private lateinit var bluetoothService: MyBluetoothService
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("CUSTOMA","get devices activity started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_devices)
        device = intent.getParcelableExtra("bt")
        tv = findViewById(R.id.tvResponse)
        button = findViewById(R.id.button)
        tv.setText("Information from bluetooth")
        handler = Handler(tv)
        getConnection()




    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(BLUETOOTH_CONNECT_REQUEST)
    fun getConnection(){
        if(EasyPermissions.hasPermissions(this,Manifest.permission.BLUETOOTH_ADMIN)){
            Log.i("CUSTOMA", "device name: ${device?.name}")
            Log.i(" CUSTOMA","newActivity started: waaa")
            val thread = ConnectThread()
            thread.start()
        }else{
            Log.i("CUSTOMA","need permission")
            EasyPermissions.requestPermissions(this,"I need bluetooth scan", BLUETOOTH_CONNECT_REQUEST, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH)
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray

    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== BLUETOOTH_CONNECT_REQUEST){
            Log.i("CUSTOMA","permission request returned")
            grantResults.forEach { ele-> Log.i("CUSTOMA",ele.toString()) }
        }
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }



    @SuppressLint("MissingPermission")
    private inner class ConnectThread: Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device?.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))

        }

        override fun run() {
            Log.i("CUSTOMA","Thread start")
            bluetoothAdapter.cancelDiscovery()

            mmSocket?.let { socket->
                socket.connect()
                bluetoothService = MyBluetoothService(handler)
                bluetoothService.ConnectedThread()


            }
        }
        fun cancel (){
            try{
                mmSocket?.close()
            }catch(e:IOException){
                Log.i("CUSTOMA","Could not close the connected socket",e)
            }
        }
    }


    fun manageMyConnectedSocket(bluetoothSocket: BluetoothSocket){
        Log.i("CUSTOMA","bluetooth socket type:${bluetoothSocket.connectionType}")

    }
}
class MyBluetoothService(private val handler:Handler){
    inner class ConnectedThread(private val mmSocket: BluetoothSocket): Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024)
        override fun run() {
            var numBytes: Int
            while (true) {
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.i("CUSTOMA", "Input stream was disconnected", e)
                    break
                }
                val readMsg = handler.obtainMessage(MESSAGE_READ, numBytes, -1, mmBuffer)
                readMsg.sendToTarget()
            }
        }

        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.i("CUSTOMA", "Error occured when sending data", e)
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }
            val writtenMsg = handler.obtainMessage(MESSAGE_WRITE, -1, -1, mmBuffer)
            writtenMsg.sendToTarget()
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.i("CUSTOMA", "Could not close the connect socket", e)
            }
        }
    }
}
class Handler(val tv: TextView) {
    inner class msg() {
        lateinit var data: Any
        lateinit var tv: TextView
        fun sendToTarget() {
            val string: String = data as String
            tv.setText(string)
        }
    }

    private lateinit var bundle: Bundle
    fun obtainMessage(msgCode: Int, a: Int, b: Int, bytes: ByteArray): msg {
        val zeMsg = msg()
        zeMsg.data = bytes
        zeMsg.tv = tv
        return zeMsg

    }

    fun obtainMessage(msgCode: Int): msg {
        val zeMsg = msg()
        zeMsg.tv = tv
        return zeMsg
    }

    fun sendMessage(zeMsg: msg) {
        val bundle: Bundle = zeMsg.data as Bundle
        zeMsg.tv.setText("message from handler: ${bundle.getString("toast")}")
    }
}

