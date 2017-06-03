package pes.twochange.presentation.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.controller.ProfileActivity;

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
                        phoneEditText.setText(profile.getPhoneNumber().toString());
                        addressEditText.setText(profile.getAddress().getAddress());
                        zicCodeEditText.setText(profile.getAddress().getZipCode());
                        cityEditText.setText(profile.getAddress().getTown());
                        stateEditText.setText(profile.getAddress().getState());
                        countryEditText.setText(profile.getAddress().getCountry());
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
                ProfileTheme.getInstance(profile).updateProfile(
                        new ProfileResponse() {
                            @Override
                            public void success(Profile profile) {
                                ProfileActivity activity = (ProfileActivity) getActivity();
                                activity.update();
                            }

                            @Override
                            public void failure(String s) {
                            }
                        });
            }
        });
    }
}
