package pes.twochange.presentation.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.presentation.fragment.SearchProductsListFragment;

public class ExploreActivity extends BaseActivity
        implements SearchProductsListFragment.OnFragmentInteractionListener {

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        toolbar.setTitle(R.string.explore);

        fragment = SearchProductsListFragment.newInstance();

        replaceFragment(R.id.explore_frame, fragment);

    }

    @Override
    protected int currentMenuItemIndex() {
        return EXPLORE_ACTIVITY;
    }

    private ArrayList<Ad> productsList = new ArrayList<>();

    @Override
    public void loadProductList() {
        // TODO
        // Descarga todos los productos de Firebase sin importar ninguna libreria ni clase de
        // firebase aqui. Esto es la capa de presentación, firebase es la de datos. Hay que pasar
        // por un Theme/Controlador/etc.
        //
        // TODO
        // Download all the products from Firebase without importing any Firebase library neither
        // class here. This is the presentation layer, Firebase is data layer. You have to give the
        // data through a Theme/Controller/etc.
        AdTheme.getInstance().getAllProducts(
                new AdTheme.ListResponse() {
                    @Override
                    public void listResponse(ArrayList<Ad> productItems) {
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


    private ArrayList<Ad> searchResultList = new ArrayList<>();

    @Override
    public void searchProducts(String query) {
        if (productsList != null && productsList.size() > 0) {
            searchResultList = new ArrayList<>();
            String upperCaseQuery = query.toUpperCase();
            for (Ad product : productsList) {
                String upperCaseTitle = product.getTitle().toUpperCase();
                String upperCaseDescription = product.getDescription().toUpperCase();
                if (upperCaseTitle.contains(upperCaseQuery) ||
                        upperCaseDescription.contains(upperCaseQuery)) {
                    searchResultList.add(product);
                }
            }
            if (fragment != null && fragment instanceof SearchProductsListFragment) {
                ((SearchProductsListFragment) fragment).display(searchResultList);
            }
        } else {
            // TODO
            // Descarga productos que contenga $query de Firebase
            //
            // TODO
            // Download product that contain $query from Firebase
        }
    }

    @Override
    public void onRecyclerViewItemClickListener(int position) {

    }

}
