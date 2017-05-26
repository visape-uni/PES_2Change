package pes.twochange.domain.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Victor on 01/05/2017.
 */

public class Product {
    private String name;
    private String key;
    private String username;
    private String category;
    private int rating;

    private static DatabaseReference mFirebaseAds = FirebaseDatabase.getInstance().getReference().child("ads");

    public Product() {
    }

    public Product(String name, String key, String username, String category, int rating) {
        this.name = name;
        this.key = key;
        this.username = username;
        this.category = category;
        this.rating = rating;
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

    public String getCategory () {
        return this.category;
    }

    public void setCategory (String category) {
        this.category = category;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating (int rating) {
        this.rating = rating;
    }
}
