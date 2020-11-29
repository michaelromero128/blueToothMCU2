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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.finalrestaurant.MainActivityViewModel;
import com.example.finalrestaurant.MobileNavigationDirections;
import com.example.finalrestaurant.R;
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
        final TextView textView = root.findViewById(R.id.text_home);
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
                    FirebaseUser user = mAuth.getCurrentUser();
                    String email = user.getEmail();
                    String name = user.getDisplayName();
                    LoginViewModel loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
                    loginViewModel.setUser(user);
                    loginViewModel.setEmail(email);
                    loginViewModel.setName(name);
                    loginViewModel.setPhotoUrl(account.getPhotoUrl().toString());
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    Log.e("My tag", "updated loginviewModel");
                    navController.navigate(R.id.action_global_to_nav_home);
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
}