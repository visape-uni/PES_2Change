package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Product;
import pes.twochange.presentation.adapter.RecyclerViewWantedAdapter;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

public class WantedProductsListFragment extends ProductsListFragment {

    private OnFragmentInteractionListener activity;
    private RecyclerViewWantedAdapter wantedAdapter;

    public WantedProductsListFragment() {
    }

    public static WantedProductsListFragment newInstance() {
        return new WantedProductsListFragment();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstnceState) {
        View view = inflater.inflate(R.layout.fragment_wanted_list, container, false);
        buildRecyclerView(view);
        FloatingActionButton addWantedbtn = (FloatingActionButton) view.findViewById(R.id.add_wanted_product);
        addWantedbtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void buildRecyclerView(@NonNull View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);
        wantedAdapter = new RecyclerViewWantedAdapter(new ArrayList<Product>(), activity, activity);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(wantedAdapter);
        activity.loadProductList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WantedProductsListFragment.OnFragmentInteractionListener) {
            activity = (WantedProductsListFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void display(ArrayList<Product> products) {
        if (recyclerView != null) {
            if (products.size() == 0) {
                // TODO empty "error"
            }
            wantedAdapter = new RecyclerViewWantedAdapter(products, activity, activity);
            recyclerView.setAdapter(wantedAdapter);
        }
    }

    public interface OnFragmentInteractionListener
            extends ProductsListFragment.OnFragmentInteractionListener,
            OnRecyclerViewItemLongClickListener {

    }

}
