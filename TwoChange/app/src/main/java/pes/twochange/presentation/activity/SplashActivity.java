package pes.twochange.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import pes.twochange.R;
import pes.twochange.domain.themes.AuthTheme;
import pes.twochange.presentation.Config;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener, AuthTheme.Response {

    private TranslateAnimation translateAnimation;

    private TextView name;
    private TextView slogan;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = (ImageView) findViewById(R.id.logo_image);
        name = (TextView) findViewById(R.id.name);
        slogan = (TextView) findViewById(R.id.slogan);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        translateAnimation = new TranslateAnimation(0.0f, 0.0f, 1000.0f, 0.0f);
        translateAnimation.setDuration(2000);
        translateAnimation.setAnimationListener(this);
        logo.startAnimation(translateAnimation);
    }



    @Override
    public void onAnimationStart(Animation animation) {
        AuthTheme authTheme = AuthTheme.getInstance();
        authTheme.setSharedPreferences(getSharedPreferences(Config.SP_NAME, MODE_PRIVATE));
        authTheme.setResponse(this);
        authTheme.startListeningFirebaseAuth();
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                }
        );
        name.startAnimation(alphaAnimation);
        slogan.startAnimation(alphaAnimation);
        progressBar.startAnimation(alphaAnimation);
        name.setVisibility(View.VISIBLE);
        slogan.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void main() {
        startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
    }

    @Override
    public void profile() {
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        intent.putExtra("editing", false);
        startActivity(intent);
    }

    @Override
    public void noConnection() {
        // TODO Control d'errors
    }

    @Override
    public void startActivity(Intent intent) {
        translateAnimation.setAnimationListener(null);
        super.startActivity(intent);
        finish();
    }
}
