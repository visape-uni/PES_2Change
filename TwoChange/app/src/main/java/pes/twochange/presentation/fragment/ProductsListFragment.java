package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.adapter.RecyclerViewProductAdapter;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;

public class ProductsListFragment extends Fragment {

    private OnFragmentInteractionListener activity;
    private RecyclerView recyclerView;

    public ProductsListFragment() {
        // Required empty public constructor
    }

    public static ProductsListFragment newInstance() {
        return new ProductsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);

        return view;
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



    public void display(ArrayList<Ad> products) {
        RecyclerViewProductAdapter adapter = new RecyclerViewProductAdapter(getContext(), products,
                activity);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }



    public interface OnFragmentInteractionListener  extends OnRecyclerViewItemClickListener {

        void loadProductList();

    }

}
