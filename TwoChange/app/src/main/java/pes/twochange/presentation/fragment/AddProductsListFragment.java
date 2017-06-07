package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pes.twochange.R;

public class AddProductsListFragment extends ProductsListFragment implements View.OnClickListener {

    private OnFragmentInteractionListener activity;

    public AddProductsListFragment() {
    }

    public static AddProductsListFragment newInstance() {
        return new AddProductsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_fragment_products_list, container, false);
        buildRecyclerView(view);
        FloatingActionButton addProduct =
                (FloatingActionButton) view.findViewById(R.id.add_product_fab);
        addProduct.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        activity.addProduct();
    }


    public interface OnFragmentInteractionListener
            extends ProductsListFragment.OnFragmentInteractionListener {
        void addProduct();
    }

}
