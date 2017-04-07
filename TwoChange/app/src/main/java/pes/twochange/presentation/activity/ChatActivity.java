package pes.twochange.presentation.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pes.twochange.R;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Message;
import pes.twochange.services.Firebase;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivitiy";
    private String userSenderUid;
    private String userReciverUid;
    private DatabaseReference mFirebaseChatRefSender;
    private DatabaseReference mFirebaseChatRefReciver;
    FloatingActionButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Coger chat pasado como extra en el intent
        Chat chat = (Chat) getIntent().getExtras().getSerializable("chat");
        /*
        //crear chat
        chat = new Chat(userSenderUid, userReciverUid);*/

        userSenderUid = chat.getMessageSender();
        userReciverUid = chat.getMessageReciver();


        //Firebase database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Referencia al chat
        mFirebaseChatRefSender = mFirebaseDatabase.getReference().child("chats").child(userSenderUid).child(userReciverUid);
        mFirebaseChatRefReciver = mFirebaseDatabase.getReference().child("chats").child(userReciverUid).child(userSenderUid);


        sendBtn = (FloatingActionButton)findViewById(R.id.sender_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                EditText messageInput = (EditText) findViewById(R.id.message_input);
                String content = messageInput.getText().toString();
                if (!content.isEmpty()) {
                    mFirebaseChatRefSender.push().setValue(new Message(content, userSenderUid, userReciverUid));
                    mFirebaseChatRefReciver.push().setValue(new Message(content, userSenderUid, userReciverUid));
                }
                messageInput.setText("");
            }
        });


        displayChatMessage();
    }

    private void displayChatMessage() {

        ListView messagesList = (ListView)findViewById(R.id.messages_list);
        FirebaseListAdapter<Message> adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message, mFirebaseChatRefSender) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView messageContent, messageSender, messageTime;
                messageContent = (TextView) v.findViewById(R.id.message_content);
                messageSender = (TextView) v.findViewById(R.id.message_sender);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageContent.setText(model.getMessageContent());
                messageSender.setText(model.getMessageSender());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
            }
        };

        messagesList.setAdapter(adapter);
    }
}
