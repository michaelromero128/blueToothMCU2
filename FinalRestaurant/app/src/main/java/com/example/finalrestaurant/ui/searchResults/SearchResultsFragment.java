package com.example.finalrestaurant.ui.searchResults;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.finalrestaurant.R;
import com.example.finalrestaurant.models.Restaurant;
import com.example.finalrestaurant.models.SearchResultsAdapter;
import com.example.finalrestaurant.models.YelpSearchResults;
import com.example.finalrestaurant.ui.details.DetailsViewModel;
import com.example.finalrestaurant.ui.searchEntry.SearchEntryViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerViewSearchResults;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        SearchEntryViewModel searchEntryViewModel = new ViewModelProvider(getActivity()).get(SearchEntryViewModel.class);
        //add listener to viewModel
        LiveData<ArrayList<Restaurant>> restaurants = searchEntryViewModel .getRestaurants();
        restaurants.observe(getActivity(), new Observer<ArrayList<Restaurant>>() {
            @Override
            public void onChanged(ArrayList<Restaurant> yelpSearchResults) {
                updateUI(yelpSearchResults);
            }
        });

    }
    private void updateUI(final ArrayList<Restaurant> restaurants){
        Log.e("My tag", "updateUi called");
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
}