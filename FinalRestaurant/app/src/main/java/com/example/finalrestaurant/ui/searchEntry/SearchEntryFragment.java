package com.example.finalrestaurant.ui.searchEntry;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalrestaurant.MainActivityViewModel;
import com.example.finalrestaurant.R;
import com.example.finalrestaurant.models.GPSTracker;
import com.example.finalrestaurant.models.YelpSearchResults;
import com.example.finalrestaurant.ui.searchResults.SearchResultsFragment;
import com.example.finalrestaurant.ui.searchResults.SearchResultsViewModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchEntryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    // TODO: Rename and change types of parameters

    private EditText searchEditText;
    private EditText locationEditText;
    private TextView errorTextView;


    // template code generated by android studio
    public SearchEntryFragment() {
        // Required empty public constructor
    }

    public static SearchEntryFragment newInstance(String param1, String param2) {
        SearchEntryFragment fragment = new SearchEntryFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // turns on title bar
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_entry, container, false);
        // gets all the views and buttons
        Button buttonSubmitSearch = (Button) view.findViewById(R.id.submitSearchButton);
        searchEditText =(EditText) view.findViewById(R.id.searchEditText);
        locationEditText = (EditText) view.findViewById(R.id.locationEditText);
        buttonSubmitSearch = (Button) view.findViewById(R.id.submitSearchButton);
        errorTextView = (TextView) view.findViewById(R.id.errorTextView);
        errorTextView.setVisibility(View.INVISIBLE);
        //attach listener to search button
        buttonSubmitSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        return view;
    }
    // function fired when search button entered
    private void search(){
        // hides error text when search begins
        errorTextView.setVisibility(View.INVISIBLE);
        String searchText = searchEditText.getText().toString();
        // prompts for user input if search field is empty
        if(searchText == "" || searchText.matches("^\\s*")){
            errorTextView.setText("Please enter search criteria");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }
        String locationText = locationEditText.getText().toString();
        String urlPrefix = "https://api.yelp.com/v3/businesses/search";

        if(locationText.equals("")){
            //grabs gps info if location is blank
            GPSTracker gps = new GPSTracker(this.getContext());
            Double longitude = null;
            Double latitude = null;
            if(gps.canGetLocation()){
                longitude =  1.0*(((int) gps.getLongitude()*10000)/10000);
                latitude = 1.0*(((int) gps.getLatitude()*10000)/10000);
            }
            if(latitude != null){
                // if gps info successfully retrieved, query yelp
                String urlSuffix = String.format("?term=%s&longitude=%s&latitude=%s",searchText.replaceAll(" ", "%20"),longitude,latitude);
                RequestQueue queue = Volley.newRequestQueue(this.getActivity());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlPrefix + urlSuffix, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // fire function on success
                        errorTextView.setVisibility(View.GONE);
                        handleInput(response);
                    }
                }, new Response.ErrorListener() {
                    //shows error text view upon error
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            Log.e("MyTag", new String(error.networkResponse.data,"UTF-8"));
                        }catch(Exception e){
                            Log.e("MyTag", "didn't get network response data");
                        }
                        errorTextView.setText("Service is down, try again later");
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params =new HashMap<>(super.getHeaders());
                        params.put("Authorization", "Bearer "+getString(R.string.yelp_api_key));
                        return params;
                    };
                };
                //send request
                queue.add(request);

            }else{
                // error for when GPS is down
                errorTextView.setText("GPS not working, enter location by hand");
                errorTextView.setVisibility(View.VISIBLE);
            }
        }else{
            // updates url suffix with search parameters
            String urlSuffix = String.format("?term=%s&location=%s",searchText.replaceAll(" ", "%20"), locationText.replaceAll(" ", "%20"));
            // fires request to yelp
            RequestQueue queue = Volley.newRequestQueue(this.getActivity());
            // query yelp
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlPrefix+urlSuffix,null, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response){
                    // handles the response of the query
                    handleInput(response);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    //error on bad request
                    try{
                        //checks for bad location
                        String errorMessage = new String(error.networkResponse.data,"UTF-8");
                        if(errorMessage.matches(".*LOCATION.*")){
                            errorTextView.setText("Invalid location");
                        }else{
                            //generic messages otherwise
                            errorTextView.setText("An error occured");
                        }
                    }catch(Exception e){
                        errorTextView.setText("An error occured");
                    }
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params =new HashMap<>(super.getHeaders());
                    params.put("Authorization", "Bearer "+getString(R.string.yelp_api_key));
                    return params;
                }
            };
            // sends request
            queue.add(request);
        }
    }
    private void handleInput(JSONObject response){
        // converts json response into a YelpSearchResults object
        Gson gson = new Gson();
        YelpSearchResults yelpSearchResults;
        SearchEntryViewModel searchEntryViewModel = new ViewModelProvider(getActivity()).get(SearchEntryViewModel.class);
        SearchResultsViewModel searchResultsViewModel = new ViewModelProvider(getActivity()).get(SearchResultsViewModel.class);
        yelpSearchResults = gson.fromJson(response.toString(),YelpSearchResults.class);
        // updates viewmodels
        searchEntryViewModel.setRestaurants(yelpSearchResults.getBusinesses());
        searchResultsViewModel.setYelpSearchResultsMutableLiveData(yelpSearchResults);
        //redirects to search results fragment
        NavDirections action = SearchEntryFragmentDirections.actionSearchEntryToNavSearchResults();
        View view = getView();
        //navigate to search results
        if(view != null && yelpSearchResults != null){
            Navigation.findNavController(view).navigate(action);
        }



    }
}