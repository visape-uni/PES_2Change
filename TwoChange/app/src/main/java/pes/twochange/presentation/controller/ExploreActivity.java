package pes.twochange.presentation.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;

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

        fragment = ProductsListFragment.newInstance();

        addFragment(R.id.explore_frame, fragment, "Products List");

    }

    @Override
    protected int currentMenuItemIndex() {
        return EXPLORE_ACTIVITY;
    }

    private ArrayList<Ad> productsList;

    @Override
    public void loadProductList() {
        // TODO:
        // Descarga todos los productos de Firebase sin importar ninguna libreria ni clase de
        // firebase aqui. Esto es la capa de presentación, firebase es la de datos. Hay que pasar
        // por un Theme/Controlador/etc.
        if (fragment != null && fragment instanceof ProductsListFragment) {
            ((ProductsListFragment) fragment).display(productsList);
        }
    }

    @Override
    public void onRecyclerViewItemClickListener(int position) {

    }
}
