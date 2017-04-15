package pes.twochange.presentation.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;

public class PostAdActivity extends AppCompatActivity implements ImagePickDialog.ImagePickListener {

    private static final String LOG_TAG = "PostAdActivity";

    private static final String CAMERA_SAVE_LOCATION =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/2change/images";

    // Do not change these. They are 0, 1, 2 and 3 for convenience.
    private static final int IMAGE_PICK_CODE_1 = 0;
    private static final int IMAGE_PICK_CODE_2 = 1;
    private static final int IMAGE_PICK_CODE_3 = 2;
    private static final int IMAGE_PICK_CODE_4 = 3;

    private EditText titleTxt, descriptionTxt, yearTxt, priceTxt;
    private Spinner stateSpn;
    private ImageButton addImageBtn1, addImageBtn2, addImageBtn3, addImageBtn4;

    private Ad ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ad = new Ad();

        titleTxt = (EditText) findViewById(R.id.titleTxt);
        descriptionTxt = (EditText) findViewById(R.id.descriptionTxt);
        yearTxt = (EditText) findViewById(R.id.yearTxt);
        priceTxt = (EditText) findViewById(R.id.priceTxt);

        stateSpn = (Spinner) findViewById(R.id.stateSpn);

        addImageBtn1 = (ImageButton) findViewById(R.id.addImageBtn1);
        addImageBtn2 = (ImageButton) findViewById(R.id.addImageBtn2);
        addImageBtn3 = (ImageButton) findViewById(R.id.addImageBtn3);
        addImageBtn4 = (ImageButton) findViewById(R.id.addImageBtn4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK_CODE_1:
                case IMAGE_PICK_CODE_2:
                case IMAGE_PICK_CODE_3:
                case IMAGE_PICK_CODE_4:
                    Uri selectedImage = intent.getData();
                    ad.setImageAt(requestCode, selectedImage);

                    Bitmap thumbnail =
                        MediaStore.Images.Thumbnails.getThumbnail
                            (
                                getContentResolver(), ContentUris.parseId(selectedImage),
                                MediaStore.Images.Thumbnails.MICRO_KIND, null
                            );

                    ImageButton button = null;
                    switch (requestCode) {
                        case IMAGE_PICK_CODE_1: button = addImageBtn1; break;
                        case IMAGE_PICK_CODE_2: button = addImageBtn2; break;
                        case IMAGE_PICK_CODE_3: button = addImageBtn3; break;
                        case IMAGE_PICK_CODE_4: button = addImageBtn4; break;
                    }

                    if (button != null) {
                        button.setImageBitmap(thumbnail);
                        button.setColorFilter(Color.argb(0, 0, 0, 0));
                    }
                    break;
            }
        }
    }

    /*
        Asks the user to select an image from either camera or gallery.
     */
    public void showImageSourcePickDialog(View v) {
        ImagePickDialog dialog = new ImagePickDialog();
        dialog.setImageButtonTag(Integer.valueOf((String) v.getTag()));
        dialog.show(getFragmentManager(), "image_pick");
    }

    public void publish(View v) {
        Toast.makeText(getApplicationContext(), "PUBLISHING", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onImageSourceSelected(ImagePickDialog.ImageSource source, int imageButtonTag) {
        Intent pickImage = null;
        switch (source) {
            case GALLERY:
                pickImage = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                break;
            case CAMERA:
                pickImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        int code = -1;
        switch (imageButtonTag) {
            case 0: code = IMAGE_PICK_CODE_1; break;
            case 1: code = IMAGE_PICK_CODE_2; break;
            case 2: code = IMAGE_PICK_CODE_3; break;
            case 3: code = IMAGE_PICK_CODE_4; break;
        }
        startActivityForResult(pickImage, code);
    }
}
