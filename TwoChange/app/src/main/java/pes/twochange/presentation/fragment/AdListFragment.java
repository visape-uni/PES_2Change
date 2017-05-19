package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.presentation.adapter.RecyclerViewProductAdapter;
import pes.twochange.presentation.model.ProductItem;
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

    private static final String ARG_PARAM1 = "user";
    private static final String ARG_PARAM2 = "list";
    private String user;
    private String list;


    private OnFragmentInteractionListener listener;
    private RecyclerView recyclerView;

    public AdListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @param list Parameter 2.
     * @return A new instance of fragment AdListFragment.
     */
    public static AdListFragment newInstance(String user, String list) {
        AdListFragment fragment = new AdListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, user);
        args.putString(ARG_PARAM2, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.user = getArguments().getString(ARG_PARAM1);
            this.list = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // create adapter and list listeners here
        View view = inflater.inflate(R.layout.fragment_ad_list, container, false);
        // TODO progress bar
        recyclerView = (RecyclerView) view.findViewById(R.id.ad_list);
        listener.getProductList(
            new ActivityResponse() {
                @Override
                public void response(ArrayList<ProductItem> ads) {
                    // TODO progress bar
                    recyclerView.setAdapter(new RecyclerViewProductAdapter(ads, listener, listener));
                }

                @Override
                public void error(String errorMessage) {
                    // TODO error message
                }
            }
        );



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener extends OnRecyclerViewItemClickListener,
            OnRecyclerViewItemLongClickListener {

        void getProductList(ActivityResponse response);

    }

    public interface ActivityResponse {
        void response(ArrayList<ProductItem> ads);
        void error(String errorMessage);
    }

}
