package com.example.finalrestaurant;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

// stores variable to toggle the title bar


public class MainActivityViewModel extends ViewModel {
    // getter for model's data
    private MutableLiveData<Boolean> showMenu = new MutableLiveData<Boolean>();
    // constructor
    public LiveData<Boolean> getMenuSetting(){
        if(showMenu.getValue() == null){
            showMenu = new MutableLiveData<Boolean>();
            showMenu.postValue(false);
            }
        return showMenu;
    }
    // setters for model's data
    public void turnOn(){
        showMenu.postValue(true);
    }
    public void turnOff(){
        showMenu.postValue(false);
    }
}
