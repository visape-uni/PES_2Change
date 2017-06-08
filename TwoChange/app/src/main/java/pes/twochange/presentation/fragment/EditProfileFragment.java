package pes.twochange.presentation.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.ModelAdapter;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.controller.ProfileActivity;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;

/**
 * Created by Victor on 03/06/2017.
 */

public class EditProfileFragment extends Fragment {

    private EditText nameEditText, surnameEditText, phoneEditText, addressEditText, zicCodeEditText, cityEditText, stateEditText, countryEditText;
    private Button saveBtn;

    private String usernameProfile;
    private Profile profile;

    public EditProfileFragment() {
    }

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        usernameProfile = getArguments().getString("usernameProfile");
        buildView(view);
        return view;
    }

    protected void buildView(@NonNull View view) {
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        surnameEditText = (EditText) view.findViewById(R.id.SurnameEditText);
        phoneEditText = (EditText) view.findViewById(R.id.PhoneEditText);
        addressEditText = (EditText) view.findViewById(R.id.AddressEditText);
        zicCodeEditText = (EditText) view.findViewById(R.id.zipCodeEditText);
        cityEditText = (EditText) view.findViewById(R.id.CityEditText);
        stateEditText = (EditText) view.findViewById(R.id.stateEditText);
        countryEditText = (EditText) view.findViewById(R.id.CountryEditText);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);

        ProfileTheme.getInstance().get(
                usernameProfile,
                new ProfileResponse() {
                    @Override
                    public void success(Profile p) {
                        profile = p;
                        nameEditText.setText(profile.getName());
                        surnameEditText.setText(profile.getSurname());
                        if (profile.getPhoneNumber() != null) phoneEditText.setText(profile.getPhoneNumber().toString());
                        if (profile.getAddress() != null) {
                            addressEditText.setText(profile.getAddress().getAddress());
                            zicCodeEditText.setText(profile.getAddress().getZipCode());
                            cityEditText.setText(profile.getAddress().getTown());
                            stateEditText.setText(profile.getAddress().getState());
                            countryEditText.setText(profile.getAddress().getCountry());
                        }
                    }

                    @Override
                    public void failure(String s) {
                        // TODO Control d'errors
                    }
                }
        );

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: que pasa quan no hi ha num de telefon o adressa?
                profile.setName(nameEditText.getText().toString().trim());
                profile.setSurname(surnameEditText.getText().toString().trim());

                profile.setPhoneNumber(phoneEditText.getText().toString().trim());

                String address = addressEditText.getText().toString().trim();
                String zipCode = zicCodeEditText.getText().toString().trim();
                String city = cityEditText.getText().toString().trim();
                String state = stateEditText.getText().toString().trim();
                String country = countryEditText.getText().toString().trim();
                profile.setAddress(new Profile.Address(address, zipCode, city, state, country));

                ModelAdapter<Profile> model = new ModelAdapter<Profile>() {
                    @Override
                    public Class classType() {
                        return Profile.class;
                    }

                    @Override
                    public Profile object() {
                        return profile;
                    }
                };

                Firebase.getInstance().update("profile", profile.getUsername(), model);

                ProfileActivity activity = (ProfileActivity) getActivity();
                activity.update(profile);
            }
        });
    }
}
