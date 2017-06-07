package pes.twochange.domain.themes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Message;
import pes.twochange.domain.model.Profile;
import pes.twochange.services.NotificationSender;

/**
 * Created by Adrian on 01/06/2017.
 */

public class ChatTheme {
    private static ChatTheme instance = new ChatTheme();
    private Profile profile;
    private String userSender;
    private String userReciver;
    private Chat chat;
    private DatabaseReference mFirebaseChatRefSender;
    private DatabaseReference mFirebaseChatRefReciver;

    public static ChatTheme getInstance() {
        return instance;
    }

    public static ChatTheme getInstance(Chat chat) {
        instance.chat = chat;
        instance.userReciver = chat.getMessageReciver();
        instance.userSender = chat.getMessageSender();

        return instance;
    }

    public void openChat() {

    }

    public void sendChatMessage(String content) {
        //If the message is not empty send it to the DBs
        new Message(content, userSender, userReciver).send();

        //Send notification for the reciver
        NotificationSender n = new NotificationSender();
        n.sendNotification(userSender);
    }

    public void sendContactDetails() {
        ProfileTheme.getInstance().get(userSender, new ProfileResponse() {
            @Override
            public void success(Profile p) {
                String message = "";
                Profile.Address address = p.getAddress();
                Profile.PhoneNumber phonenumber = p.getPhoneNumber();
                if(address != null) {
                    message = "Address: " + address.toString();
                }
                if (phonenumber != null) {
                    message += "\n" + "Phone Number: " + phonenumber.toString();
                }
                if(address != null || phonenumber != null) {
                    ChatTheme.getInstance(chat).sendChatMessage(message);
                }

            }

            @Override
            public void failure(String s) {
                // TODO Control d'errors
            }
        });


    }


}
