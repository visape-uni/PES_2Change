package pes.twochange.domain.themes;

import android.util.Log;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import pes.twochange.domain.Utils;
import pes.twochange.domain.model.Profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProfileThemeTest {

    private static final String LOG_TAG = "PROFILE_THEME_TEST_LOG";
    private CountDownLatch lock = new CountDownLatch(1);
    private Profile receivedProfile;

    @Test
    public void getCompleteProfile() throws InterruptedException {

        receivedProfile = null;

        String profileUId = "uid_here";

        Profile expectedProfile = new Profile(
                profileUId,
                "Fèlix",
                "Arribas",
                "image_id_here",
                new Profile.PhoneNumber(34, "605111655"),
                new Profile.Address(
                        "Tulipa 11",
                        "08193",
                        "Bellaterra",
                        "Barcelona",
                        "España"
                )
        );
        expectedProfile.setId("0");

        new ProfileTheme().get(
                profileUId,
                new ProfileResponse() {
                    @Override
                    public void success(Profile profile) {
                        receivedProfile = profile;
                    }

                    @Override
                    public void failure(String s) {
                    }
                }
        );

        lock.await(5, TimeUnit.SECONDS);

        assertNotNull("Timeout (5s) or no profile with UID = " + profileUId, receivedProfile);
        assertEquals("Those profiles should be equal", expectedProfile.toString(), receivedProfile.toString());
    }

    @Test
    public void getIncompleteProfile() throws InterruptedException {

        receivedProfile = null;

        String profileUId = "uid_there";

        Profile expectedProfile = new Profile(profileUId, "Incomplete", null, null, null, null);
        expectedProfile.setId("1");

        new ProfileTheme().get(
                profileUId,
                new ProfileResponse() {
                    @Override
                    public void success(Profile profile) {
                        receivedProfile = profile;
                    }

                    @Override
                    public void failure(String s) {
                    }
                }
        );

        lock.await(5, TimeUnit.SECONDS);

        assertNotNull("Timeout (5s) or no profile with UID = " + profileUId, receivedProfile);
        Log.v(LOG_TAG, receivedProfile.getId() + "");
        assertEquals("Those profiles should be equal", expectedProfile.toString(), receivedProfile.toString());
    }

    @Test
    public void getNotExistingProfile() throws InterruptedException {

        receivedProfile = null;

        String profileUId = "there_is_no_profile_with_this_uid";
        final String[] message = new String[1];

        new ProfileTheme().get(
                profileUId,
                new ProfileResponse() {
                    @Override
                    public void success(Profile profile) {
                        receivedProfile = null;
                    }

                    @Override
                    public void failure(String s) {
                        receivedProfile = new Profile();
                        message[0] = s;
                    }
                }
        );

        lock.await(5, TimeUnit.SECONDS);

        assertNotNull("Timeout (5s) or existing profile with UID = " + profileUId, receivedProfile);
        assertEquals("There is no profile with this UID", message[0], "No profile with uid = " + profileUId);



    }


    @Test
    public void updateNewProfile() throws InterruptedException {

        receivedProfile = null;
        final String randomUID = Utils.generateRandomWord(10);

        new ProfileTheme().get(
                randomUID,
                new ProfileResponse() {
                    @Override
                    public void success(Profile profile) {
                        receivedProfile = profile;
                    }

                    @Override
                    public void failure(String s) {
                        // Tiene que fallar para asegurarnos que el perfil no existe
                        final Profile newProfile = new Profile(randomUID, null, null, null, null, null);
                        new ProfileTheme(newProfile).updateProfile(
                                new ProfileResponse() {
                                    @Override
                                    public void success(Profile profile) {
                                        new ProfileTheme().get(
                                                newProfile.getUid(),
                                                new ProfileResponse() {
                                                    @Override
                                                    public void success(Profile profile) {
                                                        // Este es el nuevo perfil insertado
                                                        receivedProfile = profile;
                                                    }

                                                    @Override
                                                    public void failure(String s) {

                                                    }
                                                }
                                        );
                                    }

                                    @Override
                                    public void failure(String s) {

                                    }
                                }
                        );

                    }
                }
        );

        lock.await(5, TimeUnit.SECONDS);
        assertNotNull("Timeout (5s) or new  profile was not created", receivedProfile);
        assertEquals("New profile have been inserted", randomUID, receivedProfile.getUid());

    }

    @Test
    public void udpateExistingProfile() throws InterruptedException {

        receivedProfile = null;
        final String uid = "update_my_name_and_zipCode";
        final String name = Utils.generateRandomWord(10);
        final String zipCode = Utils.generateRandomWord(5);

        new ProfileTheme().get(
                uid,
                new ProfileResponse() {
                    @Override
                    public void success(Profile profile) {

                        profile.setName(name);
                        profile.getAddress().setZipCode(zipCode);

                        new ProfileTheme(profile).updateProfile(
                                new ProfileResponse() {
                                    @Override
                                    public void success(Profile profile) {
                                        new ProfileTheme().get(
                                                uid,
                                                new ProfileResponse() {
                                                    @Override
                                                    public void success(Profile profile) {
                                                        receivedProfile = profile;
                                                    }

                                                    @Override
                                                    public void failure(String s) {

                                                    }
                                                }
                                        );
                                    }

                                    @Override
                                    public void failure(String s) {

                                    }
                                }
                        );
                    }

                    @Override
                    public void failure(String s) {

                    }
                }
        );

        lock.await(5, TimeUnit.SECONDS);
        assertNotNull("Timeout (5s) or new  profile was not updated", receivedProfile);
        assertEquals("Name updated", name, receivedProfile.getName());
        assertEquals("Zip Code updated", zipCode, receivedProfile.getAddress().getZipCode());

    }


}