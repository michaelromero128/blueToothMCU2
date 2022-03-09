package com.example.bluetoothmcu

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.companion.CompanionDeviceManager
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

class getDevices : AppCompatActivity() {
    private var device : BluetoothDevice? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_devices)
        device =
            intent.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        Log.i("CourseraM","newActivity started: ${device?.name}")
    }
}