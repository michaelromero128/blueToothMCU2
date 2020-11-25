package com.example.finalrestaurant.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.finalrestaurant.MainActivityViewModel;
import com.example.finalrestaurant.R;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private MainActivityViewModel mainActivityViewModel;
    Button buttonRegister;
    Button buttonLogin;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loginViewModel =
                ViewModelProviders.of(this).get(LoginViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_login, container, false);
        final TextView textView = root.findViewById(R.id.text_home);


        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        //mainActivityViewModel.turnOff();
        mainActivityViewModel.turnOn();
        buttonLogin = (Button) root.findViewById(R.id.buttonLogin);
        buttonRegister= (Button) root.findViewById(R.id.buttonRegister);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).navigate(R.id.action_global_to_nav_home);
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = LoginFragmentDirections.actionNavLoginToNavRegister();
               Navigation.findNavController(root).navigate(action);
            }
        });


        return root;
    }
}