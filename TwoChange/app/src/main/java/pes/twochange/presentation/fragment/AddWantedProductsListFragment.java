package pes.twochange.presentation.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Product;
import pes.twochange.presentation.adapter.RecyclerViewWantedAdapter;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

public class AddWantedProductsListFragment extends AddProductsListFragment {

    private OnFragmentInteractionListener activity;
    private RecyclerViewWantedAdapter wantedAdapter;

    public AddWantedProductsListFragment() {
    }

    public static AddWantedProductsListFragment newInstance() {
        return new AddWantedProductsListFragment();
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
        if (context instanceof AddWantedProductsListFragment.OnFragmentInteractionListener) {
            activity = (AddWantedProductsListFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public boolean display(ArrayList<Product> products) {
        Log.v("WANTED", "null recycler view " + (recyclerView == null));
        if (recyclerView != null) {
            wantedAdapter = new RecyclerViewWantedAdapter(products, activity, activity);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(wantedAdapter);
            return true;
        } else {
            return false;
        }
    }

    public interface OnFragmentInteractionListener
            extends AddProductsListFragment.OnFragmentInteractionListener,
            OnRecyclerViewItemLongClickListener {

    }

}
