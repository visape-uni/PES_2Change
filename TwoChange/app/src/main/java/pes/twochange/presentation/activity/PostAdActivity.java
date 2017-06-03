package pes.twochange.presentation.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.File;

import pes.twochange.R;
import pes.twochange.domain.callback.AdResponse;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Image;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;

public class PostAdActivity extends AppCompatActivity implements ImagePickDialog.ImagePickListener {

    private static final String LOG_TAG = "PostAdActivity";

    private static final File CAMERA_SAVE_LOCATION =
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/2change/images");

    // Do not change these. They are 0, 1, 2 and 3 for convenience.
    private static final int IMAGE_PICK_CODE_1 = 0;
    private static final int IMAGE_PICK_CODE_2 = 1;
    private static final int IMAGE_PICK_CODE_3 = 2;
    private static final int IMAGE_PICK_CODE_4 = 3;

    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 400;
    public static final int REQUEST_CAMERA = 401;

    private EditText titleTxt, descriptionTxt, priceTxt;
    private Spinner stateSpn, adTypeSpn, adCategorySpn;
    private ImageButton addImageBtn1, addImageBtn2, addImageBtn3, addImageBtn4;
    private LinearLayout itemDetails;

    private Ad ad;

    private boolean isEdition;
    private boolean hasCameraPermission = false;
    private boolean hasExternalStoragePermission = false;

    private View selectedImageButton = null;

    private AdTheme adTheme = AdTheme.getInstance();

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
        }


        ad = new Ad();

        itemDetails = (LinearLayout) findViewById(R.id.itemDetailsLayout);

        titleTxt = (EditText) findViewById(R.id.titleTxt);
        descriptionTxt = (EditText) findViewById(R.id.descriptionTxt);
        priceTxt = (EditText) findViewById(R.id.priceTxt);

        stateSpn = (Spinner) findViewById(R.id.stateSpn);
        adTypeSpn = (Spinner) findViewById(R.id.adTypeSpn);
        adCategorySpn = (Spinner) findViewById(R.id.adCategorySpn);

        addImageBtn1 = (ImageButton) findViewById(R.id.addImageBtn1);
        addImageBtn2 = (ImageButton) findViewById(R.id.addImageBtn2);
        addImageBtn3 = (ImageButton) findViewById(R.id.addImageBtn3);
        addImageBtn4 = (ImageButton) findViewById(R.id.addImageBtn4);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            isEdition = extras.getBoolean("edition", false);
            String adId = extras.getString("adId", null);

            if (isEdition) {
                if (adId == null)
                    throw new IllegalStateException("PostAdActivity called for edition but no adId provided");
                else {
                    adTheme.findById(adId, new AdResponse() {
                        @Override
                        public void onSuccess(Ad newAd) {
                            ad = newAd;
                            titleTxt.setText(ad.getTitle());
                            descriptionTxt.setText(ad.getDescription());
                        }

                        @Override
                        public void onFailure(String error) {
                            Snackbar.make(titleTxt, "There was a problem loading the ad", Snackbar.LENGTH_LONG).show();
                        }
                    });

                    itemDetails.setVisibility(View.GONE);
                    adCategorySpn.setVisibility(View.GONE);
                    adTypeSpn.setVisibility(View.GONE);

                    findViewById(R.id.addImgLayout).setVisibility(View.GONE);
                    findViewById(R.id.addImagesLbl).setVisibility(View.GONE);
                }
            } else
                ad = new Ad();
        } else {
            ProfileResponse profileResponse = new ProfileResponse() {
                @Override
                public void success(Profile profile) {
                    ad.setUser(profile);
                }

                @Override
                public void failure(String s) {
                    Snackbar.make(titleTxt, "There is no active user to link this ad to", Snackbar.LENGTH_LONG)
                            .addCallback(new Snackbar.Callback(){
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);
                                    finish();
                                }
                            }).show();
                }
            };

            SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
            final String currentUsername = sharedPreferences.getString("username", null);
            ProfileTheme.getInstance().get(currentUsername, profileResponse);

            adTypeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 1) {
                        itemDetails.setVisibility(View.GONE);
                    }
                    else {
                        adCategorySpn.setVisibility(View.VISIBLE);
                        itemDetails.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ad = new Ad();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK_CODE_1:
                case IMAGE_PICK_CODE_2:
                case IMAGE_PICK_CODE_3:
                case IMAGE_PICK_CODE_4:
                    Image selectedImage = ad.getImageAt(requestCode);
                    if (intent != null) { // From gallery
                        selectedImage.setUri(intent.getData());
                        ad.setImageAt(requestCode, selectedImage);
                        setImageToButton(selectedImage, getButtonFromRequestCode(requestCode));

                    } else { // From camera
                        final Image finalSelectedImage = selectedImage;
                        String[] toScan = {finalSelectedImage.getUri().getPath()};
                        MediaScannerConnection.scanFile(getApplicationContext(), toScan, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        if (uri != null) {
                                            finalSelectedImage.setUri(uri);
                                            //ad.setImageAt(requestCode, finalSelectedImage);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    setImageToButton(finalSelectedImage, getButtonFromRequestCode(requestCode));
                                                }
                                            });
                                        } else {
                                            String s = "test";
                                        }
                                    }
                                });
                    }
            }
        }
    }

    private void setImageToButton(Image image, ImageButton button) {
        Bitmap thumbnail =
                MediaStore.Images.Thumbnails.getThumbnail
                        (
                                getApplicationContext().getContentResolver(), ContentUris.parseId(image.getUri()),
                                MediaStore.Images.Thumbnails.MICRO_KIND, null
                        );

        if (button != null) {
            button.setImageBitmap(thumbnail);
            button.setColorFilter(Color.argb(0, 0, 0, 0));
        }
    }

    private ImageButton getButtonFromRequestCode(int requestCode) {
        switch (requestCode) {
            case IMAGE_PICK_CODE_1: return addImageBtn1;
            case IMAGE_PICK_CODE_2: return addImageBtn2;
            case IMAGE_PICK_CODE_3: return addImageBtn3;
            case IMAGE_PICK_CODE_4: return addImageBtn4;
            default: return null;
        }
    }

    public void showImageSourcePickDialog(View v) {
        selectedImageButton = v;
        requestPermissions();
    }

    public void publish(final View v) {
        ad.setTitle(titleTxt.getText().toString().toUpperCase());
        ad.setDescription(descriptionTxt.getText().toString());

        final Snackbar.Callback successCallback = new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                finish();
            }
        };

        if (isEdition) {
            adTheme.update(ad, new AdResponse() {
                @Override
                public void onSuccess(Ad ad) {
                    Snackbar.make(v, "Ad successfully updated!", Snackbar.LENGTH_SHORT)
                            .addCallback(successCallback)
                            .show();
                }

                @Override
                public void onFailure(String error) {
                    Snackbar.make(v, "There was an error updating the ad. Please try again.", Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            Integer price = priceTxt.getText().length() == 0 ? null : Integer.valueOf(priceTxt.getText().toString());
            Ad.ProductState state = Ad.ProductState.from(stateSpn.getSelectedItem().toString());

            ad.rate(state, price);
            ad.setCategory(adCategorySpn.getSelectedItem().toString());

            adTheme.save(ad, new AdResponse() {
                @Override
                public void onSuccess(Ad ad) {
                    Snackbar.make(v, "Your ad has been published!", Snackbar.LENGTH_SHORT)
                            .addCallback(successCallback)
                            .show();
                }

                @Override
                public void onFailure(String error) {
                    Snackbar.make(v, "There was an error publishing the ad. Please try again.", Snackbar.LENGTH_LONG).show();
                }
            });
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
                Image image = new Image(this, Image.generateName());
                image.setFormat(Image.Format.JPEG);
                File photo = new File(CAMERA_SAVE_LOCATION, image.getFirebaseName());
                //File photo = File.createTempFile(Image.generateName(), ".jpg", CAMERA_SAVE_LOCATION);
                /*Uri photoURI = FileProvider.getUriForFile(this,
                        "com.twochange.fileprovider",
                        photo);*/
                Uri photoURI = Uri.fromFile(photo);
                image.setUri(photoURI);

                ad.setImageAt(imageButtonTag, image);

                pickImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pickImage.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                break;
        }
        startActivityForResult(pickImage, imageButtonTag);
    }

    private void requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permissionCheck;

            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            hasCameraPermission = permissionCheck == PackageManager.PERMISSION_GRANTED;
            if (!hasCameraPermission)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);

            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            hasExternalStoragePermission = permissionCheck == PackageManager.PERMISSION_GRANTED;
            if (!hasExternalStoragePermission)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);

            if (hasCameraPermission && hasExternalStoragePermission)
                showImagePickDialog();

        } else
            showImagePickDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Nothing
        } else {
            switch (requestCode) {
                case REQUEST_WRITE_EXTERNAL_STORAGE:
                    hasExternalStoragePermission = true;
                    break;
                case REQUEST_CAMERA:
                    hasCameraPermission = true;
                    break;
            }

            if (hasExternalStoragePermission && !CAMERA_SAVE_LOCATION.exists()) {
                if (!CAMERA_SAVE_LOCATION.mkdirs())
                    Log.e(LOG_TAG, "Unable to create directory: " + CAMERA_SAVE_LOCATION.toString());
                else
                    Log.i(LOG_TAG, "Created directory: " + CAMERA_SAVE_LOCATION.toString());
            }

            if (hasCameraPermission && hasExternalStoragePermission)
                showImagePickDialog();
        }
    }

    private void showImagePickDialog() {
        ImagePickDialog dialog = new ImagePickDialog();
        dialog.setImageButtonTag(Integer.valueOf((String) selectedImageButton.getTag()));
        dialog.show(getFragmentManager(), "image_pick");
    }
}
