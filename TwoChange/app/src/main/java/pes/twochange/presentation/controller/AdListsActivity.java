package pes.twochange.presentation.controller;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Product;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.domain.themes.MatchTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.fragment.AdListFragment;

public class AdListsActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        AdListFragment.OnFragmentInteractionListener, AdTheme.ErrorResponse, AdTheme.ListResponse, AdTheme.WantedResponse {

    private String username;
    private AdListFragment fragment;

    private final String[] TAGS = {
            "wanted", "offered", "single", "create"
    };

    private final int WANTED = 0;
    private final int OFFERED = 1;
    private final int SINGLE = 2;
    private final int CREATE = 3;

    private int currentFragment;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int navigationItem = currentFragment;
        switch (item.getItemId()) {
            case R.id.navigation_wanted:
                navigationItem = WANTED;
                break;

            case R.id.navigation_offered:
                navigationItem = OFFERED;
                break;
        }

        if (currentFragment == navigationItem) {
            return false;
        } else {
            displayFragment(navigationItem, TAGS[navigationItem], -1);
            return true;
        }
    }

    @Override
    protected int currentMenuItemIndex() {
        return AD_ACTIVITY;
    }

    // newFragment = the number of the new fragment
    // itemList = the list is showing
    // itemPosition = the position of the item is showing, if it is displaying a list -1
    private void displayFragment(int newFragment, String itemList, int itemPosition) {
        // if the new fragment is SINGLE or CREATE it is just added in the top of the stack,
        // if it is WANTED of OFFERED the whole stack is removed and then the new fragment is added
        if (newFragment == WANTED || newFragment == OFFERED) {
            int firstFragmentId = fragmentManager.getBackStackEntryAt(0).getId();
            fragmentManager.popBackStack(firstFragmentId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        switch (newFragment) {
            case WANTED:
            case OFFERED:
                fragment = AdListFragment.newInstance();
                break;

            case SINGLE:
                // TODO single item fragment
                break;

            case CREATE:
                // TODO create item fragment
                break;

            default:
                // an error, just restart the activity flow
                displayFragment(WANTED, itemList, -1);
                return;
        }

        currentFragment = newFragment;
        fragment = AdListFragment.newInstance();
        addFragment(R.id.content, fragment, TAGS[newFragment]);
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
        fragment = AdListFragment.newInstance();
        addFragment(R.id.content, fragment, TAGS[WANTED]);

        toolbar.setTitle(R.string.ad_list_title);
    }

    private ArrayList<Product> wantedProducts;
    private ArrayList<Ad> offeredAds;

    @Override
    public void onRecyclerViewItemClickListener(int position) {
        displayFragment(SINGLE, TAGS[currentFragment], position);
    }

    @Override
    public boolean onRecyclerViewItemLongClickListener(final int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.remove_wanted_title)
                .setMessage("Do you really want to remove \"" + wantedProducts.get(position).getName()
                                + "\" from your wanted list?")
                .setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AdTheme.getInstance().remove(
                                        username,
                                        TAGS[WANTED],
                                        wantedProducts.get(position).getKey()
                                );
                                getProductList(fragment, true);
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
        return true;
    }

    @Override
    public void getProductList(final AdListFragment response, boolean force) {
        String title = TAGS[currentFragment];
        if (currentFragment == WANTED) {
            if (wantedProducts != null && !force) {
                response.responseProducts(wantedProducts);
            } else {
                AdTheme.getInstance().getWantedList(
                        username,
                        this,
                        this
                );
            }
        } else {
            if (offeredAds != null && !force) {
                response.responseAds(offeredAds);
            } else {
                AdTheme.getInstance().getOfferedList(
                        username,
                        this,
                        this
                );
            }
        }
    }

    @Override
    public void match() {
        //MatchTheme.getInstance().makeMatches(username, wantedProducts, myProducts);
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void listResponse(ArrayList<Ad> ads) {
        this.offeredAds = ads;
        fragment.responseAds(ads);
        //fragment.notifyDataSetChanged();
    }

    @Override
    public void wantedListResponse(ArrayList<Product> products) {
        this.wantedProducts = products;
        fragment.responseProducts(products);
//        fragment.notifyDataSetChanged();
    }
}
