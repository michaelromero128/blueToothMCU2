package com.example.finalrestaurant.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalrestaurant.MainActivityViewModel;
import com.example.finalrestaurant.R;
import com.example.finalrestaurant.models.Restaurant;
import com.example.finalrestaurant.ui.login.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView test;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        //homeViewModel.getRestaurants().observer

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        test = (TextView) root.findViewById(R.id.testTextView);
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOn();
        LoginViewModel loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        FirebaseUser user = loginViewModel.getUser().getValue();
        final String id = user.getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.e("My tag", "before db get");
        db.collection("users").document(id).get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                Log.e("My tag", "onComplete");
                if(task.isSuccessful()) {
                    Log.e("My tag", "task successful");
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> map =document.getData();
                    if(map == null){
                        Log.e("My tag", "found null data");
                        final ArrayList<String> list = new ArrayList<>();
                        Map<String, Object> data = new HashMap<>();
                        data.put("favorites", list);
                        db.collection("users").document(id).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("My tag", "document added");
                                HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
                                homeViewModel.setRestaurants(new ArrayList<Restaurant>());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("My tag", "task unsuccessful, add on failure");
                                Log.e("My tag", e.getMessage());
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e("My tag", "set task complete");
                            }
                        });

                    }else{
                        String key = "favorites";
                        ArrayList<String> favorites =(ArrayList<String>) map.get(key);
                        Log.e("My tag", favorites.toString());
                        setRestaurants(favorites);

                    }
                }else{


                }

            }
        });
        Log.e("My tag", "after db get");

        return root;
    }
    public void setRestaurants(final ArrayList<String> keys){
        if(keys.size() == 0){
            setEmptyList();
            return;
        }
        final ArrayList<Restaurant> restaurants = new ArrayList<>();
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
                        HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
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

    }
    public void setEmptyList(){

    }
    public void updateUI(){
        HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        ArrayList<Restaurant> restaurants = homeViewModel.getRestaurants().getValue();
        Iterator<Restaurant> iterator = restaurants.iterator();
        String input = "";
        while(iterator.hasNext()){
            input += iterator.next().getName() + " / ";
        }
        test.setText(input);

    }


}