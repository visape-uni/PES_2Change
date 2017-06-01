package pes.twochange.presentation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.domain.themes.SettingsTheme;
import pes.twochange.presentation.Config;
import pes.twochange.services.Firebase;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView fullNameTextView;
    private TextView usernameTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private Button editProfileButton;
    private Button chatButton;
    private Button blockButton;
    private ProgressBar loadingProgressBar;

    private String username;
    private Profile profile;

    private Boolean selfProfile;

    private static DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://change-64bd0.firebaseio.com/").child("users_blocked");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = getIntent().getStringExtra("username");
        selfProfile = username == null;

        if (selfProfile) {
            SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
            username = sharedPreferences.getString("username", null);
        }

        imageView = (ImageView) findViewById(R.id.profile_image_view);
        fullNameTextView = (TextView) findViewById(R.id.full_name_text_view);
        usernameTextView = (TextView) findViewById(R.id.username_text_view);
        phoneTextView = (TextView) findViewById(R.id.phone_text_view);
        addressTextView = (TextView) findViewById(R.id.address_text_view);
        editProfileButton = (Button) findViewById(R.id.edit_profile_button);
        chatButton = (Button) findViewById(R.id.chat_button);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);
        blockButton = (Button) findViewById(R.id.block_button);

        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsTheme.getInstance(username).blockUser(profile.getUsername());
            }
        });

        ProfileTheme.getInstance().get(
                username,
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

    private void setUpProfile() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile_button:
                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra("editing", true);
                startActivity(intent);
                break;
            case R.id.chat_button:

                SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                String currentUsername = sharedPreferences.getString("username", null);
                Intent chatIntent = new Intent(this,ChatActivity.class);
                Chat chat = new Chat(currentUsername,profile.getUsername());
                chatIntent.putExtra("chat",chat);
                startActivity(chatIntent);
                break;
        }
    }
}
