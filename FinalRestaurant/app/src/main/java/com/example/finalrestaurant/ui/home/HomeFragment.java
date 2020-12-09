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



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // inflate view
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // homeViewModel listener
        //recyclerViewFavorites = (RecyclerView) root.findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites = (RecyclerView) root.findViewById(R.id.recyclerViewFavorites);
        //setup recycler view
        recyclerViewFavorites.setHasFixedSize(false);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getActivity()));
        //hide recyclerview and show loading textview
        recyclerViewFavorites.setVisibility(View.GONE);
        favoritesLoadingTextView = (TextView) root.findViewById(R.id.favoritesLoadingTextView);
        favoritesLoadingTextView.setVisibility(View.VISIBLE);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.getRestaurants().observe(getActivity(), new Observer<ArrayList<Restaurant>>() {
            @Override
            public void onChanged(ArrayList<Restaurant> restaurants) {
                if(restaurants != null) {
                    Log.e("My tag", "view model update detected");
                    Log.e("My tag", restaurants.toString());

                    updateUI(restaurants);
                }
            }
        });
        // turn on nav bar
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOn();
        //gets user id



        return root;
    }
    public void setRestaurants(final ArrayList<String> keys){
        // once given a set of keys from fire store, retrieves a list of restaurants from yelp.
        //only updates the viewmodel if all restaurants are collected
        if(keys.size() == 0){
            setEmptyList();
            return;
        }
        final ArrayList<Restaurant> restaurants = new ArrayList<>();
        final HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        homeViewModel.setFavoritesList(keys);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        for(int i = 0; i < keys.size(); i++){
            String url = "https://api.yelp.com/v3/businesses/" + keys.get(i);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Restaurant restaurant = gson.fromJson(response.toString(), Restaurant.class);
                    restaurants.add(restaurant);
                    if( restaurants.size()== keys.size()){
                        Collections.sort(restaurants, new Comparator<Restaurant>() {
                            @Override
                            public int compare(Restaurant restaurant, Restaurant t1) {
                                return restaurant.getName().compareTo(t1.getName());
                            }
                        });

                        homeViewModel.setRestaurants(restaurants);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("My tag", "Volley Request failed");
                    setEmptyList();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError{
                    Map<String, String> params =new HashMap<>(super.getHeaders());
                    params.put("Authorization", "Bearer "+getString(R.string.yelp_api_key));
                    return params;
                }
            };
            queue.add(request);

        }
        Log.e("My tag","set restaurants method complete");

    }
    public void setEmptyList(){

    }
    public void updateUI(final ArrayList<Restaurant> restaurants){
//        if(recyclerViewFavorites == null){
//
//            return;
//        }

        Log.e("My tag", "update UI start");
        HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);


        final DetailsViewModel detailsViewModel = new ViewModelProvider(getActivity()).get(DetailsViewModel.class);
        FavoritesAdapter.FavoritesAdapterViewModelInterface adapterInterface = new FavoritesAdapter.FavoritesAdapterViewModelInterface() {
            @Override
            public void onClick(int itemID) {
                detailsViewModel.setRestaurant(restaurants.get(itemID));
            }
        };
        recyclerViewFavorites.setAdapter(new FavoritesAdapter(restaurants, adapterInterface));
        favoritesLoadingTextView.setVisibility(View.GONE);
        recyclerViewFavorites.setVisibility(View.VISIBLE);
        Log.e("My tag","update UI finished");



    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //sets up recycler view after view has been created
        super.onViewCreated(view, savedInstanceState);


    }



}