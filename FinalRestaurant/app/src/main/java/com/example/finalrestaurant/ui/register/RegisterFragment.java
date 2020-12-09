package com.example.finalrestaurant.ui.register;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private int RC_SIGN_IN = 0;
    private RegisterViewModel registerViewModel;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registerViewModel =
                ViewModelProviders.of(this).get(RegisterViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_register, container, false);
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
        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.turnOff();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        mAuth = FirebaseAuth.getInstance();
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
                    homeViewModel.setRestaurants(new ArrayList<Restaurant>());
                    homeViewModel.setFavoritesList(new ArrayList<String>());

                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String,Object> data = new HashMap<>();
                    final ArrayList<String> list = new ArrayList<>();
                    data.put("favorites",list);
                    db.collection("users").document(user.getUid()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_global_to_nav_home);

                        }
                    });
                } else {
                }
            }
        });
    }
}