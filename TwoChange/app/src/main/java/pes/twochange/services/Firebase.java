package pes.twochange.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import pes.twochange.domain.Utils;
import pes.twochange.domain.model.ModelAdapter;

public class Firebase {

    private static Firebase instance;
    private FirebaseDatabase db;

    private Firebase() {
        db = FirebaseDatabase.getInstance();
    }

    public static Firebase getInstance() {
        if (instance == null) instance = new Firebase();
        return instance;
    }

    public Finder get(String name, DatabaseResponse callback) {
        return new Finder(db.getReference().child(name), callback);
    }

    public void update(String name, String id, ModelAdapter model) {
        DatabaseReference ref = db.getReference(name);
        Map values = new HashMap<>();
        values.put(id, model.object());
        ref.updateChildren(values);
    }

    public String insert(String name, ModelAdapter model) {
        String id = Utils.randomID();
        DatabaseReference ref = db.getReference(name);
        ref.child(id).setValue(model.object());
        return id;
    }

    public void insert(String name, String key, ModelAdapter model) {
        DatabaseReference ref = db.getReference(name);
        ref.child(key).setValue(model.object());
    }

    public void delete(String name, String id) {
        DatabaseReference ref = db.getReference(name);
        ref.child(id).removeValue();
    }
}
