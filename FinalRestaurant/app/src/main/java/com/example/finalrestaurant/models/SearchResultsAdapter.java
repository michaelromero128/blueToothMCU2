package com.example.finalrestaurant.models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalrestaurant.R;
import com.example.finalrestaurant.ui.details.DetailsViewModel;
import com.example.finalrestaurant.ui.searchResults.SearchResultsFragmentDirections;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolderSearchResults>{

    private ArrayList<Restaurant> restaurants;
    private Context context;

    public SearchResultsAdapter(ArrayList<Restaurant> restaurants, Context context){
        this.restaurants = restaurants;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolderSearchResults onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_results, parent, false);
        return new ViewHolderSearchResults(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderSearchResults viewHolderSearchResults, int position) {
        viewHolderSearchResults.getNameTextView().setText(restaurants.get(position).getName());
        viewHolderSearchResults.getAddressTextView().setText(restaurants.get(position).getLocation().getDisplay_address().get(0));
        viewHolderSearchResults.getNumberTextView().setText(restaurants.get(position).getDisplay_phone());
        viewHolderSearchResults.getToDetailsButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailsViewModel detailsViewModel = new ViewModelProvider(viewHolderSearchResults.getView().getContext()).get(DetailsViewModel.class);
                NavDirections action = SearchResultsFragmentDirections.actionNavSearchResultsToNavDetails();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolderSearchResults extends RecyclerView.ViewHolder{
        private TextView nameTextView;
        private TextView numberTextView;
        private TextView addressTextView;
        private Button toDetailsButton;
        private View view;
        public ViewHolderSearchResults(View view){
            super(view);
            Log.e("My tag",view.toString());
            this.view = view;
            Log.e("My_tag", "view holder search results constructor fired");
            nameTextView =(TextView) view.findViewById(R.id.temporaryTextViewName);
            numberTextView = (TextView) view.findViewById(R.id.temporaryTextViewPhoneNumber);
            addressTextView = (TextView) view.findViewById(R.id.temporaryTextViewAddress);
            toDetailsButton = (Button) view.findViewById(R.id.temporaryToDetailsButton);
        }

        public TextView getNameTextView(){
            return nameTextView;
        }
        public TextView getNumberTextView(){
            return numberTextView;
        }
        public TextView getAddressTextView(){
            return addressTextView;
        }
        public Button getToDetailsButton(){
            return toDetailsButton;
        }
        public View getView(){return view;}

    }
}
