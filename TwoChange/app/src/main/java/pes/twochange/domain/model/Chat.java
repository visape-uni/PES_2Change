package pes.twochange.domain.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Visape on 07/04/2017.
 */

public class Chat implements Serializable{
    private ArrayList<Message> messages;
    private String messageSender;
    private String messageReciver;

    public Chat(String messageSender, String messageReciver) {
        this.messageReciver = messageReciver;
        this.messageSender = messageSender;
        this.messages = new ArrayList<>();
    }

    public Chat() {}

    public String getMessageReciver() {
        return messageReciver;
    }

    public void setMessageReciver(String messageReciver) {
        this.messageReciver = messageReciver;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
