package pes.twochange.presentation.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import pes.twochange.R;
import pes.twochange.presentation.controller.ProfileActivity;
import pes.twochange.services.ImageManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatProductFragment extends ProductFragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM6 = "username";

    protected String username;

    private OnFragmentInteractionListener activity;

    public ChatProductFragment() {
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
     * @return A new instance of fragment ProductFragment.
     */
    public static ChatProductFragment newInstance(String name, String description, String category,
                                                  int rating, ArrayList<String> images, String username) {
        ChatProductFragment fragment = new ChatProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, category);
        args.putInt(ARG_PARAM4, rating);
        args.putStringArrayList(ARG_PARAM5, images);
        args.putString(ARG_PARAM6, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM6);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;
        LinearLayout content = (LinearLayout) view.findViewById(R.id.content_layout);
        View chatView = inflater.inflate(R.layout.chat_product_layout, null);
        final TextView usernameTextView = (TextView) chatView.findViewById(R.id.user_username);
        usernameTextView.setText(username);
        usernameTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra("usernameProfile", username);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
        );
        CircleImageView userImage = (CircleImageView) chatView.findViewById(R.id.user_image);
        String imagePath = String.format("profiles/%s.jpg", username);
        ImageManager.getInstance().putImageIntoView(imagePath, getContext(), userImage);
        String path = String.format("profile/%s.jpg", username);
        ImageManager.getInstance().putImageIntoView(path, getContext(), userImage);
        Button chatButton = (Button) chatView.findViewById(R.id.user_action);
        chatButton.setOnClickListener(this);

        content.addView(chatView);
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
        }
    }

    public interface OnFragmentInteractionListener extends ProductFragment.OnFragmentInteractionListener {
        void chat(String username);
    }
}
