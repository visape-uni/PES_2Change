package pes.twochange.domain.themes;


import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pes.twochange.domain.model.Profile;
import pes.twochange.presentation.Config;

import static android.content.Context.MODE_PRIVATE;

public class SettingsTheme {
    private static SettingsTheme instance = new SettingsTheme();
    private Profile myProfile;
    private String myusername;
    private SharedPreferences sharedPreferences;
    private static DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://change-64bd0.firebaseio.com/").child("users_blocked");

    public void getSharedPreferences(SharedPreferences sharedPreferences){
        this.sharedPreferences = sharedPreferences;
    }

    public void blockUser(String userblocked){
        //SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        final String currentUsername = sharedPreferences.getString("username", null);
        String blocked = db.child(currentUsername).child(userblocked).toString();
        if(blocked == null) {
            DatabaseReference newBlockedRef = db.push();
            newBlockedRef.setValue(userblocked);
            Log.d("bloqueado",userblocked);
        }
        else {

        }
    }

}
