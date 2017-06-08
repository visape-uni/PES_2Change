package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import pes.twochange.R;

/**
 * Created by Visape on 08/06/2017.
 */

public class AddWantedCategoryListFragment extends ProductsListFragment implements View.OnClickListener {

    private OnFragmentInteractionListener activity;

    public AddWantedCategoryListFragment() {}

    public static AddWantedCategoryListFragment newInstance() {
        return new AddWantedCategoryListFragment();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View view = inflater.inflate(R.layout.fragment_add_wanted_category, container, false);
        buildRecyclerView(view);
        FloatingActionButton addCategory = (FloatingActionButton) view.findViewById(R.id.add_wanted_product);
        addCategory.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            activity = (OnFragmentInteractionListener) context;
        } else {
            throw  new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onClick (View v) {
        //TODO
        activity.addProduct();
    }

    public interface OnFragmentInteractionListener extends ProductsListFragment.OnFragmentInteractionListener {
        void addProduct();
    }


}
