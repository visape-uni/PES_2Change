package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pes.twochange.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MatchProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MatchProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchProductFragment extends ChatProductFragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM7 = "Status";
    private static final String ARG_PARAM8 = "offered";

    protected int state;
    protected String offered;

    private OnFragmentInteractionListener activity;

    public MatchProductFragment() {
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
     * @param username Parameter 6.
     * @param state Parameter 7.
     * @param offered Parameter 8.
     * @return A new instance of fragment ProductFragment.
     */
    public static MatchProductFragment newInstance(String name, String description, String category,
                                                   int rating, ArrayList<String> images,
                                                   String username, int state, String offered) {
        MatchProductFragment fragment = new MatchProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, category);
        args.putInt(ARG_PARAM4, rating);
        args.putStringArrayList(ARG_PARAM5, images);
        args.putString(ARG_PARAM6, username);
        args.putInt(ARG_PARAM7, state);
        args.putString(ARG_PARAM8, offered);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            state = getArguments().getInt(ARG_PARAM7);
            offered = getArguments().getString(ARG_PARAM8);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;
        LinearLayout content = (LinearLayout) view.findViewById(R.id.content_layout);
        View matchButtons = inflater.inflate(R.layout.match_button, null);
        Button acceptButton = (Button) matchButtons.findViewById(R.id.accept_button);
        Button declineButton = (Button) matchButtons.findViewById(R.id.decline_button);
        TextView question = (TextView) matchButtons.findViewById(R.id.question_offered);
        if (state != 0) {
            question.setVisibility(View.GONE);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
        } else {
            question.setText("Change " + name + " for " + offered + "?");
            acceptButton.setOnClickListener(this);
            declineButton.setOnClickListener(this);
        }
        content.addView(matchButtons);
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
        switch (v.getId()) {
            case R.id.user_action:
                activity.chat(username);
                break;

            case R.id.accept_button:
                activity.accept();
                break;

            case R.id.decline_button:
                activity.decline();
                break;
        }
    }

    public interface OnFragmentInteractionListener extends ChatProductFragment.OnFragmentInteractionListener {
        void accept();
        void decline();
    }
}
