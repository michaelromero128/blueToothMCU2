package com.example.finalrestaurant;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<Boolean> showMenu = new MutableLiveData<Boolean>();

    public LiveData<Boolean> getMenuSetting(){
        if(showMenu.getValue() == null){
            showMenu = new MutableLiveData<Boolean>();
            showMenu.postValue(false);
            }
        return showMenu;
    }
    public void turnOn(){
        showMenu.postValue(true);
    }
    public void turnOff(){
        showMenu.postValue(false);
    }
}
