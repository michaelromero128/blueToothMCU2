package com.example.finalrestaurant.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalrestaurant.models.Restaurant;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Restaurant>> restaurants;
    private MutableLiveData<ArrayList<String>> favoritesList;

    public HomeViewModel() {
        restaurants = new MutableLiveData<>();
        restaurants.setValue(null);
        favoritesList = new MutableLiveData<>();
        favoritesList.setValue(null);

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