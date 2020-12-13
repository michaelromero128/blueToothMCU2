package com.example.finalrestaurant.ui.searchResults;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.finalrestaurant.MainActivityViewModel;
import com.example.finalrestaurant.R;
import com.example.finalrestaurant.models.Restaurant;
import com.example.finalrestaurant.models.SearchResultsAdapter;
import com.example.finalrestaurant.models.YelpSearchResults;
import com.example.finalrestaurant.ui.details.DetailsViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerViewSearchResults;
    private MapView mapView;
    private TextView errorTextView;

    // template code from android studio, no idea what it does
    public SearchResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultsFragment newInstance(String param1, String param2) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // turns on title bar
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        return view;
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstance){
        super.onViewCreated(view, savedInstance);

        //sets up recycler view
        recyclerViewSearchResults = (RecyclerView) view.findViewById(R.id.recyclerViewSearchResults);
        recyclerViewSearchResults.setHasFixedSize(false);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(getActivity()));
        errorTextView = (TextView) view.findViewById(R.id.searchResultErrorTextView);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstance);
        mapView.onResume();
        mapView.getMapAsync(this);
        //add listener to viewModel
        SearchResultsViewModel searchResultsViewModel = new ViewModelProvider(getActivity()).get(SearchResultsViewModel.class);
        LiveData<YelpSearchResults> yelpSearchResultsLiveData= searchResultsViewModel.getYelpSearchResultsMutableLiveData();
        yelpSearchResultsLiveData.observe(getActivity(), new Observer<YelpSearchResults>() {
            @Override
            public void onChanged(YelpSearchResults yelpSearchResults) {
                updateUI(yelpSearchResults.getBusinesses());
            }
        });



    }
    private void updateUI(final ArrayList<Restaurant> restaurants){
        Log.e("My tag", "updateUi called");
        // updates recycler view
        if(recyclerViewSearchResults == null){
            return;
        }

        Log.e("My tag","recyclerview not null");
        Log.e("My tag", restaurants.toString());
        SearchResultsAdapter.SearchResultsAdapterViewModelInterface searchResultsAdapterViewModelInterface = new SearchResultsAdapter.SearchResultsAdapterViewModelInterface() {
            @Override
            public void onClick(int itemID) {
                DetailsViewModel detailsViewModel = new ViewModelProvider(getActivity()).get(DetailsViewModel.class);
                detailsViewModel.setRestaurant(restaurants.get(itemID));
            }
        };
        recyclerViewSearchResults.setAdapter(new SearchResultsAdapter(restaurants, searchResultsAdapterViewModelInterface));

    }
    @Override
    public void onMapReady(final GoogleMap map){
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        SearchResultsViewModel searchResultsViewModel = new ViewModelProvider(getActivity()).get(SearchResultsViewModel.class);
        final LiveData<YelpSearchResults> searchResultsLiveData= searchResultsViewModel.getYelpSearchResultsMutableLiveData();
        Log.e("My tag",searchResultsLiveData.getValue().getBusinesses().toString());
        //attach listener when map loads
        searchResultsLiveData.observe(getActivity(), new Observer<YelpSearchResults>() {
            @Override
            public void onChanged(YelpSearchResults yelpSearchResults) {
                // handles nothing returned
                if(yelpSearchResults.getBusinesses().size() == 0){
                    // sets error text, displays it and hides other views
                    mapView.setVisibility(View.GONE);
                    recyclerViewSearchResults.setVisibility(View.GONE);
                    errorTextView.setVisibility(View.VISIBLE);
                }else{
                    // on successful search, hide error text, show other views
                    errorTextView.setVisibility(View.GONE);
                    mapView.setVisibility(View.VISIBLE);
                    recyclerViewSearchResults.setVisibility(View.VISIBLE);
                }
                // loads map when yelpSearchResults change
                // gets the center of the search results
                final YelpSearchResults.Region.Center center = yelpSearchResults.getRegion().getCenter();
                LatLng cameraCenter = new LatLng(center.getLatitude(),center.getLongitude());
                // updates camera
                map.moveCamera(CameraUpdateFactory.newLatLng(cameraCenter));
                map.moveCamera(CameraUpdateFactory.zoomTo(12f));
                //iterates over restaurants searched and places a marker for each
                ArrayList<Restaurant> restaurants = searchResultsLiveData.getValue().getBusinesses();
                Iterator iterator = restaurants.iterator();
                while(iterator.hasNext()){
                    Restaurant restaurant = (Restaurant) iterator.next();
                    Restaurant.Coordinates coords =restaurant.getCoordinates();
                    LatLng restaurantLatLng = new LatLng(coords.getLatitude(), coords.getLongitude());
                    map.addMarker(new MarkerOptions().position(restaurantLatLng).title(restaurant.getName()));
                    Log.e("My tag", "Marker placed");
                }
            }
        });
    }
}