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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText searchEditText;
    private EditText locationEditText;
    private Button submitSearchButton;
    private TextView errorTextView;



    public SearchEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOn();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_entry, container, false);
        Button buttonSubmitSearch = (Button) view.findViewById(R.id.submitSearchButton);
        buttonSubmitSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(SearchEntryFragmentDirections.actionSearchEntryToNavSearchResults());
            }
        });
        searchEditText =(EditText) view.findViewById(R.id.searchEditText);
        locationEditText = (EditText) view.findViewById(R.id.locationEditText);
        buttonSubmitSearch = (Button) view.findViewById(R.id.submitSearchButton);
        errorTextView = (TextView) view.findViewById(R.id.errorTextView);
        errorTextView.setVisibility(View.INVISIBLE);
        buttonSubmitSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        return view;
    }
    private void search(){
        Log.e("My tag", "Thing");
        errorTextView.setVisibility(View.INVISIBLE);
        String searchText = searchEditText.getText().toString();
        String locationText = locationEditText.getText().toString();
        String urlPrefix = "https://api.yelp.com/v3/businesses/search";
        if(locationText.equals("")){
            GPSTracker gps = new GPSTracker(this.getContext());
            Double longitude = null;
            Double latitude = null;
            if(gps.canGetLocation()){
                longitude =  1.0*(((int) gps.getLongitude()*10000)/10000);
                latitude = 1.0*(((int) gps.getLatitude()*10000)/10000);
            }
            if(latitude != null){
                String urlSuffix = String.format("?term=%s&longitude=%s&latitude=%s","Star Bucks".replaceAll(" ", "%20"),longitude,latitude);
                RequestQueue queue = Volley.newRequestQueue(this.getActivity());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlPrefix + urlSuffix, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleInput(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("My tag", "bah");

                        try{
                            Log.e("MyTag", new String(error.networkResponse.data,"UTF-8"));
                        }catch(Exception e){
                            Log.e("MyTag", "wah");
                        }
                        errorTextView.setText("Something bad happened");
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
                queue.add(request);

            }else{
                errorTextView.setText("GPS not working, enter location by hand");
                errorTextView.setVisibility(View.VISIBLE);
            }
        }else{
            String urlSuffix = String.format("?term=%s&location=%s","StarBucks", "Miami");
            RequestQueue queue = Volley.newRequestQueue(this.getActivity());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlPrefix+urlSuffix,null, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response){
                    Log.e("My tag", "wuh");

                    handleInput(response);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Log.e("My tag", "Thing");
                    Log.e("My tag", "Thing");

                    errorTextView.setText("An error occured");
                    errorTextView.setVisibility(View.VISIBLE);

                    Log.e("My tag", "bah");

                    try{
                        Log.e("MyTag", new String(error.networkResponse.data,"UTF-8"));
                    }catch(Exception e){
                        Log.e("MyTag", "wah");
                    };
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params =new HashMap<>(super.getHeaders());
                    params.put("Authorization", "Bearer "+getString(R.string.yelp_api_key));
                    return params;
                }
            };
            queue.add(request);
        }
    }
    private void handleInput(JSONObject response){
        Gson gson = new Gson();
        YelpSearchResults yelpSearchResults = null;
        SearchEntryViewModel searchEntryViewModel = new ViewModelProvider(getActivity()).get(SearchEntryViewModel.class);
        yelpSearchResults = gson.fromJson(response.toString(),YelpSearchResults.class);
        searchEntryViewModel.setResults(yelpSearchResults);
        Log.e("My tag",yelpSearchResults.getBusinesses().get(0).getDisplay_phone());
        try{
            Log.e("My Tag", searchEntryViewModel.getResults().getValue().getBusinesses().get(0).getDisplay_phone());
        }catch(Exception e){
            Log.e("My tag,", "It died");
        }
        NavDirections action = SearchEntryFragmentDirections.actionSearchEntryToNavSearchResults();
        View view = getView();
        if(view != null && yelpSearchResults != null){
            Navigation.findNavController(view).navigate(action);
        }



    }
}