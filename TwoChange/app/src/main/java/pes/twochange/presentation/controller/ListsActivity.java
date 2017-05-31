package pes.twochange.presentation.controller;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.fragment.AddProductsListFragment;
import pes.twochange.presentation.fragment.NewProductFragment;
import pes.twochange.presentation.fragment.ProductsListFragment;
import pes.twochange.presentation.fragment.WantedProductsListFragment;

public class ListsActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, AdTheme.ErrorResponse,
        AddProductsListFragment.OnFragmentInteractionListener,
        WantedProductsListFragment.OnFragmentInteractionListener,
        NewProductFragment.OnFragmentInteractionListener {

    private static final String SINGLE = "single_product_view";
    private String username;
    private int currentList = WANTED;
    private Fragment fragment;

    //
    private static final int WANTED = R.id.navigation_wanted;
    private static final int OFFERED = R.id.navigation_offered;
    private static final int MATCHES = R.id.navigation_matches;

    private int currentFragment;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_lists);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        currentFragment = WANTED;
        fragment = WantedProductsListFragment.newInstance();
        displayFragment(R.id.content, WantedProductsListFragment.newInstance());

        toolbar.setTitle(R.string.ad_list_title);
    }

    @Override
    protected int currentMenuItemIndex() {
        return AD_ACTIVITY;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        currentList = item.getItemId();
        switch (item.getItemId()) {
            case R.id.navigation_wanted:
                displayFragment(R.id.content, WantedProductsListFragment.newInstance());
                break;

            case R.id.navigation_offered:
                displayFragment(R.id.content, AddProductsListFragment.newInstance());
                break;

            case R.id.navigation_matches:
//                displayFragment(R.id.content, MatchProductsListFragment.newInstance());
                break;

        }
        return true;
    }

    @Override
    public void onRecyclerViewItemClickListener(int position) {
        switch (currentList) {
            case WANTED:
                break;

            case OFFERED:
                break;

            case MATCHES:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment instanceof NewProductFragment) {
            close();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onRecyclerViewItemLongClickListener(final int position) {
        switch (currentList) {
            case WANTED:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.remove_wanted_title)
                        .setMessage("Do you really want to remove \"" +
                                wantedProducts.get(position).getTitle() + "\" from your wanted list?")
                        .setPositiveButton(
                                R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AdTheme.getInstance().remove(
                                                username,
                                                wantedProducts.get(position).getId()
                                        );
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
                break;

            case OFFERED:
                break;

            case MATCHES:
                break;
        }
        return true;
    }

    @Override
    public void addProduct() {
        switch (currentList) {
            case WANTED:

                break;

            case OFFERED:
                fragment = NewProductFragment.newInstance();
                toolbar.setVisibility(View.GONE);
                navigation.setVisibility(View.GONE);
                addFragment(R.id.content, fragment, "new product");
                break;

            case MATCHES:
                // TODO calculate matches
                break;
        }
    }

    private ArrayList<Ad> wantedProducts;
    private ArrayList<Ad> offeredProducts;
    private ArrayList<Ad> matchProducts;

    @Override
    public void loadProductList() {
        switch (currentList) {
            case WANTED:
                if (wantedProducts != null) {
                    if (fragment instanceof WantedProductsListFragment) {
                        ((WantedProductsListFragment) fragment).display(wantedProducts);
                    }
                } else {
                    AdTheme.getInstance().getWantedList(
                            username,
                            new AdTheme.ListResponse() {
                                @Override
                                public void listResponse(ArrayList<Ad> productItems) {
                                    wantedProducts = productItems;
                                    loadProductList();
                                }
                            }, this
                    );
                }
                break;

            case OFFERED:
                if (offeredProducts != null) {
                    if (fragment instanceof ProductsListFragment) {
                        ((ProductsListFragment) fragment).display(offeredProducts);
                    }
                } else {
                    AdTheme.getInstance().getAllProducts(
                            new AdTheme.ListResponse() {
                                @Override
                                public void listResponse(ArrayList<Ad> productItems) {
                                    offeredProducts = new ArrayList<>();
                                    for (Ad product : productItems) {
                                        if (product.getUserName().equals(username)) {
                                            offeredProducts.add(product);
                                        }
                                    }
                                    loadProductList();
                                }
                            }, this
                    );
                }
                break;

            case MATCHES:
//                MatchTheme.getInstance()
                break;
        }
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void addImage() {

    }

    @Override
    public void close() {
        fragmentManager.popBackStack();
        toolbar.setVisibility(View.VISIBLE);
        navigation.setVisibility(View.VISIBLE);
    }

    @Override
    public void postProduct(String name, String description, String category) {

    }
}
