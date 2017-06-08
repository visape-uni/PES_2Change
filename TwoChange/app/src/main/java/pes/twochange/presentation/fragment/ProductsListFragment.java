package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Product;
import pes.twochange.presentation.adapter.RecyclerViewProductAdapter;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

public class ProductsListFragment extends Fragment {

    protected RecyclerView recyclerView;
    private OnFragmentInteractionListener activity;
    private RecyclerViewProductAdapter productAdapter;

    public ProductsListFragment() {
    }

    public static ProductsListFragment newInstance() {
        return new ProductsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_list, container, false);
        buildRecyclerView(view);
        return view;
    }

    public void buildRecyclerView(@NonNull View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);
        productAdapter = new RecyclerViewProductAdapter(getContext(), new ArrayList<Product>(),
                activity, activity);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(productAdapter);
        activity.loadProductList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            activity = (OnFragmentInteractionListener) context;
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

    public boolean display(ArrayList<Product> products) {
        if (recyclerView != null) {
            if (products.size() == 0) {
                // TODO empty "error"
            }
            productAdapter = new RecyclerViewProductAdapter(getContext(), products, activity, activity);
            recyclerView.setAdapter(productAdapter);
            return true;
        } else {
            return false;
        }
    }

    public interface OnFragmentInteractionListener extends OnRecyclerViewItemClickListener,
            OnRecyclerViewItemLongClickListener {
        void loadProductList();
    }

}
