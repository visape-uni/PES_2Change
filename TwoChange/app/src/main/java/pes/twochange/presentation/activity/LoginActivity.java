package pes.twochange.presentation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {

    // region Attributes
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;
    // endregion

    // region Activity workflow
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        //Login button + Pressed button listener
        Button loginButton = (Button) findViewById(R.id.logInBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                login();
            }
        });

        //Google button + Pressed button listener
        Button googleBtn = (Button)findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                loginWithGoogle();
            }
        });

        //New user button + Pressed button listener
        Button newUserBtn = (Button)findViewById(R.id.newUserBtn);
        newUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent (getApplicationContext(), NewUserActivity.class));
            }
        });

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

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(this);
        }
    }
    // endregion



    // Check if it is already logged in
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            doLogin(uid);
        }
    }

    @Override
    public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {
        // TODO: Control d'errors
    }


    private void login() {
        EditText emailField = (EditText) findViewById(R.id.mailField);
        EditText passwordField = (EditText) findViewById(R.id.passwordField);
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if (email.isEmpty()) {
            // TODO: Control d'errors
            //toast("Fill in the Email field");
        } else if (!email.contains("@")) {
            // TODO: Control d'errors
            //toast("Incorrect Email format");
        } else if (password.isEmpty()) {
            // TODO: Control d'errors
            //toast("Fill in the password field");
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        doLogin(firebaseAuth.getCurrentUser().getUid());
                    } else {
                        // TODO: Control d'errors
                    }
                }
            });
        }
    }



    private void loginWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // TODO: Control d'errors
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            doLogin(uid);
                        } else {
                            // TODO: Control d'errors
                        }
                    }
                });
    }



    private void doLogin(String uid) {
        SharedPreferences.Editor editor = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE).edit();
        editor.putString("uid", uid).apply();
        new ProfileTheme().find(
                uid,
                new ProfileResponse() {
                    @Override
                    public void success(Profile profile) {
                        startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                        finish();
                    }

                    @Override
                    public void failure(String s) {
                        if (s.equals("Cannot find any profile")) {
                            // TODO: Crea perfil
                        } else {
                            // TODO: Control d'errors
                        }
                    }
                }
        );
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
