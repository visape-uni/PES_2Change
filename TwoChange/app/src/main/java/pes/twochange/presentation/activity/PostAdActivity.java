package pes.twochange.presentation.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Image;

public class PostAdActivity extends AppCompatActivity implements ImagePickDialog.ImagePickListener {

    private static final String LOG_TAG = "PostAdActivity";

    private static final File CAMERA_SAVE_LOCATION =
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/2change/images");

    // Do not change these. They are 0, 1, 2 and 3 for convenience.
    private static final int IMAGE_PICK_CODE_1 = 0;
    private static final int IMAGE_PICK_CODE_2 = 1;
    private static final int IMAGE_PICK_CODE_3 = 2;
    private static final int IMAGE_PICK_CODE_4 = 3;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 400;

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

        if (!CAMERA_SAVE_LOCATION.exists()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
            if (!CAMERA_SAVE_LOCATION.mkdirs()) {
                Log.e(LOG_TAG, "Unable to create directory: " + CAMERA_SAVE_LOCATION.toString());
            }
        }

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
                    Image selectedImage = ad.getImageAt(requestCode);
                    if (selectedImage == null) {
                        selectedImage = new Image(this, intent.getData());
                        ad.setImageAt(requestCode, selectedImage);
                    }

                    try {
                        ContentUris.parseId(selectedImage.getUri());
                    } catch (NumberFormatException e) { //  /path/to/image.jpg
                        //selectedImage.setUri(Uri.parse(selectedImage.getUri().toString()));
                        selectedImage.setUri(Uri.parse(CAMERA_SAVE_LOCATION.toString() + "/20170415_175859-1148825809.jpg"));
                    }

                    Bitmap thumbnail =
                        MediaStore.Images.Thumbnails.getThumbnail
                            (
                                getContentResolver(), ContentUris.parseId(selectedImage.getUri()),
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
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_PICK_CODE_1:
                case IMAGE_PICK_CODE_2:
                case IMAGE_PICK_CODE_3:
                case IMAGE_PICK_CODE_4:
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
        ad.setTitle(titleTxt.getText().toString());
        ad.setDescription(descriptionTxt.getText().toString());

        int year = Integer.valueOf(yearTxt.getText().toString());
        Ad.ProductState state = Ad.ProductState.from(stateSpn.getSelectedItem().toString());
        int price = Integer.valueOf(priceTxt.getText().toString());

        ad.rate(state, year, price);
        try {
            ad.save();
            Snackbar.make(v, "Your ad has been published!", Snackbar.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(v, "There was an error publishing the ad. Please try again.", Snackbar.LENGTH_LONG).show();
        }
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
                try {
                    File photo = File.createTempFile(Image.generateName(), ".jpg", CAMERA_SAVE_LOCATION);
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.twochange.fileprovider",
                            photo);

                    ad.setImageAt(imageButtonTag, new Image(this, photoURI));

                    pickImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    pickImage.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
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
