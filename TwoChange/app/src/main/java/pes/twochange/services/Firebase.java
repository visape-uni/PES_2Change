package pes.twochange.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

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

    // Get [name] following finder instruction and get the result in the callback
    public Finder get(String name, DatabaseResponse callback) {
        return new Finder(db.getReference().child(name), callback);
    }

    // Update [name] with key = [id]. The model type is described in the model adapter
    public void update(String name, String id, ModelAdapter model) {
        DatabaseReference ref = db.getReference(name);
        Map values = new HashMap<>();
        values.put(id, model.object());
        ref.updateChildren(values);
    }

    // Insert [name]. Then we return the new random ID. The model type is described in the model adapter
    public String insert(String name, ModelAdapter model) {
        DatabaseReference ref = db.getReference(name);
        DatabaseReference newRef = ref.push();
        String id = newRef.getKey();
        ref.child(id).setValue(model.object());
        return id;
    }

    // Insert [name] with ID = [key]. The model type is described in the model adapter
    public void insert(String name, String key, ModelAdapter model) {
        DatabaseReference ref = db.getReference(name);
        ref.child(key).setValue(model.object());
    }

    // Delete [name] with key = [ID]
    public void delete(String name, String id) {
        DatabaseReference ref = db.getReference(name);
        ref.child(id).removeValue();
    }
}
