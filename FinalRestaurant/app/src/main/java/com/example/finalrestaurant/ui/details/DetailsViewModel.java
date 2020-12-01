package com.example.finalrestaurant.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalrestaurant.models.Restaurant;

public class DetailsViewModel extends ViewModel {
    private MutableLiveData<Restaurant> restaurant;

    public DetailsViewModel(){
        restaurant = new MutableLiveData<>();
    }

    public LiveData<Restaurant> getRestaurant(){
        return restaurant;
    }
    public void setRestaurant(Restaurant restaurant){
        this.restaurant.setValue(restaurant);
    }

}
