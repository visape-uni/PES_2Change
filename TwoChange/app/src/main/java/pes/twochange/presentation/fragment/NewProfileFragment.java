package pes.twochange.presentation.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pes.twochange.R;
import pes.twochange.domain.model.Profile;



public class NewProfileFragment extends Fragment {
    //Attributes
    private OnFragmentInteractionListener activity;

    //Constructor
    public NewProfileFragment() { /* Required empty public constructor*/ }

    public static NewProfileFragment newInstance() {
        return new NewProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_profile, container, false);

        final EditText usernameText = (EditText)view.findViewById(R.id.usernameField);
        final EditText nameText = (EditText)view.findViewById(R.id.nameField);
        final EditText surnameText = (EditText)view.findViewById(R.id.surnameField);
        final EditText phoneText = (EditText)view.findViewById(R.id.phoneField);
        final EditText addressText = (EditText)view.findViewById(R.id.addressField);
        final EditText cityText = (EditText)view.findViewById(R.id.cityField);
        final EditText zipText = (EditText)view.findViewById(R.id.zipField);
        final EditText stateText = (EditText)view.findViewById(R.id.stateField);
        final EditText countryText = (EditText)view.findViewById(R.id.countryField);

        //Set profile button listener
        Button setProfileBtn = (Button)view.findViewById(R.id.FinishBtn);
        setProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final String username = usernameText.getText().toString().trim();
                final String name = nameText.getText().toString().trim();
                final String surname = surnameText.getText().toString().trim();
                final String phone = phoneText.getText().toString().trim();
                final String addressStr = addressText.getText().toString().trim();
                final String zip = zipText.getText().toString().trim();
                final String city = cityText.getText().toString().trim();
                final String state = stateText.getText().toString().trim();
                final String country = countryText.getText().toString().trim();
                if (name.isEmpty() || nameText.getText() == null) {
                    Toast.makeText(getActivity(), "Please fill in the name field", Toast.LENGTH_LONG).show();
                } else if (surname.isEmpty() || surnameText.getText() == null) {
                    Toast.makeText(getActivity(), "Please fill in the surname field", Toast.LENGTH_LONG).show();
                } else if (username.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in the username field", Toast.LENGTH_LONG).show();
                } else {
                    Profile profile = new Profile(
                            username,
                            null, //The uid is determined on the AuthActivity
                            name,
                            surname,
                            phone,
                            new Profile.Address(addressStr, zip, city, state, country)
                    );
                    activity.onSetProfileClick(profile);
                }
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
        void onSetProfileClick(Profile profile);
    }
}
