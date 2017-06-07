package pes.twochange.domain.themes;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Profile;
import pes.twochange.presentation.controller.AuthActivity;

public class AuthTheme implements FirebaseAuth.AuthStateListener, ProfileResponse, OnCompleteListener<AuthResult> {

    private static AuthTheme instance = new AuthTheme();
    private SharedPreferences sharedPreferences;
    private Response response;
    private String uid;

    public static AuthTheme getInstance() {
        return instance;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void startListeningFirebaseAuth() {
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            ProfileTheme.getInstance().find(uid, this);
        }
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }


    @Override
    public void success(Profile profile) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", profile.getUsername());
        editor.putString("uid", uid);
        editor.apply();
        response.main();
    }

    @Override
    public void failure(String s) {
        if (s.equals("Cannot find any profile")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("uid", uid);
            editor.apply();
            response.profile();
        } else {
            response.noConnection();
        }
    }


    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            uid = task.getResult().getUser().getUid();
            ProfileTheme.getInstance().find(uid, this);
        } else {
            Toast.makeText(AuthActivity.getContext(), "Wrong mail or password", Toast.LENGTH_LONG).show();
        }
    }


    public void login(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this);
    }

    public void login(GoogleSignInAccount account) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this);
    }

    public interface Response {
        void main();
        void profile();
        void noConnection();
    }




}
