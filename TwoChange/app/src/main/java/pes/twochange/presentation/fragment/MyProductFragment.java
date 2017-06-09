package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import pes.twochange.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProductFragment extends ProductFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener activity;

    public MyProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Parameter 1.
     * @param description Parameter 2.
     * @param category Parameter 3.
     * @param rating Parameter 4.
     * @param images Parameter 5.
     * @return A new instance of fragment ProductFragment.
     */
    public static MyProductFragment newInstance(String name, String description, String category,
                                                int rating, ArrayList<String> images) {
        MyProductFragment fragment = new MyProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, category);
        args.putInt(ARG_PARAM4, rating);
        args.putStringArrayList(ARG_PARAM5, images);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;
        LinearLayout content = (LinearLayout) view.findViewById(R.id.content_layout);
        View editButtonView = inflater.inflate(R.layout.edit_buttond, null);
        Button editButton = (Button) editButtonView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.edit();
                    }
                }
        );
        content.addView(editButtonView);
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

    public interface OnFragmentInteractionListener extends ProductFragment.OnFragmentInteractionListener {
        void edit();
    }
}
