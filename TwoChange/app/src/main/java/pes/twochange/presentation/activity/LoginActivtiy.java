package pes.twochange.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import pes.twochange.R;
import pes.twochange.domain.controller.UserController;
import pes.twochange.domain.model.User;

public class LoginActivtiy extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivtiy";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;

    private UserController userController;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userController = UserController.getInstance(this);

        //Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();

        //Mirar si ya esta logeado
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Logeado
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    Intent mainMenuIntent = new Intent (getApplicationContext(), zWorking.class);
                    startActivity(mainMenuIntent);
                } else {
                    //No logeado
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //Login button + Pressed button listener
        Button loginBtn = (Button) findViewById(R.id.logInBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                EditText user = (EditText)findViewById(R.id.mailField);
                EditText pass = (EditText)findViewById(R.id.passwordField);
                String email = user.getText().toString().trim();
                String password = pass.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill in the Email field", Toast.LENGTH_LONG).show();
                } else if (!email.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Incorrect Email format", Toast.LENGTH_LONG).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill in the password field", Toast.LENGTH_LONG).show();
                } else {
                    //logInEmail(email, password);
                    userController.login(
                            email,
                            password,
                            new UserController.OnLogin() {
                                @Override
                                public void onLoginSuccess(User user) {
                                    // Save user and session to the Shared Preferences Storage
                                    Toast.makeText(LoginActivtiy.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onLoginFailure(String message) {
                                    Log.v("ERROR", "Error al hacer login con Firebase: " + message);
                                }
                            }
                    );
                }
            }
        });

        //Google button + Pressed button listener
        Button googleBtn = (Button)findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                logInGoogle();
            }
        });

        //New user button + Pressed button listener
        Button newUserBtn = (Button)findViewById(R.id.newUserBtn);
        newUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent mainMenuIntent = new Intent (getApplicationContext(), RegisterActivity.class);
                startActivity(mainMenuIntent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void logInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //Logeado
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                //Login failed
            }
        }
    }

    private void firebaseAuthWithGoogle (GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //Mostrar pantalla de loading???

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential:failed", task.getException());
                            Context context = getApplicationContext();
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                        //Esconder pantalla de loading???

                    }
                });

    }

    public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_LONG).show();
    }
}
