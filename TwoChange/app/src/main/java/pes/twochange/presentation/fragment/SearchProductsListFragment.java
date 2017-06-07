package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import pes.twochange.R;

public class SearchProductsListFragment extends ProductsListFragment implements TextWatcher {

    private OnFragmentInteractionListener activity;

    public SearchProductsListFragment() {
    }

    public static SearchProductsListFragment newInstance() {
        return new SearchProductsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_products_list, container, false);
        buildRecyclerView(view);
        EditText editText = (EditText) view.findViewById(R.id.search_edit_text);
        editText.addTextChangedListener(this);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        String query = s.toString();
        if (query.length() == 0) {
            activity.loadProductList();
        } else {
            activity.searchProducts(query);
        }
    }

    public interface OnFragmentInteractionListener
            extends ProductsListFragment.OnFragmentInteractionListener {
        void searchProducts(String query);

    }

}
