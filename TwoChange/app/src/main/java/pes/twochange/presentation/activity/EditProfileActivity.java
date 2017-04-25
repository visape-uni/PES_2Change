package pes.twochange.presentation.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.ModelAdapter;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;
import pes.twochange.services.Firebase;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Boolean editing;

    private String uid;

    private String email;
    private String password;

    private EditText usernameText;
    private EditText nameText;
    private EditText surnameText;
    private EditText phoneText;
    private EditText addressText;
    private EditText cityText;
    private EditText zipText;
    private EditText stateText;
    private EditText countryText;

    //Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        editing = getIntent().getBooleanExtra("editing", false);

        TextInputLayout usernameInputLayout = (TextInputLayout) findViewById(R.id.usernameInputLayout);
        usernameText = (EditText)findViewById(R.id.userNameField);
        nameText = (EditText)findViewById(R.id.nameField);
        surnameText = (EditText)findViewById(R.id.surnameField);
        phoneText = (EditText)findViewById(R.id.phoneField);
        addressText = (EditText)findViewById(R.id.addressField);
        cityText = (EditText)findViewById(R.id.cityField);
        zipText = (EditText)findViewById(R.id.zipField);
        stateText = (EditText)findViewById(R.id.stateField);
        countryText = (EditText)findViewById(R.id.countryField);

        if (!editing) {
            email = getIntent().getExtras().getString("email", null);
            password = getIntent().getExtras().getString("password", null);
            uid = null;
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
            uid = sharedPreferences.getString("uid", null);
            usernameInputLayout.setVisibility(View.GONE);
            usernameText.setVisibility(View.GONE);
        }

        //Next Step button + Pressed button listener
        Button nextBtn = (Button)findViewById(R.id.FinishBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                final String name = nameText.getText().toString().trim();
                final String surname = surnameText.getText().toString().trim();

                if (name.isEmpty() || nameText.getText() == null) {
                    // TODO Control d'errors
                } else if (surname.isEmpty() || surnameText.getText() == null) {
                    // TODO Control d'errors
                } else {
                    if (editing) {
                        updateProfile(getCompleteProfile());
                        // TODO update feedback to user
                    } else {
                        checkAvailability();
                    }
                }
            }
        });
    }

    private void checkAvailability() {
        String username = usernameText.getText().toString();
        if (username.isEmpty()) {
            // TODO Control d'errors
        } else {
            new ProfileTheme().get(
                    username,
                    new ProfileResponse() {

                        @Override
                        public void success(Profile profile) {
                            // TODO Control d'errors
                        }

                        @Override
                        public void failure(String s) {
                            if (s.equals("Something went wrong :(")) {
                                // TODO Control d'errors
                            } else {
                                createUserAndProfile();
                            }
                        }
                    }
            );
        }
    }

    private void createUserAndProfile() {
        final Profile profile = getCompleteProfile();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            profile.setUid(uid);
                            updateProfile(profile);
                            login(email, password, profile.getUsername());
                        }
                    }
                }
        );
    }

    private Profile getCompleteProfile() {
        String username = usernameText.getText().toString().trim();
        String name = nameText.getText().toString().trim();
        String surname = surnameText.getText().toString().trim();
        String phone = phoneText.getText().toString().trim();
        String addressStr = addressText.getText().toString().trim();
        String zip = zipText.getText().toString().trim();
        String city = cityText.getText().toString().trim();
        String state = stateText.getText().toString().trim();
        String country = countryText.getText().toString().trim();

        return new Profile(
                username,
                uid,
                name,
                surname,
                new Profile.PhoneNumber(null, phone),
                new Profile.Address(addressStr, zip, city, state, country)
        );
    }

    private void updateProfile(final Profile profile) {
        Firebase.getInstance().insert(
                "profile",
                new ModelAdapter<Profile>() {
                    @Override
                    public Class classType() {
                        return Profile.class;
                    }

                    @Override
                    public Profile object() {
                        return profile;
                    }
                }
        );


    }

    private void login(String email, String password, final String username) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SharedPreferences.Editor editor = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE).edit();
                    editor.putString("username", username);
                    editor.putString("uid", firebaseAuth.getCurrentUser().getUid());
                    editor.apply();
                    // TODO go to main menu!
                } else {
                    // TODO Control d'errors
                }
            }
        });
    }
}
