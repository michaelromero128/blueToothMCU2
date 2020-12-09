package com.example.finalrestaurant.models;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalrestaurant.R;
import com.example.finalrestaurant.ui.details.DetailsViewModel;
import com.example.finalrestaurant.ui.home.HomeFragmentDirections;

import java.util.ArrayList;
import java.util.Iterator;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolderFavorites>{
    private ArrayList<Restaurant> restaurants;
    private FavoritesAdapterViewModelInterface favoritesAdapterViewModelInterface;

    public FavoritesAdapter(ArrayList<Restaurant> restaurants, FavoritesAdapterViewModelInterface favoritesAdapterViewModelInterface){
        this.restaurants = restaurants;
        this.favoritesAdapterViewModelInterface = favoritesAdapterViewModelInterface;
    }

    @Override
    public ViewHolderFavorites onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favorites, parent, false);
        return new ViewHolderFavorites(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderFavorites viewHolderFavorites, final int position){
        Log.e("My tag", restaurants.get(position).getId());

        viewHolderFavorites.getNameTextView().setText(restaurants.get(position).getName());
        viewHolderFavorites.getPhoneNumberTextView().setText(restaurants.get(position).getDisplay_phone() );
        StringBuilder stringBuilderPhoneNumber = new StringBuilder();
        Iterator iterator = restaurants.get(position).getLocation().getDisplay_address().iterator();
        while(iterator.hasNext()){
            stringBuilderPhoneNumber.append(iterator.next()+"\n");
        }
        viewHolderFavorites.getAddressTextView().setText(stringBuilderPhoneNumber.subSequence(0,stringBuilderPhoneNumber.length()-1));
        viewHolderFavorites.getDetailsButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoritesAdapterViewModelInterface.onClick(position);
                NavDirections action = HomeFragmentDirections.actionNavHomeToNavDetails();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }
    @Override
    public int getItemCount(){
        return restaurants.size();
    }

    public class ViewHolderFavorites extends RecyclerView.ViewHolder{
        private TextView nameTextView;
        private TextView addressTextView;
        private TextView phoneNumberTextView;
        private Button detailsButton;
        public ViewHolderFavorites(View view){
            super(view);

            nameTextView =(TextView) view.findViewById(R.id.temporaryTextViewName);
            addressTextView = (TextView) view.findViewById(R.id.temporaryTextViewAddress);
            phoneNumberTextView = (TextView) view.findViewById(R.id.temporaryTextViewPhoneNumber);
            detailsButton = (Button) view.findViewById(R.id.temporaryToDetailsButton);

        }
        public TextView getNameTextView(){return this.nameTextView;}
        public TextView getAddressTextView(){return this.addressTextView;}
        public TextView getPhoneNumberTextView(){return this.phoneNumberTextView;}
        public Button getDetailsButton(){return this.detailsButton;}


    }
    public interface FavoritesAdapterViewModelInterface{
        void onClick(int itemID);
    }

}
