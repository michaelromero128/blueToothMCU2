package com.example.finalrestaurant.models;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finalrestaurant.R;

import java.util.ArrayList;
import java.util.Iterator;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolderFavorites>{
    private ArrayList<Restaurant> restaurants;

    public FavoritesAdapter(ArrayList<Restaurant> restaurants){
        this.restaurants = restaurants;
    }

    @Override
    public ViewHolderFavorites onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favorites, parent, false);
        return new ViewHolderFavorites(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderFavorites viewHolderFavorites, final int position){

        viewHolderFavorites.getTextView().setText(restaurants.get(position).getName());
    }
    @Override
    public int getItemCount(){
        return restaurants.size();
    }

    public class ViewHolderFavorites extends RecyclerView.ViewHolder{
        private TextView textView;
        public ViewHolderFavorites(View view){
            super(view);
            textView =(TextView) view.findViewById(R.id.temporaryRecycleritem);
        }
        public TextView getTextView(){
            return textView;
        }

    }

}
