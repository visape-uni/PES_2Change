package pes.twochange.presentation.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.AuthTheme;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.fragment.LoginFragment;
import pes.twochange.presentation.fragment.NewProfileFragment;
import pes.twochange.presentation.fragment.RegisterFragment;
import pes.twochange.services.Firebase;

public class AuthActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener, RegisterFragment.OnFragmentInteractionListener,
        NewProfileFragment.OnFragmentInteractionListener,
        AuthTheme.Response, GoogleApiClient.OnConnectionFailedListener {
    //Attributes
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private static final String TAG = "New User Fragment";
    private GoogleApiClient googleApiClient;
    private FragmentManager fragmentManager;
    private static Context context;
    private Fragment fragment;
    private AuthTheme authTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        fragmentManager = getSupportFragmentManager();
        context = getApplicationContext();

        authTheme = AuthTheme.getInstance();
        authTheme.setSharedPreferences(getSharedPreferences(Config.SP_NAME, MODE_PRIVATE));
        authTheme.setResponse(this);

        //Whether the user automatically logged in has a profile or not
        if (getIntent().getExtras().getString("startPoint").equals("LOGIN")) {
            fragment = LoginFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        } else if (getIntent().getExtras().getString("startPoint").equals("PROFILE")) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragment = LoginFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            fragment = NewProfileFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack("Set Profile")
                    .commit();
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
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragment = LoginFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        fragment = NewProfileFragment.newInstance();
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack("Set Profile")
                .commit();
    }
    @Override
    public void noConnection() {
        // TODO Control d'errors
        Toast.makeText(AuthActivity.this, "Unknown Error: Check your credentials", Toast.LENGTH_LONG).show();
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

    @Override
    public void onCreateUser(final String newMail, final String newPass) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(newMail, newPass).addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "User successfully signed up", Toast.LENGTH_LONG).show();
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragment = LoginFragment.newInstance();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .commit();
                    fragment = NewProfileFragment.newInstance();
                    fragmentManager.beginTransaction()
                            .add(R.id.container, fragment)
                            .addToBackStack("Set Profile")
                            .commit();
                } else {
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        EditText passText = (EditText)findViewById(R.id.passField);
                        EditText repePassText = (EditText)findViewById(R.id.repeatPassField);
                        Toast.makeText(AuthActivity.this, "Error: Password must be larger", Toast.LENGTH_LONG).show();
                        passText.setText("");
                        repePassText.setText("");
                        passText.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        EditText mailText = (EditText)findViewById(R.id.mailField);
                        Toast.makeText(AuthActivity.this, "Error: Invalid Email Format", Toast.LENGTH_LONG).show();
                        mailText.setText("");
                        mailText.requestFocus();
                    } catch(FirebaseAuthUserCollisionException e) {
                        EditText mailText = (EditText)findViewById(R.id.mailField);
                        Toast.makeText(AuthActivity.this, "Error: This Email is Already Registered", Toast.LENGTH_LONG).show();
                        mailText.setText("");
                        mailText.requestFocus();
                    } catch(Exception e) {
                        Toast.makeText(AuthActivity.this, "Internal error. Try later", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onSetProfileClick(final Profile profile) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("profile").child(profile.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getApplication(), "There is already a user with this username"
                            , Toast.LENGTH_LONG).show();
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    final String uid = mAuth.getCurrentUser().getUid();
                    profile.setUid(uid);
                    ProfileTheme.getInstance(profile).updateProfile(
                            new ProfileResponse() {
                                @Override
                                public void success(Profile profile) {
                                    SharedPreferences.Editor editor = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE).edit();
                                    editor.putString("username", profile.getUsername());
                                    editor.putString("uid", uid);
                                    editor.apply();
                                    startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
                                    finish();
                                }
                                @Override
                                public void failure(String s) {
                                    Toast.makeText(getApplication(), "Internal Error", Toast.LENGTH_LONG).show();
                                }
                            }
                    );

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static Context getContext(){
        return context;
    }
}