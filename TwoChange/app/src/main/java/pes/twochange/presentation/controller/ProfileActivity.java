package pes.twochange.presentation.controller;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Product;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.domain.themes.SettingsTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.activity.ImagePickDialog;
import pes.twochange.presentation.fragment.EditProfileFragment;
import pes.twochange.presentation.fragment.MyProductFragment;
import pes.twochange.presentation.fragment.ProductsListFragment;
import pes.twochange.presentation.fragment.WantedProductsListFragment;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.ImageManager;

public class ProfileActivity extends BaseActivity implements AdTheme.ErrorResponse,
        WantedProductsListFragment.OnFragmentInteractionListener,
        MyProductFragment.OnFragmentInteractionListener, View.OnClickListener,
        ImagePickDialog.ImagePickListener {

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
    private static final int PRODUCT = 4;

    private float rate;
    RatingBar ratingBar;

    private int currentFragment;

    private static final String TAG = "ProfileNoActivity";
    private CircleImageView image;
    private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        if (getIntent().getStringExtra("usernameProfile") == null) {
            usernameProfile = currentUsername;
        } else {
            usernameProfile = getIntent().getStringExtra("usernameProfile");
        }

        imageName = "profiles/" + usernameProfile + ".jpg";

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

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (currentUsername.equals(usernameProfile)) {
                    Toast.makeText(ProfileActivity.this, "You can't rate your own profile", Toast.LENGTH_SHORT).show();
                } else {
                    rate = rating;
                    ProfileTheme.getInstance(profile).isRated(currentUsername, rateCallback);
                }
                    setUpProfile();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (usernameProfile.equals(currentUsername)) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("My profile");
            getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        }
        else {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("User profile");
            getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        }
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
                //TODO: desactivar/activar notificaciones
                SettingsTheme.getInstance(currentUsername).toggleNotifications(profile);

                return true;
            case R.id.action_block:
                //TODO: unblock user
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

            case R.id.profile_photo:
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    int permissionCheck;

                    permissionCheck = ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA);
                    if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CAMERA);
                    } else {
                        hasCameraPermission = true;
                    }

                    permissionCheck = ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        hasExternalStoragePermission = true;
                    }

                    if (hasCameraPermission && hasExternalStoragePermission) {
                        showImagePickDialog();
                    }
                } else {
                    showImagePickDialog();
                }
                break;
        }
    }

    // region IMAGE

    private Uri imageUri;

    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 400;
    public static final int REQUEST_CAMERA = 401;
    private boolean hasExternalStoragePermission = false;
    private boolean hasCameraPermission = false;

    private static final File CAMERA_SAVE_LOCATION =
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/2change/images");

    private static final String LOG_TAG = "ListActivity";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // TODO show error because we do not have permission
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
                if (!CAMERA_SAVE_LOCATION.mkdirs()) {
                    Log.e(LOG_TAG, "Unable to create directory: " + CAMERA_SAVE_LOCATION.toString());
                } else {
                    Log.i(LOG_TAG, "Created directory: " + CAMERA_SAVE_LOCATION.toString());
                }
            }

            if (hasCameraPermission && hasExternalStoragePermission) {
                showImagePickDialog();
            }
        }
    }

    private void showImagePickDialog() {
        ImagePickDialog dialog = new ImagePickDialog();
        dialog.setImageButtonTag(-1);
        dialog.show(getFragmentManager(), "image_pick");
    }

    private static final int CAMERA_IMAGE_REQUEST = 1110;
    private static final int GALLERY_IMAGE_REQUEST = 1996;

    @Override
    public void onImageSourceSelected(ImagePickDialog.ImageSource source, int imageButtonTag) {
        Intent pickImage = null;
        String name = Product.generateImageName();
        int requestCode = -1;
        switch (source) {
            case GALLERY:
                pickImage = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                requestCode = GALLERY_IMAGE_REQUEST;
                break;
            case CAMERA:
                try {
                    File photo = File.createTempFile(name, ".jpg", CAMERA_SAVE_LOCATION);
                    Uri photoURI = FileProvider.getUriForFile(this, "com.twochange.fileprovider",
                            photo);
                    imageUri = photoURI;
                    pickImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    pickImage.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    requestCode = CAMERA_IMAGE_REQUEST;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        startActivityForResult(pickImage, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_IMAGE_REQUEST) {
                imageUri = intent.getData();
            }
            Picasso.with(this).load(imageUri).into(image);
            ImageManager.getInstance().storeImage(imageName, imageUri, getApplicationContext());
        }
    }

    // endregion

    // region the_rest

    protected int currentMenuItemIndex() {
        return PROFILE_ACTIVITY;
    }

    DatabaseResponse rateCallback = new DatabaseResponse() {
        @Override
        public void success(DataSnapshot dataSnapshot) {
            Toast.makeText(ProfileActivity.this, "You have already rated this user", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void empty() {
            ProfileTheme.getInstance(profile).rate(rate, currentUsername);
            Toast.makeText(ProfileActivity.this, "User rated successfully", Toast.LENGTH_SHORT).show();
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

        usernameTextView.setText(profile.getUsername().toUpperCase());
        nameTextView.setText(profile.fullName());
        if (profile.getNumRates() == 0) rate.setText(String.valueOf(0));
        else rate.setText(new DecimalFormat("##.##").format(profile.getRate()));
        numRates.setText(String.valueOf(profile.getNumRates()));

        image = (CircleImageView) findViewById(R.id.profile_photo);
        ImageManager.getInstance().putImageIntoView(imageName, this, image);
        if (usernameProfile.equals(currentUsername)) {
            image.setOnClickListener(this);
        }

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

    private Product selectedProduct;

    @Override
    public void onRecyclerViewItemClickListener(int position) {
        if (currentFragment == OFFERED) {
            findViewById(R.id.margin_layout).setVisibility(View.VISIBLE);
            selectedProduct = offeredList.get(position);
            fragment = MyProductFragment.newInstance(selectedProduct.getName(),
                    selectedProduct.getDescription(), selectedProduct.getCategory(),
                    selectedProduct.getRating(), selectedProduct.getUrls());
            currentFragment = PRODUCT;
            replaceFragment(R.id.contentProfile, fragment, "product");
        }
    }

    @Override
    public boolean onRecyclerViewItemLongClickListener(int position) {
        return false;
    }

    @Override
    public void loadProductList() {

    }

    @Override
    public void onBackPressed() {
        if (currentFragment == PRODUCT || currentFragment == EDIT) {
            findViewById(R.id.margin_layout).setVisibility(View.GONE);
            fragment = ProductsListFragment.newInstance();
            replaceFragment(R.id.contentProfile, fragment, "offered");
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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void edit() {

    }

    // endregion
}
