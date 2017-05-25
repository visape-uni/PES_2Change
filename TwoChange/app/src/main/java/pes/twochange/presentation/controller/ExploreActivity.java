package pes.twochange.presentation.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.fragment.ProductsListFragment;

public class ExploreActivity extends BaseActivity implements ProductsListFragment.OnFragmentInteractionListener {

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        toolbar.setTitle(R.string.explore);

        fragment = ProductsListFragment.newInstance(true);

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
        if (fragment != null && fragment instanceof ProductsListFragment) {
            ((ProductsListFragment) fragment).display(productsList);
        }
    }


    private ArrayList<Ad> searchResultList = new ArrayList<>();

    @Override
    public void searchProducts(String query) {
        // TODO
        // Descarga productos que contenga $query de Firebase
        //
        // TODO
        // Download product that contain $query from Firebase
        if (fragment != null && fragment instanceof ProductsListFragment) {
            ((ProductsListFragment) fragment).display(searchResultList);
        }
    }

    @Override
    public void onRecyclerViewItemClickListener(int position) {

    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }
}
