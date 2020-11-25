package com.example.finalrestaurant.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.finalrestaurant.MainActivityViewModel;
import com.example.finalrestaurant.R;

public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registerViewModel =
                ViewModelProviders.of(this).get(RegisterViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_register, container, false);
        Button buttonAcceptRegister = (Button) root.findViewById(R.id.buttonApproveRegister);
        Button buttonDeclineRegister = (Button) root.findViewById(R.id.buttonRegisterDecline);
        buttonAcceptRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_global_to_nav_login);
            }
        });
        buttonDeclineRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_global_to_nav_login);
            }
        });
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOff();
        return root;
    }
}