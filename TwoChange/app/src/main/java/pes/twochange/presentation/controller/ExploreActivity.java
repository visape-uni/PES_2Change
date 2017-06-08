package pes.twochange.presentation.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Product;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.fragment.ChatProductFragment;
import pes.twochange.presentation.fragment.MyProductFragment;
import pes.twochange.presentation.fragment.ProductFragment;
import pes.twochange.presentation.fragment.SearchProductsListFragment;

public class ExploreActivity extends BaseActivity implements
        SearchProductsListFragment.OnFragmentInteractionListener,
        ChatProductFragment.OnFragmentInteractionListener,
        MyProductFragment.OnFragmentInteractionListener {

    public static final String TAG = "EXPLORE ACTIVITY";
    private Fragment fragment;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        toolbar.setTitle(R.string.explore);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        fragment = SearchProductsListFragment.newInstance();

        displayFragment(R.id.content_explore, fragment, "main_list");

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Explore 2Change");
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    private Spinner inputSpinner;
    private EditText inputText;
    private String[] categoryArray;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.category_filter:
                inputSpinner = new Spinner(this);
                categoryArray = getResources().getStringArray(R.array.ad_category);
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, categoryArray);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                inputSpinner.setAdapter(categoryAdapter);
                new AlertDialog.Builder(this)
                        .setView(inputSpinner)
                        .setTitle("Select the category you want to filter with")
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
                                        String category = categoryArray[inputSpinner.getSelectedItemPosition()];
                                        categoryFilter(category);
                                    }
                                }
                        ).show();
                break;

            case R.id.rate_filter:
                inputText = new EditText(this);
                inputText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(this)
                        .setView(inputSpinner)
                        .setTitle("Select the approximated rate want to filter with")
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
                                        String rateStr = inputText.getText().toString();
                                        int rate = Integer.valueOf(rateStr);
                                        rateFilter(rate - 10, rate + 10);
                                    }
                                }
                        ).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int currentMenuItemIndex() {
        return EXPLORE_ACTIVITY;
    }

    private ArrayList<Product> productsList = new ArrayList<>();

    @Override
    public void loadProductList() {
        AdTheme.getInstance().getAllProducts(
                new AdTheme.ProductListResponse() {
                    @Override
                    public void listResponse(ArrayList<Product> productItems) {
                        productsList = productItems;
                        if (fragment != null && fragment instanceof SearchProductsListFragment) {
                            ((SearchProductsListFragment) fragment).display(productsList);
                        }
                    }
                },
                new AdTheme.ErrorResponse() {
                    @Override
                    public void error(String error) {

                    }
                }
        );
    }

    @Override
    public void searchProducts(String query) {
        if (productsList != null && productsList.size() > 0) {
            ArrayList<Product> searchResultList = new ArrayList<>();
            String upperCaseQuery = query.toUpperCase();
            for (Product product : productsList) {
                String upperCaseTitle = product.getName().toUpperCase();
                String upperCaseDescription = product.getDescription().toUpperCase();
                if (upperCaseTitle.contains(upperCaseQuery) ||
                        upperCaseDescription.contains(upperCaseQuery)) {
                    searchResultList.add(product);
                }
            }
            if (fragment != null && fragment instanceof SearchProductsListFragment) {
                ((SearchProductsListFragment) fragment).display(searchResultList);
            }
        }
    }

    private void categoryFilter(String category) {
        if (productsList != null && productsList.size() > 0) {
            ArrayList<Product> categoryResultList = new ArrayList<>();
            for (Product product : productsList) {
                String prodCategory = product.getCategory();
                if (category.equals(prodCategory)) {
                    categoryResultList.add(product);
                }
            }
            if (fragment != null && fragment instanceof SearchProductsListFragment) {
                ((SearchProductsListFragment) fragment).display(categoryResultList);
            }
        }
    }

    private void rateFilter(int min, int max) {
        if (productsList != null && productsList.size() > 0) {
            ArrayList<Product> rateResultList = new ArrayList<>();
            for (Product product : productsList) {
                int prodRate = product.getRating();
                if ((prodRate >= min) && (prodRate <= max)) {
                    rateResultList.add(product);
                }
            }
            if (fragment != null && fragment instanceof SearchProductsListFragment) {
                ((SearchProductsListFragment) fragment).display(rateResultList);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment instanceof ProductFragment) {
            fragment = SearchProductsListFragment.newInstance();
            replaceFragment(R.id.content_explore, fragment, "main_list");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRecyclerViewItemClickListener(int position) {
        if (productsList != null && position < productsList.size()) {
            Product selectedProduct = productsList.get(position);
            String usersProduct = selectedProduct.getUsername();
            if (usersProduct.equals(username)) {
                fragment = MyProductFragment.newInstance(selectedProduct.getName(),
                        selectedProduct.getDescription(), selectedProduct.getCategory(),
                        selectedProduct.getRating(), selectedProduct.getUrls());
            } else {
                fragment = ChatProductFragment.newInstance(selectedProduct.getName(),
                        selectedProduct.getDescription(), selectedProduct.getCategory(),
                        selectedProduct.getRating(), selectedProduct.getUrls(), usersProduct);
            }
            replaceFragment(R.id.content_explore, fragment, "product");
        }
    }

    @Override
    public void chat(String username) {
        Intent chatIntent = new Intent(this,ChatActivity.class);
        Chat chat = new Chat(this.username , username);
        chatIntent.putExtra("chat",chat);
        startActivity(chatIntent);
    }

    @Override
    public void edit() {

    }

    @Override
    public boolean onRecyclerViewItemLongClickListener(int position) {
        return false;
    }
}
