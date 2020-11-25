package com.example.finalrestaurant.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.finalrestaurant.MainActivityViewModel;
import com.example.finalrestaurant.R;
import com.example.finalrestaurant.ui.login.LoginViewModel;

import java.util.HashSet;
import java.util.Set;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivityViewModel loginViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        loginViewModel.turnOn();

        return root;
    }

}