package com.example.finalrestaurant.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalrestaurant.MainActivityViewModel;
import com.example.finalrestaurant.R;
import com.example.finalrestaurant.models.FavoritesAdapter;
import com.example.finalrestaurant.models.Restaurant;
import com.example.finalrestaurant.ui.details.DetailsViewModel;
import com.example.finalrestaurant.ui.login.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView favoritesLoadingTextView;
    private RecyclerView recyclerViewFavorites;
    private TextView favoritesNothingTextView;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // makes this fragment persistent
        setRetainInstance(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // inflate view
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //setup recycler view
        recyclerViewFavorites = (RecyclerView) root.findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites.setHasFixedSize(false);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getActivity()));
        //hide recyclerview and show loading textview
        recyclerViewFavorites.setVisibility(View.GONE);
        favoritesLoadingTextView = (TextView) root.findViewById(R.id.favoritesLoadingTextView);
        favoritesLoadingTextView.setVisibility(View.VISIBLE);
        favoritesNothingTextView = (TextView) root.findViewById(R.id.nothingTextView);
        favoritesNothingTextView.setVisibility(View.GONE);
        //listener for when the favorites list changes
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.getRestaurants().observe(getActivity(), new Observer<ArrayList<Restaurant>>() {
            @Override
            public void onChanged(ArrayList<Restaurant> restaurants) {
                if (restaurants != null) {
                    if (restaurants.size() == 0 && homeViewModel.getEmpty() != null) {
                        homeViewModel.setEmpty(true);
                    }
                    if (restaurants.size() != 0) {
                        homeViewModel.setEmpty(false);
                    }
                    Log.e("My tag", "view model update detected");
                    Log.e("My tag", restaurants.toString());
                    updateUI(restaurants);
                }
            }
        });
        // turn on nav bar
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOn();
        return root;
    }

    public void updateUI(final ArrayList<Restaurant> restaurants){
        // loads appropriate text view if it isn't loaded or nothing in favorites
        // turns them off otherwise
        if(homeViewModel.getEmpty().getValue() == null){
            Log.e("My tag", "loading fired");
            favoritesLoadingTextView.setVisibility(View.VISIBLE);
            favoritesNothingTextView.setVisibility(View.GONE);
            return;
        }
        Log.e("My tag","empty:" + homeViewModel.getEmpty().getValue().toString());
        if(homeViewModel.getEmpty().getValue()){
            Log.e("My tag", "Nothing text view fired");
            favoritesNothingTextView.setVisibility(View.VISIBLE);
            favoritesLoadingTextView.setVisibility(View.GONE);
            return;
        }else{
        }
        favoritesLoadingTextView.setVisibility(View.GONE);
        favoritesNothingTextView.setVisibility(View.GONE);
        Log.e("My tag", "update UI start");
    //replaces recycler view adapter with new information
        HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        final DetailsViewModel detailsViewModel = new ViewModelProvider(getActivity()).get(DetailsViewModel.class);
        // creates call back for a button in a recycler view item
        FavoritesAdapter.FavoritesAdapterViewModelInterface adapterInterface = new FavoritesAdapter.FavoritesAdapterViewModelInterface() {
            @Override
            public void onClick(int itemID) {
                detailsViewModel.setRestaurant(restaurants.get(itemID));
            }
        };
        recyclerViewFavorites.setAdapter(new FavoritesAdapter(restaurants, adapterInterface));
        recyclerViewFavorites.setVisibility(View.VISIBLE);
        Log.e("My tag","update UI finished");
    }
}