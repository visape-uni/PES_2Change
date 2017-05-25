package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.adapter.RecyclerViewProductAdapter;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;

public class ProductsListFragment extends Fragment implements TextWatcher {

    private static final String ARG_PARAM1 = "search_enabled";
    private boolean searchEnabled;

    private OnFragmentInteractionListener activity;
    private RecyclerView recyclerView;
    private RecyclerViewProductAdapter adapter;

    public ProductsListFragment() {
    }

    public static ProductsListFragment newInstance(boolean param1) {
        ProductsListFragment fragment = new ProductsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchEnabled = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);
        adapter = new RecyclerViewProductAdapter(getContext(),
                new ArrayList<Ad>(), activity);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2,
                LinearLayoutManager.VERTICAL, false);
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        EditText editText = (EditText) view.findViewById(R.id.search_edit_text);
        if (searchEnabled) {
            editText.addTextChangedListener(this);
        } else {
            editText.setVisibility(View.GONE);
        }
        activity.loadProductList();
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
        adapter.setProductArrayList(products);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
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


    public interface OnFragmentInteractionListener  extends OnRecyclerViewItemClickListener {

        void loadProductList();
        void searchProducts(String query);
        Toolbar getToolbar();

    }

}
