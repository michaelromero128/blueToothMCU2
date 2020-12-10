package com.example.finalrestaurant.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.finalrestaurant.MobileNavigationDirections;
import com.example.finalrestaurant.R;
import com.example.finalrestaurant.models.Restaurant;
import com.example.finalrestaurant.ui.home.HomeViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private Button buttonRegister;
    private Button buttonLogin;

    private int RC_SIGN_IN = 0;
    private View zeView;
    private FirebaseAuth mAuth;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.e("My_tag","login view started");

        final View root = inflater.inflate(R.layout.fragment_login, container, false);

        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOff();
        LoginViewModel loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        if(loginViewModel.getUser().getValue() != null){
            NavDirections action = MobileNavigationDirections.actionGlobalToNavHome();
            Navigation.findNavController(root).navigate(action);
            return root;
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.auth_client_id)).requestEmail().requestProfile().build();
        final GoogleSignInClient signInClient = GoogleSignIn.getClient(getContext(),gso);
        buttonLogin = (Button) root.findViewById(R.id.buttonLogin);
        buttonRegister= (Button) root.findViewById(R.id.buttonRegister);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = signInClient.getSignInIntent();
                startActivityForResult(signInIntent,RC_SIGN_IN);


            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = LoginFragmentDirections.actionNavLoginToNavRegister();
                Navigation.findNavController(view).navigate(action);
            }
        });
        loginViewModel.getUser().observe(getActivity(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser == null){
                    signInClient.revokeAccess();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        return root;
    }

    public void performLogin(final GoogleSignInAccount account){

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    final FirebaseUser user = mAuth.getCurrentUser();
                    String email = user.getEmail();
                    String name = user.getDisplayName();
                    final LoginViewModel loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
                    loginViewModel.setUser(user);
                    loginViewModel.setEmail(email);
                    loginViewModel.setName(name);
                    loginViewModel.setPhotoUrl(account.getPhotoUrl().toString());
                    final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    Log.e("New tag", "updated loginviewModel");
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(user.getUid()).get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                        @Override
                        public void onComplete(Task<DocumentSnapshot> task) {
                            Log.e("New tag", "onComplete");
                            if(task.isSuccessful()) {
                                Log.e("New tag", "task successful");
                                DocumentSnapshot document = task.getResult();
                                Map<String, Object> map =document.getData();
                                if(map == null){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setNeutralButton("Ok", null);

                                    builder.setMessage("Please Register First");
                                    AlertDialog dialogFragment = builder.create();
                                    dialogFragment.show();
                                    FirebaseAuth.getInstance().signOut();
                                    loginViewModel.setUser(null);

                                }else{
                                    Log.e("New tag", map.toString() +"wuzzle");
                                    String key = "favorites";
                                    ArrayList<String> favorites =(ArrayList<String>) map.get(key);
                                    Log.e("New tag", favorites.toString());
                                    setRestaurants(favorites);
                                    navController.navigate(R.id.action_global_to_nav_home);
                                }
                            }else{
                                Log.e("New Tag", "didn't complete");
                            }
                        }
                    });

                }else{
                    Log.e("My tag", "convert google login to firebase login failed");
                }

            }
        });



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                performLogin(account);

            }catch(ApiException e){
                Log.e("My tag", "signInResult:failed code= "+e.getStatusCode());
            }
        }
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

    }
}