package com.example.finalrestaurant.ui.searchEntry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalrestaurant.models.YelpSearchResults;

public class SearchEntryViewModel extends ViewModel {
    private MutableLiveData<YelpSearchResults> results;


    public SearchEntryViewModel(){
        results = new MutableLiveData<YelpSearchResults>();
    }
    public LiveData<YelpSearchResults> getResults(){
        return results;
    }

    public void setResults(YelpSearchResults results){

        this.results.postValue(results);
    }

}
