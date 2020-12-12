package com.example.finalrestaurant.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;


// ViewModel used to  store user information
public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> email;
    private MutableLiveData<String> name;
    private MutableLiveData<FirebaseUser> user;
    private MutableLiveData<String> photoUrl;

    // constructor
    public LoginViewModel() {
        email = new MutableLiveData<>();
        email.setValue(null);
        name = new MutableLiveData<>();
        name.setValue(null);
        user = new MutableLiveData<>();
        user.setValue(null);
        photoUrl = new MutableLiveData<String>();
        photoUrl.setValue(null);
    }
    // setters and getters for the member variables
    public LiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public LiveData<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user.setValue(user);
    }
    public LiveData<String> getPhotoUrl(){return photoUrl;}
    public void setPhotoUrl(String photoUrl){
        this.photoUrl.setValue(photoUrl);
    }
}