package pes.twochange.domain.model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Victor on 01/05/2017.
 */

public class Product {
    String name;
    String key;
    String username;

    private static DatabaseReference mFirebaseAds = FirebaseDatabase.getInstance().getReference().child("ads");

    public Product() {
    }

    public Product(String name, String key, String username) {
        this.name = name;
        this.key = key;
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
