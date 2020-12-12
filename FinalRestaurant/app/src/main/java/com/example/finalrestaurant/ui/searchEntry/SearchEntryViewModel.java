package com.example.finalrestaurant.ui.searchEntry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalrestaurant.models.Restaurant;

import java.util.ArrayList;

public class SearchEntryViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Restaurant>> restaurants;


    public SearchEntryViewModel(){
        restaurants = new MutableLiveData<ArrayList<Restaurant>>();
    }
    public LiveData<ArrayList<Restaurant>> getRestaurants(){
        return restaurants;
    }

    public void setRestaurants(ArrayList<Restaurant> results){

        this.restaurants.postValue(results);
    }

}
