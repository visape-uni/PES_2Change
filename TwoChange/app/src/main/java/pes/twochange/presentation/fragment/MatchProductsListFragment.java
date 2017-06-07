package pes.twochange.presentation.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.adapter.RecyclerViewMatchAdapter;

public class MatchProductsListFragment extends ProductsListFragment {

    private OnFragmentInteractionListener activity;
    protected RecyclerViewMatchAdapter adapter;

    public MatchProductsListFragment() {
    }

    public static MatchProductsListFragment newInstance() {
        return new MatchProductsListFragment();
    }

    @Override
    public void buildRecyclerView(@NonNull View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);
        adapter = new RecyclerViewMatchAdapter(getContext(), new ArrayList<Ad>(), activity);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        activity.loadProductList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProductsListFragment.OnFragmentInteractionListener) {
            activity = (MatchProductsListFragment.OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener
            extends ProductsListFragment.OnFragmentInteractionListener {

    }

}
