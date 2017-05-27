package pes.twochange.presentation.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.fragment.AddProductsListFragment;
import pes.twochange.presentation.fragment.ProductsListFragment;
import pes.twochange.presentation.fragment.WantedProductsListFragment;

public class ListsActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        AddProductsListFragment.OnFragmentInteractionListener, AdTheme.ErrorResponse {

    private static final String SINGLE = "single_product_view";
    private String username;
    private int currentList = WANTED;
    private Fragment fragment;

    //
    private static final int WANTED = R.id.navigation_wanted;
    private static final int OFFERED = R.id.navigation_offered;
    private static final int MATCHES = R.id.navigation_matches;

    private int currentFragment;

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
    protected int currentMenuItemIndex() {
        return AD_ACTIVITY;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_lists);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        currentFragment = WANTED;
        fragment = WantedProductsListFragment.newInstance();
        displayFragment(R.id.content, WantedProductsListFragment.newInstance());

        toolbar.setTitle(R.string.ad_list_title);
    }

    @Override
    public void onRecyclerViewItemClickListener(int position) {

    }

    @Override
    public void addProduct() {

    }

    @Override
    public void loadProductList() {
        switch (currentList) {
            case WANTED:
                AdTheme.getInstance().getWantedList(
                        username,
                        new AdTheme.ListResponse() {
                            @Override
                            public void listResponse(ArrayList<Ad> productItems) {
                                display(productItems);
                            }
                        }, this
                );
                break;

            case OFFERED:
                AdTheme.getInstance().getAllProducts(
                        new AdTheme.ListResponse() {
                            @Override
                            public void listResponse(ArrayList<Ad> productItems) {
                                ArrayList<Ad> offeredProducts = new ArrayList<>();
                                for (Ad product : productItems) {
                                    if (product.getUserName().equals(username)) {
                                        offeredProducts.add(product);
                                    }
                                }
                                display(offeredProducts);
                            }
                        }, this
                );
                break;

            case MATCHES:
//                MatchTheme.getInstance()
                break;
        }
    }

    private void display(ArrayList<Ad> items) {
        if (fragment instanceof ProductsListFragment) {
            ((ProductsListFragment) fragment).display(items);
        }
    }

    @Override
    public void error(String error) {

    }


//    @Override
//    public boolean onRecyclerViewItemLongClickListener(final int position) {
//        new AlertDialog.Builder(this)
//                .setTitle(R.string.remove_wanted_title)
//                .setMessage("Do you really want to remove \"" + wantedProducts.get(position).getName()
//                                + "\" from your wanted list?")
//                .setPositiveButton(
//                        R.string.yes,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                AdTheme.getInstance().remove(
//                                        username,
//                                        wantedProducts.get(position).getKey()
//                                );
//                            }
//                        }
//                )
//                .setNegativeButton(
//                        R.string.no,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        }
//                )
//                .show();
//        return true;
//    }
}
