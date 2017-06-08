package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import pes.twochange.R;

public class LoginFragment extends Fragment {
    //Attributes
    private OnFragmentInteractionListener activity;

    //Constructor
    public LoginFragment() { /* Required empty public constructor*/ }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText emailField = (EditText) view.findViewById(R.id.mailField);
        final EditText passwordField = (EditText) view.findViewById(R.id.passwordField);

        //Login Button pressed listener
        Button loginButton = (Button) view.findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                activity.onLoginClick(email, password);
            }
        });

        //Google button pressed listener
        Button googleBtn = (Button) view.findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                activity.onLoginWithGoogle();
            }
        });

        //New user button pressed listener
        Button newUserBtn = (Button) view.findViewById(R.id.newUserBtn);
        newUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                activity.onRegisterClick();
            }
        });
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

    //Methods implemented by the host activity
    public interface OnFragmentInteractionListener {
        void onLoginClick(String email, String password);
        void onRegisterClick();
        void onLoginWithGoogle();
    }
}
