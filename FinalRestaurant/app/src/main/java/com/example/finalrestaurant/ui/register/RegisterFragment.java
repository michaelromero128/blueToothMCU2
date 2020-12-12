package com.example.finalrestaurant.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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
import com.example.finalrestaurant.ui.home.HomeViewModel;
import com.example.finalrestaurant.ui.login.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private int RC_SIGN_IN = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_register, container, false);
        //enables url link on text
        TextView tos = (TextView) root.findViewById(R.id.registerText);
        tos.setMovementMethod(LinkMovementMethod.getInstance());
        tos.setText(Html.fromHtml(getResources().getString(R.string.tos)));

        //attaches listener to start login activity on one button and navigate to login screen on the other
        Button buttonAcceptRegister = (Button) root.findViewById(R.id.buttonApproveRegister);
        Button buttonDeclineRegister = (Button) root.findViewById(R.id.buttonRegisterDecline);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.auth_client_id)).requestEmail().requestProfile().build();
        final GoogleSignInClient signInClient = GoogleSignIn.getClient(getContext(), gso);
        buttonAcceptRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = signInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        buttonDeclineRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_global_to_nav_login);
            }
        });
        //turn off title bar
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOff();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //listener for when the activity finishes
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                performLogin(account);
            } catch (ApiException e) {
                Log.e("My tag", "signedInResult:failed code= " + e.getStatusCode());
            }
        }
    }

    public void performLogin(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //grabs user information and restaurant info
        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                Log.e("My tag", "onComplete");
                if (task.isSuccessful()) {
                    Log.e("My tag", "task successful");
                    final FirebaseUser user = mAuth.getCurrentUser();
                    String email = user.getEmail();
                    String name = user.getDisplayName();
                    final LoginViewModel loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
                    loginViewModel.setUser(user);
                    loginViewModel.setEmail(email);
                    loginViewModel.setName(name);
                    loginViewModel.setPhotoUrl(account.getPhotoUrl().toString());
                    final HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String,Object> data = new HashMap<>();
                    final ArrayList<String> list = new ArrayList<>();
                    data.put("favorites",list);
                    db.collection("users").document(user.getUid()).get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Map<String, Object> map= task.getResult().getData();
                                if(map == null){
                                    // if no data found in system, adds empty arrays to data under user id
                                    homeViewModel.setRestaurants(new ArrayList<Restaurant>());
                                    homeViewModel.setFavoritesList(new ArrayList<String>());
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("favorites", new ArrayList<>());
                                    db.collection("users").document(user.getUid()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_global_to_nav_home);
                                        }
                                    });
                                }else{
                                    //alert dialog if user data found and redirect
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setNeutralButton("Ok", null);

                                    builder.setMessage("You are already registered");
                                    homeViewModel.setRestaurants(new ArrayList<Restaurant>());
                                    homeViewModel.setFavoritesList(new ArrayList<String>());
                                    AlertDialog dialogFragment = builder.create();
                                    dialogFragment.show();
                                    ArrayList<String> favorites =(ArrayList<String>) map.get("favorites");
                                    Log.e("New tag", favorites.toString());
                                    if(favorites.size() == 0){
                                        setEmptyList();
                                    }else{
                                        setRestaurants(favorites);
                                    }
                                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_global_to_nav_home);
                                }

                            }
                        }
                    });

                } else {
                }
            }
        });
    }
    public void setRestaurants(final ArrayList<String> keys) throws InvalidParameterException {
        // once given a set of keys from fire store, retrieves a list of restaurants from yelp.
        //only updates the viewmodel if all restaurants are collected
        if(keys.size() == 0){
            throw new InvalidParameterException("Parameter of zero given");
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
                        homeViewModel.setEmpty(false);
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
                public Map<String, String> getHeaders() throws AuthFailureError {
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
        HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        homeViewModel.setFavoritesList(new ArrayList<String>());
        homeViewModel.setRestaurants(new ArrayList<Restaurant>());
        homeViewModel.setEmpty(true);
    }
}