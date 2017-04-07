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
    private FirebaseListAdapter<Message> adapter;
    private Chat chat;
    private String userSenderUid;
    private String userReciverUid;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseChatRef;
    FloatingActionButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Coger chat pasado como extra en el intent
        this.chat = (Chat)getIntent().getExtras().getSerializable("chat");
        /*
        //crear chat
        chat = new Chat(userSenderUid, userReciverUid);*/

        userSenderUid = chat.getMessageSender();
        userReciverUid = chat.getMessageSender();


        //Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Referencia al chat
        mFirebaseChatRef = mFirebaseDatabase.getReference().child("chats").child(userSenderUid).child(userReciverUid);


        sendBtn = (FloatingActionButton)findViewById(R.id.sender_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                EditText messageInput = (EditText) findViewById(R.id.message_input);
                mFirebaseChatRef.push().setValue(new Message(messageInput.getText().toString()));
                messageInput.setText("");
            }
        });


        displayChatMessage();
    }

    private void displayChatMessage() {

        ListView messagesList = (ListView)findViewById(R.id.messages_list);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message, mFirebaseChatRef) {
            @Override
            protected void populateView(View v, Message model, int position) {
                Log.d(TAG,"display messages");
                TextView messageContent, messageSender, messageTime;
                messageContent = (TextView) v.findViewById(R.id.message_content);
                messageSender = (TextView) v.findViewById(R.id.message_sender);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageContent.setText(model.getMessageContent());
                messageSender.setText("SENDER");
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

            }
        };
        messagesList.setAdapter(adapter);
    }
}
