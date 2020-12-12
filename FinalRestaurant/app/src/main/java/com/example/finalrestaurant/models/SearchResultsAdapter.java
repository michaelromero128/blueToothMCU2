package com.example.finalrestaurant.models;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalrestaurant.R;
import com.example.finalrestaurant.ui.searchResults.SearchResultsFragmentDirections;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolderSearchResults>{

    private ArrayList<Restaurant> restaurants;
    private SearchResultsAdapterViewModelInterface searchResultsAdapterViewModelInterface;

    //constructor
    public SearchResultsAdapter(ArrayList<Restaurant> restaurants, SearchResultsAdapterViewModelInterface searchResultsAdapterViewModelInterface){
        this.restaurants = restaurants;
        this.searchResultsAdapterViewModelInterface = searchResultsAdapterViewModelInterface;
    }
    @NonNull
    @Override
    public ViewHolderSearchResults onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // view inflater
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_results, parent, false);
        return new ViewHolderSearchResults(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderSearchResults viewHolderSearchResults, final int position) {
        // sets views when data is added, parses more complicated data
        viewHolderSearchResults.getNameTextView().setText(restaurants.get(position).getName());
        Iterator iterator = restaurants.get(position).getLocation().getDisplay_address().iterator();
        StringBuilder addressStringBuilder = new StringBuilder();
        while(iterator.hasNext()){
            addressStringBuilder.append(iterator.next()+"\n");
        }
        viewHolderSearchResults.getAddressTextView().setText(addressStringBuilder.subSequence(0,addressStringBuilder.length()-1));
        viewHolderSearchResults.getNumberTextView().setText(restaurants.get(position).getDisplay_phone());
        // sets listener on details button
        viewHolderSearchResults.getToDetailsButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fires call back to update the details viewmodel
                searchResultsAdapterViewModelInterface.onClick(position);
                // navigate to details fragment
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
    public interface SearchResultsAdapterViewModelInterface {
        void onClick(int itemID);
    }
}
