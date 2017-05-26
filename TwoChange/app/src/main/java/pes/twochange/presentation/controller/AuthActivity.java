package pes.twochange.presentation.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import pes.twochange.R;
import pes.twochange.domain.themes.AuthTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.activity.EditProfileActivity;
import pes.twochange.presentation.fragment.LoginFragment;
import pes.twochange.presentation.fragment.RegisterFragment;

public class AuthActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener, RegisterFragment.OnFragmentInteractionListener,
        AuthTheme.Response, GoogleApiClient.OnConnectionFailedListener {
    //Attributes
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient googleApiClient;
    private FragmentManager fragmentManager;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        fragmentManager = getSupportFragmentManager();

        //Whether the user automatically logged in has a profile or not
        if (getIntent().getExtras().getString("startPoint").equals("LOGIN")) {
            fragment = LoginFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        } else if (getIntent().getExtras().getString("startPoint").equals("PROFILE")) {
            /*fragment = ProfileFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();*/
            Toast.makeText(getApplicationContext(), "Profiles Fragment", Toast.LENGTH_LONG).show();
        }
        //Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void addFragment(String tag) {
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(tag)
                .commit();
    }

    //Method called when the user presses the normal login button
    @Override
    public void onLoginClick(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You must fill in both fields to log in", Toast.LENGTH_LONG).show();
        } else if (!email.contains("@")) {
            Toast.makeText(getApplicationContext(), "Email with incorrect format", Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You must fill in both fields to log in", Toast.LENGTH_LONG).show();
        } else {
            AuthTheme authTheme;
            authTheme = AuthTheme.getInstance();
            authTheme.setSharedPreferences(getSharedPreferences(Config.SP_NAME, MODE_PRIVATE));
            authTheme.setResponse(this);
            authTheme.login(email, password);
        }
    }

    //These 3 methods are from the AuthTeme.response interface. They capture whether the loged in
    // user has an attached user profile or not
    @Override
    public void main() {
        startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
        finish();
    }
    @Override
    public void profile() {
        /*fragment = SignupFragment.newInstance();
        addFragment("Sign Up");*/
        Toast.makeText(getApplicationContext(), "Hej pussies I'm here!", Toast.LENGTH_LONG).show();
    }
    @Override
    public void noConnection() {
        // TODO Control d'errors
    }

    //Register button pressed listener
    @Override
    public void onRegisterClick() {
        fragment = RegisterFragment.newInstance();
        addFragment("Register User");
    }

    //Login with Google button pressed listener
    @Override
    public void onLoginWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                AuthTheme.getInstance().login(result.getSignInAccount());
            } else {
                // TODO Control d'errors
            }
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
