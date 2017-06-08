package pes.twochange.presentation.controller;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import pes.twochange.R;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Match;
import pes.twochange.domain.model.Product;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.domain.themes.MatchTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.activity.ImagePickDialog;
import pes.twochange.presentation.fragment.AddProductsListFragment;
import pes.twochange.presentation.fragment.AddWantedProductsListFragment;
import pes.twochange.presentation.fragment.MatchProductFragment;
import pes.twochange.presentation.fragment.MatchProductsListFragment;
import pes.twochange.presentation.fragment.MyProductFragment;
import pes.twochange.presentation.fragment.NewProductFragment;

public class ListsActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, AdTheme.ErrorResponse,
        AddProductsListFragment.OnFragmentInteractionListener, MatchTheme.ErrorResponse,
        AddWantedProductsListFragment.OnFragmentInteractionListener,
        NewProductFragment.OnFragmentInteractionListener,
        ImagePickDialog.ImagePickListener, MatchTheme.MatchesResponse,
        MatchProductsListFragment.OnFragmentInteractionListener,
        MyProductFragment.OnFragmentInteractionListener,
        MatchProductFragment.OnFragmentInteractionListener, MatchTheme.MatchResponse {

    // region ACTIVITY

    private String username;
    private int currentList;
    private Fragment fragment;

    private static final int NONE = -1;
    private static final int WANTED = R.id.navigation_wanted;
    private static final int OFFERED = R.id.navigation_offered;
    private static final int MATCHES = R.id.navigation_matches;

    private BottomNavigationView navigation;
    private Match selectedMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_lists);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        fragment = AddWantedProductsListFragment.newInstance();
        displayFragment(R.id.content_list, AddWantedProductsListFragment.newInstance(), "wanted");
        currentList = WANTED;

        navigation.setSelectedItemId(OFFERED);

        toolbar.setTitle(R.string.ad_list_title);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.ad_list_title);
        return true;
    }

    @Override
    protected int currentMenuItemIndex() {
        return LISTS_ACTIVITY;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (currentList != item.getItemId()) {
            currentList = item.getItemId();
            switch (item.getItemId()) {
                case R.id.navigation_wanted:
                    fragment = AddWantedProductsListFragment.newInstance();
                    displayFragment(R.id.content_list, fragment, "wanted");
                    break;

                case R.id.navigation_offered:
                    fragment = AddProductsListFragment.newInstance();
                    displayFragment(R.id.content_list, fragment, "offered");
                    break;

                case R.id.navigation_matches:
                    fragment = MatchProductsListFragment.newInstance();
                    displayFragment(R.id.content_list, fragment, "matches");
                    break;

            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (fragment instanceof NewProductFragment) {
            close();
        } else if (fragment instanceof MyProductFragment) {
            fragment = AddProductsListFragment.newInstance();
            replaceFragment(R.id.content_list, fragment, "offered");
        } else if (fragment instanceof MatchProductFragment) {
            fragment = MatchProductsListFragment.newInstance();
            replaceFragment(R.id.content_list, fragment, "matches");
        } else {
            super.onBackPressed();
        }
    }

    // endregion

    // region Recycler View Listeners

    @Override
    public void onRecyclerViewItemClickListener(int position) {
        switch (currentList) {
            case OFFERED:
                if (offeredProducts != null && position < offeredProducts.size()) {
                    Product selectedProduct = offeredProducts.get(position);
                    fragment = MyProductFragment.newInstance(selectedProduct.getName(),
                            selectedProduct.getDescription(), selectedProduct.getCategory(),
                            selectedProduct.getRating(), selectedProduct.getUrls());
                    replaceFragment(R.id.content_list, fragment, "product");
                }
                break;

            case MATCHES:
                selectedMatch = new ArrayList<Match>(matchedProducts.values()).get(position);
                MatchTheme.getInstance().getProductsMatch(selectedMatch, this, this);
                break;
        }
    }

    @Override
    public boolean onRecyclerViewItemLongClickListener(final int position) {
        if (currentList == WANTED) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.remove_wanted_title)
                    .setMessage("Do you really want to remove \"" +
                            wantedProducts.get(position).getCategory() + "\" from your wanted list?")
                    .setPositiveButton(
                            R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AdTheme.getInstance().remove(username,
                                            wantedProducts.get(position).getId());
                                    loadProductList();
                                }
                            }
                    )
                    .setNegativeButton(
                            R.string.no,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }
                    )
                    .show();
        }
        return true;
    }

    // endregion

    // region Products

    private Spinner input;
    private String[] categoryArray;

    @Override
    public void addProduct() {
        switch (currentList) {
            case WANTED:
                input = new Spinner(this);
                categoryArray = getResources().getStringArray(R.array.ad_category);
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, categoryArray);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                input.setAdapter(categoryAdapter);
                new AlertDialog.Builder(this)
                        .setView(input)
                        .setTitle("Select the category you want.")
                        .setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }
                        )
                        .setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String category = categoryArray[input.getSelectedItemPosition()];
                                        if (notWanted(category)) {
                                            AdTheme.getInstance().postWanted(username, category);
                                            dialog.dismiss();
                                            wantedProducts = null;
                                            loadProductList();
                                        }
                                    }
                                }
                        ).show();
                break;

            case OFFERED:
                images = new ArrayList<>();
                imageUris = new ArrayList<>();
                currentList = NONE;
                fragment = NewProductFragment.newInstance();
                toolbar.setVisibility(View.GONE);
                navigation.setVisibility(View.GONE);
                addFragment(R.id.content_list, fragment, "new_product");
                break;

            case MATCHES:
                if (wantedProducts == null) {
                    wantedProducts = new ArrayList<>();
                }
                MatchTheme.getInstance().makeMatches(username, matchedProducts, wantedProducts, this);
                break;
        }
    }

    private boolean notWanted(String category) {
        for (Product product : wantedProducts) {
            if (product.getCategory().equals(category)) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Product> wantedProducts;
    private ArrayList<Product> offeredProducts;
    private Map<String, Match> matchedProducts;

    private AdTheme.ProductListResponse wantedProductsResponse = new AdTheme.ProductListResponse() {
        @Override
        public void listResponse(ArrayList<Product> productItems) {
            Log.v("WANTED", productItems.size() + "");
            wantedProducts = productItems;
            loadProductList();
        }
    };

    private AdTheme.ProductListResponse offeredProductsResponse = new AdTheme.ProductListResponse() {
        @Override
        public void listResponse(ArrayList<Product> productItems) {
            offeredProducts = new ArrayList<>();
            for (Product product : productItems) {
                if (product.getUsername().equals(username)) {
                    offeredProducts.add(product);
                }
            }
            loadProductList();
        }
    };

    @Override
    public void loadProductList() {
        switch (currentList) {
            case WANTED:
                if (wantedProducts != null) {
                    if (fragment instanceof AddWantedProductsListFragment) {
                        ((AddWantedProductsListFragment) fragment).display(wantedProducts);
                    }
                } else {
                    AdTheme.getInstance().getWantedList(username, wantedProductsResponse, this);
                }
                break;

            case OFFERED:
                if (offeredProducts != null) {
                    if (fragment instanceof AddProductsListFragment) {
                        ((AddProductsListFragment) fragment).display(offeredProducts);
                    }
                } else {
                    AdTheme.getInstance().getAllProducts(offeredProductsResponse, this);
                }
                break;

            case MATCHES:
                if (matchedProducts != null) {
                    if (fragment instanceof MatchProductsListFragment) {
                        ((MatchProductsListFragment) fragment)
                                .display(matchedProducts);
                    }
                } else {
                    MatchTheme.getInstance().getMatches(username, this, this);
                }
                break;
        }
    }

    // endregion

    @Override
    public void success(Product product, Match match) {
        fragment = MatchProductFragment.newInstance(product.getName(), product.getDescription(),
                product.getCategory(), product.getRating(), product.getUrls(), product.getUsername(),
                match.getStatusInt());
        replaceFragment(R.id.content_list, fragment, "match");
    }

    @Override
    public void error(String error) {

    }

    // region Create Product

    private ArrayList<String> images;
    private ArrayList<Uri> imageUris;

    @Override
    public void addImage() {
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
    }

    @Override
    public void close() {
        fragmentManager.popBackStack();
        fragment = fragmentManager.findFragmentByTag("offered");
        toolbar.setVisibility(View.VISIBLE);
        navigation.setVisibility(View.VISIBLE);
        currentList = OFFERED;
        loadProductList();
    }

    @Override
    public void postProduct(Product product) {
        product.setUsername(username);
        product.setImages(images);
        String id = AdTheme.getInstance().save(product);
        String path = String.format("product/%s/", id);
        AdTheme.getInstance().storeImages(path, images, imageUris, this);
        close();
    }

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
                images.add(name + ".jpg");
                requestCode = GALLERY_IMAGE_REQUEST;
                break;
            case CAMERA:
                try {
                    File photo = File.createTempFile(name, ".jpg", CAMERA_SAVE_LOCATION);
                    Uri photoURI = FileProvider.getUriForFile(this, "com.twochange.fileprovider",
                            photo);
                    images.add(name + ".jpg");
                    imageUris.add(photoURI);
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
                imageUris.add(intent.getData());
            }
            ((NewProductFragment) fragment).display(imageUris);
        }
    }

    // endregion

    // region Match

    @Override
    public void success(Map<String, Match> myMatches) {
        if (myMatches == null || myMatches.size() == 0) {
            // TODO error
        } else {
            matchedProducts = myMatches;
            loadProductList();
        }
    }

    @Override
    public void match() {
        MatchTheme.getInstance().makeMatches(username, matchedProducts, wantedProducts, this);
    }

    @Override
    public void edit() {

    }

    @Override
    public void accept() {
        if (selectedMatch != null) {
            MatchTheme.getInstance().accept(selectedMatch);
            MatchTheme.getInstance().getProductsMatch(selectedMatch, this, this);
        }
    }

    @Override
    public void decline() {
        if (selectedMatch != null) {
            MatchTheme.getInstance().decline(selectedMatch);
            MatchTheme.getInstance().getProductsMatch(selectedMatch, this, this);
        }
    }

    @Override
    public void chat(String username) {
        Intent chatIntent = new Intent(this,ChatActivity.class);
        Chat chat = new Chat(this.username , username);
        chatIntent.putExtra("chat",chat);
        startActivity(chatIntent);
    }

    // endregion
}
