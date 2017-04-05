package pes.twochange.services;

import com.google.firebase.database.DataSnapshot;

public interface DatabaseResponse {

    void success(DataSnapshot dataSnapshot);
    void empty();
    void failure(String message);

}
