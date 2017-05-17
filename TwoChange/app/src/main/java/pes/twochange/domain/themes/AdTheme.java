package pes.twochange.domain.themes;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kredes on 03/05/2017.
 */

public class AdTheme {

    private Context context;
    private static DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://change-64bd0.firebaseio.com/").child("ads");


    public AdTheme(Context context) {
        this.context = context.getApplicationContext();
    }


}
