package pes.twochange.domain.model;

import java.util.Date;

/**
 * Created by Victor on 06/04/2017.
 */

public class Message {

    private String messageContent;
    private long messageTime;

    public Message (String messageContent) {
        this.messageContent = messageContent;

        messageTime = new Date().getTime();
    }

    public Message() {
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
