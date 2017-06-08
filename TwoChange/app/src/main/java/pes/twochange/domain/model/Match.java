package pes.twochange.domain.model;

import com.google.firebase.database.Exclude;

public class Match {

    public final static int UNDEFINED_INT = 0;
    public final static int ACCEPTED_INT = 1;
    public final static int DENIED_INT = -1;

    @Exclude
    public int getStatusInt() {
        switch (status) {
            case ACCEPTED:
                return ACCEPTED_INT;
            case DENIED:
                return DENIED_INT;
            default:
                return UNDEFINED_INT;
        }
    }

    public enum Status {
        UNDEFINED,
        ACCEPTED,
        DENIED
    }

    private String usernameSender;
    private String usernameReceiver;
    private String productKeySender;
    private String productKeyReceiver;
    private String categoryProductReceiver;

    private Status status;


    //CONSTRUCTOR
    public Match(String usernameSender, String usernameReceiver, String productKeySender,
                 String productKeyReceiver, String categoryProductReceiver) {
        this.usernameSender = usernameSender;
        this.usernameReceiver = usernameReceiver;
        this.productKeySender = productKeySender;
        this.productKeyReceiver = productKeyReceiver;
        this.categoryProductReceiver = categoryProductReceiver;
        this.status = Status.UNDEFINED;
    }

    public Match() {}

    //GETTERS y SETTERS

    public String getUsernameSender() {
        return usernameSender;
    }

    public void setUsernameSender(String usernameSender) {
        this.usernameSender = usernameSender;
    }

    public String getUsernameReceiver() {
        return usernameReceiver;
    }

    public void setUsernameReceiver(String usernameReceiver) {
        this.usernameReceiver = usernameReceiver;
    }

    public String getProductKeySender() {
        return productKeySender;
    }

    public void setProductKeySender(String productKeySender) {
        this.productKeySender = productKeySender;
    }

    public String getProductKeyReceiver() {
        return productKeyReceiver;
    }

    public void setProductKeyReceiver(String productKeyReceiver) {
        this.productKeyReceiver = productKeyReceiver;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCategoryProductReceiver() {
        return categoryProductReceiver;
    }

    public void setCategoryProductReceiver(String categoryProductReceiver) {
        this.categoryProductReceiver = categoryProductReceiver;
    }

}
