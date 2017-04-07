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
import com.google.firebase.database.FirebaseDatabase;

import pes.twochange.R;
import pes.twochange.domain.model.Message;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivtiy";
    private FirebaseListAdapter<Message> adapter;
    private String userSenderUid;
    private String userReciverUid;
    FloatingActionButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userSenderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //FALTA OBTENER EL UID DEL USUARIO QUE RECIVE EL MENSAJE
        //userReciverUid = ;

        sendBtn = (FloatingActionButton)findViewById(R.id.sender_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                EditText messageInput = (EditText) findViewById(R.id.message_input);
                FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(new Message(messageInput.getText().toString(),
                        userSenderUid, userReciverUid));
                messageInput.setText("");
            }
        });


        displayChatMessage();
    }

    private void displayChatMessage() {

        ListView messagesList = (ListView)findViewById(R.id.messages_list);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message, FirebaseDatabase.getInstance().getReference().child("messages")) {
            @Override
            protected void populateView(View v, Message model, int position) {
                Log.d(TAG,"display messages");
                TextView messageContent, messageSender, messageTime;
                messageContent = (TextView) v.findViewById(R.id.message_content);
                messageSender = (TextView) v.findViewById(R.id.message_sender);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageContent.setText(model.getMessageContent());
                messageSender.setText(model.getMessageSender());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

            }
        };
        messagesList.setAdapter(adapter);
    }
}
