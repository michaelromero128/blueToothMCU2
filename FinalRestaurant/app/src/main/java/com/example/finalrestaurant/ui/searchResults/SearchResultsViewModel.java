package com.example.finalrestaurant.ui.searchResults;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalrestaurant.models.YelpSearchResults;

public class SearchResultsViewModel extends ViewModel {
    private MutableLiveData<YelpSearchResults> yelpSearchResultsMutableLiveData;

    public SearchResultsViewModel(){
        yelpSearchResultsMutableLiveData = new MutableLiveData<>();
    }

    public LiveData<YelpSearchResults> getYelpSearchResultsMutableLiveData() {
        return yelpSearchResultsMutableLiveData;
    }
    public void setYelpSearchResultsMutableLiveData(YelpSearchResults yelpSearchResults){
        this.yelpSearchResultsMutableLiveData.setValue(yelpSearchResults);
    }
}

