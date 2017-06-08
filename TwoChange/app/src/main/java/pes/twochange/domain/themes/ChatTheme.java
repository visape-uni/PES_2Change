package pes.twochange.domain.themes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import pes.twochange.domain.callback.NotificationResponse;
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

    public static ChatTheme getInstance() {
        return instance;
    }

    public static ChatTheme getInstance(Chat chat) {
        instance.chat = chat;
        instance.userReciver = chat.getMessageReciver();
        instance.userSender = chat.getMessageSender();

        return instance;
    }

    public void sendChatMessage(String content) {
        if (!content.isEmpty()) {
            new Message(content, userSender, userReciver).send();
            SettingsTheme.getInstance(userReciver).sendNotification(new NotificationResponse() {
                @Override
                public void sendNotis(boolean notifications) {
                    if(notifications) {
                        NotificationSender n = new NotificationSender();
                        n.sendNotification(userSender);
                    }
                }

                @Override
                public void changeNotis(Profile profile) {

                }
            });
        }
    }

    public void sendContactDetails() {
        ProfileTheme.getInstance().get(userSender, new ProfileResponse() {
            @Override
            public void success(Profile p) {
                String message = "";
                Profile.Address address = p.getAddress();
                String phonenumber = p.getPhoneNumber();
                if(address != null) {
                    message = "Address: " + address.toString();
                }
                if (phonenumber != null) {
                    message += "\n" + "Phone Number: " + phonenumber;
                }
                ChatTheme.getInstance(chat).sendChatMessage(message);


            }

            @Override
            public void failure(String s) {
                // TODO Control d'errors
            }
        });


    }


}
