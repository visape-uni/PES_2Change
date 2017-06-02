package pes.twochange.presentation.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.fragment.WantedProductsListFragment;

public class ProfileActivity extends BaseActivity implements AdTheme.ErrorResponse, WantedProductsListFragment.OnFragmentInteractionListener{

    private String usernameProfile;
    private String currentUsername;

    private int numWanted;
    private int numOffered;

    private Profile profile;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        if (getIntent().getStringExtra("usernameProfile") == null) usernameProfile = currentUsername;
        else usernameProfile = getIntent().getStringExtra("usernameProfile");

        ProfileTheme.getInstance().get(
                usernameProfile,
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

        AdTheme.getInstance().getWantedList(
                usernameProfile,
                new AdTheme.ListResponse() {
                    @Override
                    public void listResponse(ArrayList<Ad> wantedItems) {
                        numWanted = wantedItems.size();
                        setUpWanted();
                        //TODO: si currentfragment es wanted mostrar llista de wanted
                    }
                }, this
        );

        AdTheme.getInstance().getOfferedList(
                usernameProfile,
                new AdTheme.ListResponse() {
                    @Override
                    public void listResponse(ArrayList<Ad> offeredItems) {
                        numOffered = offeredItems.size();
                        setUpOffered();
                        //TODO: si currentfragment es offered mostrar llista de offered
                    }
                }, this
        );

        fragment = WantedProductsListFragment.newInstance();
        displayFragment(R.id.content, fragment);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (usernameProfile.equals(currentUsername)) getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        else getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
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

    private void setUpWanted () {
        TextView numWantedTextView = (TextView) findViewById(R.id.wantedNum);

        numWantedTextView.setText(String.valueOf(numWanted));
    }

    private void setUpOffered () {
        TextView numOfferedTextView = (TextView) findViewById(R.id.offeredNum);

        numOfferedTextView.setText(String.valueOf(numOffered));
    }

    @Override
    public void error(String error) {
        // TODO
    }

    @Override
    public void onRecyclerViewItemClickListener(int position) {

    }

    @Override
    public boolean onRecyclerViewItemLongClickListener(int position) {
        return false;
    }

    @Override
    public void loadProductList() {

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
