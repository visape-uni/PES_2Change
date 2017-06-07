package pes.twochange.presentation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.controller.ExploreActivity;

public class EditProfileActivity extends AppCompatActivity {
    //Attributes
    private Boolean editing;
    private String uid;
    private String username;
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
        //setContentView(R.layout.activity_new_profile);

        editing = getIntent().getBooleanExtra("editing", false);

        TextInputLayout usernameInputLayout = (TextInputLayout) findViewById(R.id.usernameInputLayout);
        usernameText = (EditText)findViewById(R.id.usernameField);
        nameText = (EditText)findViewById(R.id.nameField);
        surnameText = (EditText)findViewById(R.id.surnameField);
        phoneText = (EditText)findViewById(R.id.phoneField);
        addressText = (EditText)findViewById(R.id.addressField);
        cityText = (EditText)findViewById(R.id.cityField);
        zipText = (EditText)findViewById(R.id.zipField);
        stateText = (EditText)findViewById(R.id.stateField);
        countryText = (EditText)findViewById(R.id.countryField);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        if (editing) {
            username = sharedPreferences.getString("username", null);
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
                        Profile profile = getCompleteProfile();
                        profile.setUid(uid);
                        profile.setUsername(username);
                        updateProfile(profile);
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
            ProfileTheme.getInstance().get(
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
                                updateProfile(getCompleteProfile());
                            }
                        }
                    }
            );
        }
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
                null, //The uid is assigned to the profile on the AuthActivity
                name,
                surname,
                new Profile.PhoneNumber(null, phone),
                new Profile.Address(addressStr, zip, city, state, country)
        );
    }

    private void updateProfile(Profile profile) {
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
                    }
                }
        );


    }
}
