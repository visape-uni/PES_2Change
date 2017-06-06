package pes.twochange.presentation.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pes.twochange.R;

public class RegisterFragment extends Fragment {
    //Attributes
    private RegisterFragment.OnFragmentInteractionListener activity;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        final EditText mailText = (EditText)view.findViewById(R.id.mailField);
        final EditText passText = (EditText)view.findViewById(R.id.passField);
        final EditText repePassText = (EditText)view.findViewById(R.id.repeatPassField);

        //Next Step button + Pressed button listener
        Button nextBtn = (Button)view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Context context = getActivity();
                final String mail = mailText.getText().toString().trim();
                final String pass = passText.getText().toString().trim();
                String repePass = repePassText.getText().toString().trim();
                if (mail.isEmpty() || mailText.getText() == null) {
                    Toast.makeText(context, "Fill in the Email field", Toast.LENGTH_LONG).show();
                    mailText.requestFocus();
                } else if (pass.isEmpty() || passText.getText() == null) {
                    Toast.makeText(context, "Fill in the password field", Toast.LENGTH_LONG).show();
                    passText.requestFocus();
                } else if (repePass.isEmpty() || repePassText.getText() == null) {
                    Toast.makeText(context, "Fill in the repeated password field", Toast.LENGTH_LONG).show();
                    repePassText.requestFocus();
                } else if (!(repePassText.getText().toString().equals(passText.getText().toString()))) {
                    Toast.makeText(context, "Both passwords must be equal", Toast.LENGTH_LONG).show();
                    repePassText.setText("");
                    repePassText.requestFocus();
                } else {
                    activity.onCreateUser(mail, pass);
                }
            }
        });

        //Matches both passwords in real time and changes foreground color
        repePassText.addTextChangedListener((new TextWatcher(){
            public void afterTextChanged(Editable s) {
                if (!(repePassText.getText().toString().equals(passText.getText().toString()))) {
                    repePassText.setTextColor(Color.RED);
                } else {
                    repePassText.setTextColor(Color.BLACK);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        }));

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
        void onCreateUser(String mail, String password);
    }
}
