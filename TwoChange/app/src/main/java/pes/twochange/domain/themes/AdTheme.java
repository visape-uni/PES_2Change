package pes.twochange.domain.themes;

import com.google.firebase.database.FirebaseDatabase;

public class AdTheme {

    private static final AdTheme ourInstance = new AdTheme();
    private static final String REFERENCE = "lists";
    private static String user;

    public static AdTheme getInstance() {
        return ourInstance;
    }

    public void remove(String username, String list, String key) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(REFERENCE)
                .child(username)
                .child(list)
                .child(key)
                .removeValue();
    }



}
