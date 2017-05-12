package pes.twochange.services;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.NoSuchElementException;

public class Finder {

    private DatabaseReference ref;
    private DatabaseResponse callback;

    Finder(DatabaseReference ref, DatabaseResponse callback) {
        this.ref = ref;
        this.callback = callback;
    }

    // Get the entire list
    public void list() {
        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        responseResult(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.failure(databaseError.getMessage());
                    }
                }
        );
    }

    // Get one element with that ID (Firebase only allows one)
    public void byId(final String id) {
        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            if (childSnapshot.getKey().equals(id)) {
                                responseResult(childSnapshot);
                                return;
                            }
                        }
                        callback.empty();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.failure(databaseError.getMessage());
                    }
                }
        );
    }

    // Get first with [key] equal [value]
    public void by(String key, String value) {
        Query queryReference = ref.orderByChild(key).equalTo(value);
        queryReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            responseResult(dataSnapshot.getChildren().iterator().next());
                        } catch (NoSuchElementException exception) {
                            responseResult(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.failure(databaseError.getMessage());
                    }
                }
        );
    }

    // Search with the value of [key] similar to [value]
    // with("name", "tomato") --> Search with value of name similar to tomato
    public void with(String key, String value) {
        char lastChar = value.charAt(value.length() - 1);
        char newLastChar = (char) (lastChar + 1);
        String lastValue = value.substring(0, value.length() - 1);
        lastValue = lastValue + newLastChar;
        //Log.d("SEAAARCH:", lastValue);
        Query queryReference = ref.orderByChild(key).startAt(value).endAt(lastValue);
        queryReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        responseResult(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.failure(databaseError.getMessage());
                    }
                }
        );
    }

    private void responseResult(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) callback.empty();
        else callback.success(dataSnapshot);
    }

}
