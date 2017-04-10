package pes.twochange.domain.themes;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import pes.twochange.domain.model.ModelAdapter;
import pes.twochange.domain.model.Profile;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;

public class ProfileTheme implements ModelAdapter<Profile> {

    private Profile profile;

    public ProfileTheme(Profile profile) {
        this.profile = profile;
    }

    public ProfileTheme() {
    }

    public void updateProfile(final ProfileResponse profileResponse) {

        Firebase.getInstance().get(
                "profile",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        update();
                        profileResponse.success(profile);
                    }

                    @Override
                    public void empty() {
                        insert();
                        profileResponse.success(profile);
                    }

                    @Override
                    public void failure(String message) {
                        profileResponse.failure("Something went wrong :(");
                    }
                }
        ).by("uid", profile.getUid());
    }

    private void insert() {
        Firebase.getInstance().insert("profile", this);
    }

    private void update() {
        Firebase.getInstance().update("profile", profile.getId(), this);
    }


    public void get(final String uid, final ProfileResponse profileResponse) {
        Firebase.getInstance().get(
                "profile",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        Log.v("ProfileTheme", dataSnapshot.getValue().toString());
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        profile.setId(dataSnapshot.getKey());
                        profileResponse.success(profile);
                    }

                    @Override
                    public void empty() {
                        profileResponse.failure("No profile with uid = " + uid);
                    }

                    @Override
                    public void failure(String message) {
                        profileResponse.failure("Something went wrong :(");
                    }
                }
        ).by("uid", uid);

    }

    @Override
    public Class classType() {
        return Profile.class;
    }

    @Override
    public Profile object() {
        Profile result = profile;
        profile.setId(null);
        return result;
    }
}
