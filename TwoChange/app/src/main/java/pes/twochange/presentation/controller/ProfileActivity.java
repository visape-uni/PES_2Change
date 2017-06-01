package pes.twochange.presentation.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;

public class ProfileActivity extends BaseActivity {

    private String usernameProfile;
    private String currentUsername;

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        /*usernameProfile = getIntent().getExtras().getString("usernameProfile");


        if (currentUsername.equals(usernameProfile)) toolbar.setTitle("My profile"); //Mi perfil
        else toolbar.setTitle("User profile"); //Perfil de otro usuario
        */
        ProfileTheme.getInstance().get(
                currentUsername /*usernameProfile*/,
                new ProfileResponse() {
                    @Override
                    public void success(Profile p) {
                        profile = p;
                        setUpProfile();
                    }

                    @Override
                    public void failure(String s) {
                        // TODO Control d'errors
                    }
                }
        );

    }

    protected int currentMenuItemIndex() {
        return PROFILE_ACTIVITY;
    }

    private void setUpProfile() {
        // TODO imagen perfil

        TextView usernameTextView = (TextView) findViewById(R.id.usernameTxt);
        TextView nameTextView = (TextView) findViewById(R.id.nameTxt);
        RatingBar userRatingBar = (RatingBar) findViewById(R.id.userRatingBar);

        usernameTextView.setText(profile.getUsername().toUpperCase());
        nameTextView.setText(profile.fullName());
        userRatingBar.setRating(profile.getRate());
    }

    /*private void setUpProfile() {
        // TODO imagen de perfil & image view
        fullNameTextView.setText(profile.fullName().toUpperCase());
        usernameTextView.setText(username);

        if (profile.getPhoneNumber() != null) {
            phoneTextView.setText(profile.getPhoneNumber().getNumber());
        } else {
            phoneTextView.setText("No phone number provided");
        }

        if (profile.getAddress() != null) {
            addressTextView.setText(profile.getAddress().toString());
        } else {
            addressTextView.setText("No address provided");
        }

        if (selfProfile) {
            editProfileButton.setVisibility(View.VISIBLE);
            editProfileButton.setOnClickListener(this);
            blockButton.setVisibility(View.GONE);
        } else {
            chatButton.setVisibility(View.VISIBLE);
            chatButton.setOnClickListener(this);
            blockButton.setVisibility(View.VISIBLE);
        }
        loadingProgressBar.setVisibility(View.GONE);
    }*/
}
