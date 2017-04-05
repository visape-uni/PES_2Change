package pes.twochange.services;

import com.google.firebase.database.FirebaseDatabase;

import pes.twochange.domain.model.Model;

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

    public void update(String name, String id, Model model, DatabaseResponse callback) {
        // TODO Metode general per fer update de l'entitat "name" amb id = "id" i canviarla per "model"
    }

    public void insert(String name, Model model, DatabaseResponse callback) {
        // TODO Metode general per fer insert de l'entitat "name" amb "model"
    }

    public void delete(String name, String id, DatabaseResponse callback) {
        // TODO Metode general per fer borrar de l'entitat "name" amb id = "id"
    }
}
