package pes.twochange.domain.model;

import com.google.firebase.database.Exclude;

public class Model {

    private String id;

    public Model() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }
}
