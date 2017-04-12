package pes.twochange.presentation.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import pes.twochange.R;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.themes.ProfileTheme;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView fullNameTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private Button editProfileButton;
    private Button chatButton;
    private ProgressBar loadingProgressBar;

    private String uid;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO reb l'UID del perfil d'alguna manera
        uid = "gclmkmaxal";

        imageView = (ImageView) findViewById(R.id.profile_image_view);
        fullNameTextView = (TextView) findViewById(R.id.full_name_text_view);
        phoneTextView = (TextView) findViewById(R.id.phone_text_view);
        addressTextView = (TextView) findViewById(R.id.address_text_view);
        editProfileButton = (Button) findViewById(R.id.edit_profile_button);
        chatButton = (Button) findViewById(R.id.chat_button);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);

        new ProfileTheme().get(
                uid,
                new ProfileResponse() {
                    @Override
                    public void success(Profile p) {
                        profile = p;
                        setUpProfile();
                    }

                    @Override
                    public void failure(String s) {
                        // TODO mostrar un mensaje de error, seguramente por conexión a internet
                    }
                }
        );

    }

    private void setUpProfile() {
        // TODO imagen de perfil & image view

        fullNameTextView.setText(profile.getFullName().toUpperCase());
        phoneTextView.setText(profile.getPhoneNumber().toString());
        addressTextView.setText(profile.getAddress().toString());

        // TODO depenent de com arriba a aquesta pantalla sap si esta mostrant el perfil d'un altre usuari o el seu perfil
        // en este caso vamos a suponer que es el mismo perfil que el usuario
        editProfileButton.setVisibility(View.VISIBLE);
        editProfileButton.setOnClickListener(this);

        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        // TODO start editing :D
        Toast.makeText(this, "EDIT PROFILE :D", Toast.LENGTH_SHORT).show();
    }
}
