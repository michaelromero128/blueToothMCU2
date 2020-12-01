package com.example.finalrestaurant.ui.details;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalrestaurant.R;
import com.example.finalrestaurant.models.Restaurant;
import com.example.finalrestaurant.ui.home.HomeViewModel;
import com.example.finalrestaurant.ui.login.LoginViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //setting simple text views
        DetailsViewModel detailsViewModel = new ViewModelProvider(getActivity()).get(DetailsViewModel.class);
        Restaurant restaurant = detailsViewModel.getRestaurant().getValue();
        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.detailsFragmentGridLayout);

        ((TextView) gridLayout.findViewById(R.id.textViewDetailsName)).setText(restaurant.getName());
        ((TextView) gridLayout.findViewById(R.id.textViewDetailsIs_Closed)).setText(restaurant.getIs_closed() ? "Closed": "Open");
        ((TextView) gridLayout.findViewById(R.id.textViewsDetailsAddress)).setText(restaurant.getLocation().getDisplay_address().get(0));
        ((TextView) gridLayout.findViewById(R.id.textViewDetailsRatings)).setText(restaurant.getRating().toString());
        StringBuilder categoriesStringBuilder = new StringBuilder();
        Iterator iterator = restaurant.getCategories().iterator();
        while(iterator.hasNext()) {
            categoriesStringBuilder.append(iterator.next() + ", ");
        }
        String categoriesString = categoriesStringBuilder.subSequence(0,categoriesStringBuilder.length()).toString();
        ((TextView) gridLayout.findViewById(R.id.textViewDetailCategories)).setText(categoriesString);
        ((TextView) gridLayout.findViewById(R.id.textViewDetailsName)).setText(restaurant.getDisplay_phone());
        ((TextView) gridLayout.findViewById(R.id.textViewDetailsName)).setText("Price: "+restaurant.getPrice());
        //setting image view
        final String params = restaurant.getImage_url();
        final ImageView imageView= (ImageView) view.findViewById(R.id.detailsImageView);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = new java.net.URL(params).openStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    Log.e("My tag", "Runnable finished");
                } catch (Exception e) {
                    Log.e("My tag", "failure on runnable");
                    Log.e("My tag", e.getMessage()+e.getLocalizedMessage());
                }

            }
        });
        // set up toggle button
        HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        setFavoriteToggleButtonImage(gridLayout, restaurant.getId(), homeViewModel);
    }
    public void setFavoriteToggleButtonImage(View view, final String restaurantID, final HomeViewModel homeViewModel) {
        // set image for add favorite toggle
        final LiveData<ArrayList<String>> liveDataFavorites = homeViewModel.getFavoritesList();
        ArrayList<String> favorites = liveDataFavorites.getValue();
        final Button button = (Button) view.findViewById(R.id.detailsButtonToggleFavorite);
        LoginViewModel loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        final String userID = loginViewModel.getUser().getValue().getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //sets the image
        if (favorites.contains(restaurantID)) {
            button.setBackgroundResource(R.drawable.ic_iconmonstr_favorite_toggle_off);
        } else {
            button.setBackgroundResource(R.drawable.ic_iconmonstr_favorite_toggle_on);
        }
        // listener for the image
        liveDataFavorites.observe(getActivity(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> favorites) {
                if (favorites.contains(restaurantID)) {
                    button.setBackgroundResource(R.drawable.ic_iconmonstr_favorite_toggle_off);
                } else {
                    button.setBackgroundResource(R.drawable.ic_iconmonstr_favorite_toggle_on);
                }
            }
        });
        // listener for click to toggle favorites and update db
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> favorites = liveDataFavorites.getValue();
                if (favorites.contains(restaurantID)) {
                    favorites.remove(restaurantID);
                    favorites = new ArrayList(favorites);
                    homeViewModel.setFavoritesList(favorites);
                } else {
                    favorites.add(restaurantID);
                    favorites = new ArrayList(favorites);
                    homeViewModel.setFavoritesList(favorites);
                }
                db.collection("users").document(userID).set(favorites);
            }
        });
    }


}