package pes.twochange.presentation.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Product;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.domain.themes.SettingsTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.fragment.EditProfileFragment;
import pes.twochange.presentation.fragment.ProductsListFragment;
import pes.twochange.presentation.fragment.WantedProductsListFragment;
import pes.twochange.services.DatabaseResponse;

public class ProfileActivity extends BaseActivity implements AdTheme.ErrorResponse, WantedProductsListFragment.OnFragmentInteractionListener{

    private String usernameProfile;
    private String currentUsername;

    private int numWanted;
    private int numOffered;

    private Profile profile;
    private Fragment fragment;

    private ArrayList<Product> wantedList;
    private ArrayList<Product> offeredList;

    private static final int WANTED = 1;
    private static final int OFFERED = 2;
    private static final int EDIT = 3;

    private boolean rated;
    RatingBar ratingBar;

    private int currentFragment;

    private static final String TAG = "ProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        if (getIntent().getStringExtra("usernameProfile") == null) usernameProfile = currentUsername;
        else usernameProfile = getIntent().getStringExtra("usernameProfile");

        fragment = ProductsListFragment.newInstance();
        displayFragment(R.id.contentProfile, fragment, "offered");
        currentFragment = OFFERED;

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
                new AdTheme.ProductListResponse() {
                    @Override
                    public void listResponse(ArrayList<Product> wantedItems) {
                        wantedList = wantedItems;
                        numWanted = wantedList.size();
                        setUpWanted();
                    }
                }, this
        );

        AdTheme.getInstance().getOfferedList(
                usernameProfile,
                new AdTheme.ProductListResponse() {
                    @Override
                    public void listResponse(ArrayList<Product> offeredItems) {
                        offeredList = offeredItems;
                        numOffered = offeredList.size();
                        setUpOffered();
                    }
                }, this
        );

        ratingBar = (RatingBar) findViewById(R.id.userRatingBar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ratingBar.isIndicator()) {
                    if (currentUsername.equals(usernameProfile)) Toast.makeText(ProfileActivity.this, "You can't rate your own profile", Toast.LENGTH_LONG).show();
                    else Toast.makeText(ProfileActivity.this, "You have already rated this user", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                ProfileTheme.getInstance(profile).rate(rating, currentUsername);
                setUpProfile();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (usernameProfile.equals(currentUsername)) getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        else getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editar_perfil:
                Bundle bundle = new Bundle();
                bundle.putString("usernameProfile", usernameProfile);
                fragment = EditProfileFragment.newInstance();
                fragment.setArguments(bundle);
                displayFragment(R.id.contentProfile, fragment, "edit");
                currentFragment = EDIT;
                return true;
            case R.id.action_desactivar:
                //TODO: desactivar notificaciones
                return true;
            case R.id.action_block:
                SettingsTheme.getInstance(currentUsername).blockUser(profile.getUsername());
                return true;
            case R.id.action_open_chat:
                Intent chatIntent = new Intent(this,ChatActivity.class);
                Chat chat = new Chat(currentUsername,profile.getUsername());
                chatIntent.putExtra("chat",chat);
                startActivity(chatIntent);
                return true;
            default:  return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.offeredTab:
                fragment = ProductsListFragment.newInstance();
                displayFragment(R.id.contentProfile, fragment, "offered");
                currentFragment = OFFERED;
                AdTheme.getInstance().getOfferedList(
                usernameProfile,
                        new AdTheme.ProductListResponse() {
                            @Override
                            public void listResponse(ArrayList<Product> offeredItems) {
                                offeredList = offeredItems;
                                numOffered = offeredList.size();
                                setUpOffered();
                            }
                        }, this
                );
                break;
            case R.id.wantedTab:
                fragment = WantedProductsListFragment.newInstance();
                displayFragment(R.id.contentProfile, fragment, "wanted");
                currentFragment = WANTED;
                AdTheme.getInstance().getWantedList(
                        usernameProfile,
                        new AdTheme.ProductListResponse() {
                            @Override
                            public void listResponse(ArrayList<Product> wantedItems) {
                                wantedList = wantedItems;
                                numWanted = wantedList.size();
                                setUpWanted();
                            }
                        }, this
                );
                break;
        }
    }

    protected int currentMenuItemIndex() {
        return PROFILE_ACTIVITY;
    }

    DatabaseResponse rateCallback = new DatabaseResponse() {
        @Override
        public void success(DataSnapshot dataSnapshot) {
            rated = true;
            ratingBar.setIsIndicator(true);
        }

        @Override
        public void empty() {
            rated = false;
            ratingBar.setIsIndicator(false);
        }

        @Override
        public void failure(String message) {
            Log.d(TAG, message);
        }
    };

    private void setUpProfile() {
        // TODO cargar imagen perfil

        TextView usernameTextView = (TextView) findViewById(R.id.usernameTxt);
        TextView nameTextView = (TextView) findViewById(R.id.nameTxt);
        TextView numRates = (TextView) findViewById(R.id.ratesNum);
        TextView rate = (TextView) findViewById(R.id.rate);

        if (currentUsername.equals(usernameProfile)) {
            rated = true;
            ratingBar.setIsIndicator(true);
        } else {
            ProfileTheme.getInstance(profile).isRated(currentUsername, rateCallback);
        }

        usernameTextView.setText(profile.getUsername().toUpperCase());
        nameTextView.setText(profile.fullName());
        if (numRates.equals(0)) rate.setText(String.valueOf(0));
        else rate.setText(new DecimalFormat("##.##").format(profile.getRate()));
        numRates.setText(String.valueOf(profile.getNumRates()));

    }

    private void setUpWanted () {
        TextView numWantedTextView = (TextView) findViewById(R.id.wantedNum);

        numWantedTextView.setText(String.valueOf(numWanted));
        if (currentFragment == WANTED) ((WantedProductsListFragment) fragment).display(wantedList);
    }

    private void setUpOffered () {
        TextView numOfferedTextView = (TextView) findViewById(R.id.offeredNum);

        numOfferedTextView.setText(String.valueOf(numOffered));
        if (currentFragment == OFFERED)((ProductsListFragment) fragment).display(offeredList);
    }

    public void update(Profile pro) {
        profile = pro;
        setUpProfile();
        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
    }

    @Override
    public void error(String error) {
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
}
