package com.example.finalrestaurant.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalrestaurant.models.Restaurant;

import java.util.ArrayList;

// view model that stores information regarding a logged in users favorites
public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Restaurant>> restaurants;
    private MutableLiveData<ArrayList<String>> favoritesList;
    private MutableLiveData<Boolean> empty;

    public HomeViewModel() {
        // data of concern
        restaurants = new MutableLiveData<>();
        restaurants.setValue(new ArrayList<Restaurant>());
        favoritesList = new MutableLiveData<>();
        favoritesList.setValue(new ArrayList<String>());
        // flag to see if model is not loaded yet, loaded and full, and loaded but empty
        empty = new MutableLiveData<>();
        empty.setValue(null);
    }
    // getters and setters
    public LiveData<Boolean>getEmpty(){
        return empty;
    }
    public void setEmpty(Boolean status){
        empty.setValue(status);
    }
    public LiveData<ArrayList<Restaurant>> getRestaurants() {
        return restaurants;
    }
    public void setRestaurants(ArrayList<Restaurant> restaurants){
        this.restaurants.setValue(restaurants);
    }
    public LiveData<ArrayList<String>> getFavoritesList(){return favoritesList;}
    public void setFavoritesList(ArrayList<String> favoritesList){
        this.favoritesList.setValue(favoritesList);
    }
}