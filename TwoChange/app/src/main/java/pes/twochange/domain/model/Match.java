package pes.twochange.domain.model;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Visape on 10/05/2017.
 */

public class Match {

    public enum state {
        UNDEFINED,
        ACCEPTED,
        DENIED
    }

    private String usernameSender;
    private String usernameReciver;
    private String productKeySender;
    private String productKeyReciver;

    private state state;

    private static DatabaseReference mFirebaseMatches = FirebaseDatabase.getInstance().getReference().child("matches");

    //CONSTRUCTOR
    public Match(String usernameSender, String usernameReciver, String productKeySender, String productKeyReciver) {
        this.usernameSender = usernameSender;
        this.usernameReciver = usernameReciver;
        this.productKeySender = productKeySender;
        this.productKeyReciver = productKeyReciver;
        this.state = state.UNDEFINED;
    }

    //GETTERS y SETTERS

    public String getUsernameSender() {
        return usernameSender;
    }

    public void setUsernameSender(String usernameSender) {
        this.usernameSender = usernameSender;
    }

    public String getUsernameReciver() {
        return usernameReciver;
    }

    public void setUsernameReciver(String usernameReciver) {
        this.usernameReciver = usernameReciver;
    }

    public String getProductKeySender() {
        return productKeySender;
    }

    public void setProductKeySender(String productKeySender) {
        this.productKeySender = productKeySender;
    }

    public String getProductKeyReciver() {
        return productKeyReciver;
    }

    public void setProductKeyReciver(String productKeyReciver) {
        this.productKeyReciver = productKeyReciver;
    }

    public Match.state getState() {
        return state;
    }

    public void setState(Match.state state) {
        this.state = state;
    }

    //OTHER METHODS
    public void save() {
        //usernameSender = nom del usuari que ha iniciat el match
        //productKeyReciver = key del producte del usuari que NO ha iniciat del match
        DatabaseReference newMatchRef = mFirebaseMatches.child(usernameSender).child(productKeyReciver);
        newMatchRef.setValue(this);
    }
}
