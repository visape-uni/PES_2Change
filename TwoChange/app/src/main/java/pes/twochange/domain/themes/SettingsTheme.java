package pes.twochange.domain.themes;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.LauncherApps;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import pes.twochange.domain.callback.BlockedResponse;
import pes.twochange.domain.callback.NotificationResponse;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Profile;
import pes.twochange.presentation.Config;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;
import pes.twochange.services.NotificationSender;

import static android.content.Context.MODE_PRIVATE;

public class SettingsTheme {
    private static SettingsTheme instance = new SettingsTheme();
    private Profile profile;
    private String myusername;
    private static DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://change-64bd0.firebaseio.com/").child("users_blocked");

    public static SettingsTheme getInstance() {return instance;}

    public static SettingsTheme getInstance(String username) {
        instance.myusername = username;
        return instance;
    }

    public void toggleNotifications(Profile pro){
        pro.setNotifications(!pro.getNotifications());
        ProfileTheme.getInstance(pro).updateProfile(new ProfileResponse() {
            @Override
            public void success(Profile profile) {

            }

            @Override
            public void failure(String s) {

            }
        });
    }


    public void sendNotification(final NotificationResponse callback){

        ProfileTheme.getInstance().get(myusername, new ProfileResponse() {
            @Override
            public void success(Profile profile) {
                callback.sendNotis(profile.getNotifications());

            }

            @Override
            public void failure(String s) {
            }
        });
    }

    public void blockUser(final String userblocked){
        DatabaseReference blocked = db.child(myusername).getRef();
        blocked.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(userblocked).exists()) {
                    dataSnapshot.child(userblocked).getRef().removeValue();
                    //Log.d("bloqueo","desbloqueado "+userblocked);
                }
                else {
                    DatabaseReference newBlockedRef = db;
                    newBlockedRef.child(myusername).child(userblocked).setValue("");
                   // Log.d("bloqueo","bloqueado "+userblocked);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void userIsBlocked(final String userblocked, final BlockedResponse callback) {
        DatabaseReference blocked = db.child(myusername).getRef();

        blocked.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                callback.isBlocked( dataSnapshot.child(userblocked).exists(), userblocked);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
