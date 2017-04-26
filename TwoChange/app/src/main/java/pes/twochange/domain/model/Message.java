package pes.twochange.domain.model;

import java.util.Date;

/**
 * Created by Victor on 06/04/2017.
 */

public class Message {

    private String messageSender;
    private String messageReciver;
    private String messageContent;
    private long messageTime;

    public Message (String messageContent, String messageSender, String messageReciver) {
        this.messageContent = messageContent;
        this.messageReciver = messageReciver;
        this.messageSender = messageSender;

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

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public String getMessageReciver() {
        return messageReciver;
    }

    public void setMessageReciver(String messageReciver) {
        this.messageReciver = messageReciver;
    }
}
