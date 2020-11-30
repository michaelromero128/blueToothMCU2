package com.example.finalrestaurant.models;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalrestaurant.R;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolderSearchResults>{

    private ArrayList<Restaurant> restaurants;

    public SearchResultsAdapter(ArrayList<Restaurant> restaurants){this.restaurants = restaurants;}
    @NonNull
    @Override
    public ViewHolderSearchResults onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_results, parent, false);
        return new ViewHolderSearchResults(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSearchResults viewHolderSearchResults, int position) {
        viewHolderSearchResults.getTextView().setText(restaurants.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolderSearchResults extends RecyclerView.ViewHolder{
        private TextView textView;
        public ViewHolderSearchResults(View view){
            super(view);
            Log.e("My tag",view.toString());
            //

            textView =(TextView) view.findViewById(R.id.temporarySearchResultsItem);
            Log.e("My tag, text view",textView.toString());
        }

        public TextView getTextView(){
            return textView;
        }

    }
}
