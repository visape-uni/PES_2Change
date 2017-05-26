package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Product;
import pes.twochange.presentation.adapter.RecyclerViewAdAdapter;
import pes.twochange.presentation.adapter.RecyclerViewWantedAdapter;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdListFragment extends Fragment {


    private OnFragmentInteractionListener activity;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayoutManager layoutManager;

    public AdListFragment() {
        // Required empty public constructor
    }

    public static AdListFragment newInstance() {
        return new AdListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // create adapter and list listeners here
        View view = inflater.inflate(R.layout.fragment_ad_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.ad_list);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        final Button match = (Button) view.findViewById(R.id.match_button);
        match.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.match();
                    }
                }
        );

        activity.getProductList(this, false);

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

    private RecyclerViewAdAdapter adAdapter;
    private RecyclerViewWantedAdapter wantedAdapter;

    private void setAdapter(RecyclerView.Adapter adapter) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void notifyDataSetChanged() {
        if (adAdapter != null) {
            adAdapter.notifyDataSetChanged();
        }
        if (wantedAdapter != null) {
            wantedAdapter.notifyDataSetChanged();
        }
    }


    public void responseAds(ArrayList<Ad> ads) {
        adAdapter = new RecyclerViewAdAdapter(ads, activity, activity);
        setAdapter(adAdapter);
    }

    public void responseProducts(ArrayList<Product> products) {
        wantedAdapter = new RecyclerViewWantedAdapter(products, activity, activity);
        setAdapter(wantedAdapter);
    }

    public void error(String errorMessage) {
        // TODO error message
    }

    public interface OnFragmentInteractionListener extends OnRecyclerViewItemClickListener,
            OnRecyclerViewItemLongClickListener {

        void getProductList(AdListFragment response, boolean force);
        void match();

    }

}
