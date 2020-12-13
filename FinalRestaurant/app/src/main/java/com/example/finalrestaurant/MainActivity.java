package com.example.finalrestaurant;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finalrestaurant.models.Restaurant;
import com.example.finalrestaurant.ui.home.HomeViewModel;
import com.example.finalrestaurant.ui.login.LoginViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// besides being the activity of the app, this houses the logic for the title bar and the drawer
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get gps permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);

        //sets up toolbar and drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu -D as a set of Ids because each
        // menu should be considered as top level destinations.
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.nav_login);
        topLevelDestinations.add(R.id.nav_home);
        topLevelDestinations.add(R.id.nav_search_entry);
        mAppBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .setDrawerLayout(drawer)
                .build();
        //magic to hook up drawer to navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // various listeners for toggling the title bar or user profile information
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.getMenuSetting().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean.booleanValue() == true) {
                    getSupportActionBar().show();
                } else {
                   getSupportActionBar().hide();
                }

            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView textView = (TextView) findViewById(R.id.textView_nav_Name);
                if(textView == null){
                    return;
                }
                textView.setText(s);
            }
        });
        loginViewModel.getEmail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                TextView textView = (TextView) findViewById(R.id.textView_nav_email);
                if(textView == null){
                    return;
                }
                textView.setText(s);
            }
        });
        loginViewModel.getPhotoUrl().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ImageView imageView = findViewById(R.id.imageView);

                if(s == null || imageView == null){
                    return;
                }else {
                    // load image in seperate thread
                    final String params = s;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                InputStream inputStream = new java.net.URL(params).openStream();
                                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ImageView imageView = findViewById(R.id.imageView);
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });
                                Log.e("My tag", "Runnable finished");
                            } catch (Exception e) {
                                Log.e("My tag", "failure on runnable");
                                Log.e("My tag", e.getMessage());
                            }

                        }
                    });
                    thread.start();
                }

            }
        });


    }
    // I don't know, something about the back button on the drawer
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // needed to add logout logic so had to manually do onNavigationItemSelected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        switch(item.getItemId()){
            case R.id.action_global_to_nav_login:
                LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
                HomeViewModel homeViewModel = new ViewModelProvider((this)).get(HomeViewModel.class);
                loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
                FirebaseAuth.getInstance().signOut();
                loginViewModel.setUser(null);
                loginViewModel.setName("");
                loginViewModel.setEmail("");
                loginViewModel.setPhotoUrl("");
                homeViewModel.setFavoritesList(new ArrayList<String>());
                homeViewModel.setRestaurants(new ArrayList<Restaurant>());
                navController.navigate(R.id.action_global_to_nav_login);
                break;
            case R.id.action_global_to_nav_home:
                navController.navigate(R.id.action_global_to_nav_home);
                break;
            case R.id.action_global_to_nav_search_entry:
                navController.navigate(R.id.action_global_to_nav_search_entry);
                break;
        }
        drawer.closeDrawers();
        return true;

    }





}