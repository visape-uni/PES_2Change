package pes.twochange.presentation.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;

public class PostAdActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AppCompatActivity";
    private static final int IMAGE_PICK_CODE = 0;

    private Ad ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case IMAGE_PICK_CODE:
                if (resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();

                }

                break;
        }
    }

    /*
        Asks the user to select an image from either camera or gallery
        and adds it to the ad. It also modifies the ImageButton to show a
        thumbnail of the image.
     */
    public void selectImage(View v) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 0);
    }

    public void publish(View v) {

    }
}
