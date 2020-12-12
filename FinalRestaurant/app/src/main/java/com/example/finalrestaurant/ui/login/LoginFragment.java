package com.example.finalrestaurant.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// fragment for login page. App starts here. Redirects to homefragment or register fragment
public class LoginFragment extends Fragment {

    private Button buttonRegister;
    private Button buttonLogin;
    private int RC_SIGN_IN = 0;

    //------------------------------------------------------------------------------------------
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.e("My_tag","login view started");
        final View root = inflater.inflate(R.layout.fragment_login, container, false);
        //toggles title bar
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOff();
        // forces redirect if already logged in
        LoginViewModel loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        if(loginViewModel.getUser().getValue() != null){
            NavDirections action = MobileNavigationDirections.actionGlobalToNavHome();
            Navigation.findNavController(root).navigate(action);
            return root;
        }
        //starts sign in process on login button
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
        //redirect for register
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = LoginFragmentDirections.actionNavLoginToNavRegister();
                Navigation.findNavController(view).navigate(action);
            }
        });
        // signs out if user logs out
        loginViewModel.getUser().observe(getActivity(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser == null){
                    signInClient.revokeAccess();
                }
            }
        });
        return root;
    }

    //------------------------------------------------------------------------------------------
    // performs login for the app by signing in with firebase and storing user info in the loginViewModel
    public void performLogin(final GoogleSignInAccount account){
        //starts login process
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //updates view model
                    final FirebaseUser user = mAuth.getCurrentUser();
                    String email = user.getEmail();
                    String name = user.getDisplayName();
                    final LoginViewModel loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
                    loginViewModel.setUser(user);
                    loginViewModel.setEmail(email);
                    loginViewModel.setName(name);
                    loginViewModel.setPhotoUrl(account.getPhotoUrl().toString());
                    // gets the nav controller for the call backs when we send or receive stuff from firestore
                    final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    Log.e("New tag", "updated loginviewModel");
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    //checks current entry in firestore
                    db.collection("users").document(user.getUid()).get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                        @Override
                        public void onComplete(Task<DocumentSnapshot> task) {
                            Log.e("New tag", "onComplete");
                            if(task.isSuccessful()) {
                                Log.e("New tag", "task successful");
                                DocumentSnapshot document = task.getResult();
                                Map<String, Object> map =document.getData();
                                //if nothing found in firestore, alert tells user to register
                                if(map == null){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setNeutralButton("Ok", null);
                                    builder.setMessage("Please Register First");
                                    AlertDialog dialogFragment = builder.create();
                                    dialogFragment.show();
                                    FirebaseAuth.getInstance().signOut();
                                    loginViewModel.setUser(null);
                                }else{
                                    //starts to load favorites into view model, then navigates to home page
                                    Log.e("New tag", map.toString() +"wuzzle");
                                    String key = "favorites";
                                    ArrayList<String> favorites =(ArrayList<String>) map.get(key);
                                    Log.e("New tag", favorites.toString());
                                    if(favorites.size() == 0){
                                        setEmptyList();
                                    }else{
                                        setRestaurants(favorites);
                                    }
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
    //------------------------------------------------------------------------------------------
    //listener for google login activity
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
    //------------------------------------------------------------------------------------------
    public void setRestaurants(final ArrayList<String> keys) throws InvalidParameterException {
        // once given a set of keys from fire store, retrieves a list of restaurants from yelp.
        //only updates the viewmodel if all restaurants are collected
        if(keys.size() == 0){
            throw new InvalidParameterException("Parameter of zero given to setRestaurants");
            // key list of zero size should be handled before calling this method
        }
        final ArrayList<Restaurant> restaurants = new ArrayList<>();
        final HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        homeViewModel.setFavoritesList(keys);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // makes requests from yelp for each entry in the favorites list
        for(int i = 0; i < keys.size(); i++){
            String url = "https://api.yelp.com/v3/businesses/" + keys.get(i);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Restaurant restaurant = gson.fromJson(response.toString(), Restaurant.class);
                    //adds restaurant to restaurant list
                    restaurants.add(restaurant);
                    if( restaurants.size()== keys.size()){
                        //updates view model if all keys processed
                        Collections.sort(restaurants, new Comparator<Restaurant>() {
                            @Override
                            public int compare(Restaurant restaurant, Restaurant t1) {
                                return restaurant.getName().compareTo(t1.getName());
                            }
                        });
                        homeViewModel.setEmpty(false);
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
    //------------------------------------------------------------------------------------------
    //sets view model to empty lists
    public void setEmptyList(){
        HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        homeViewModel.setFavoritesList(new ArrayList<String>());
        homeViewModel.setRestaurants(new ArrayList<Restaurant>());
        homeViewModel.setEmpty(true);
    }
}