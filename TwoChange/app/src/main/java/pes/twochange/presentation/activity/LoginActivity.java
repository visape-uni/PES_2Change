package pes.twochange.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import pes.twochange.R;
import pes.twochange.domain.themes.AuthTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.controller.ExploreActivity;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, AuthTheme.Response {

    // region Attributes
    private static final int RC_SIGN_IN = 9001;
    //private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;
    private String email;
    private String password;
    private AuthTheme authTheme;
    // endregion

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authTheme = AuthTheme.getInstance();
        authTheme.setSharedPreferences(getSharedPreferences(Config.SP_NAME, MODE_PRIVATE));
        authTheme.setResponse(this);

        //Login button + Pressed button listener
        Button loginButton = (Button) findViewById(R.id.logInBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                loginWithEmail();
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

    private void loginWithEmail() {
        EditText emailField = (EditText) findViewById(R.id.mailField);
        EditText passwordField = (EditText) findViewById(R.id.passwordField);
        email = emailField.getText().toString().trim();
        password = passwordField.getText().toString().trim();
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

    private void loginWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                authTheme.login(result.getSignInAccount());
            } else {
                // TODO Control d'errors
            }
        }
    }

    @Override
    public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {
        noConnection();
    }


    @Override
    public void main() {
        startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
        finish();
    }

    @Override
    public void profile() {
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        intent.putExtra("editing", false);
        startActivity(intent);
        finish();
    }

    @Override
    public void noConnection() {
        // TODO Control d'errors
    }
}
