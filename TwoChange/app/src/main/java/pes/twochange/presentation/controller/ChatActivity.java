package pes.twochange.presentation.controller;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

import pes.twochange.R;
import pes.twochange.domain.callback.BlockedResponse;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Message;
import pes.twochange.domain.themes.ChatTheme;
import pes.twochange.domain.themes.SettingsTheme;
import pes.twochange.presentation.controller.BaseActivity;
import pes.twochange.services.NotificationSender;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends BaseActivity {

    private static final String TAG = "ChatActivitiy";
    private String userSender;
    private String userReciver;
    private DatabaseReference mFirebaseChatRefSender;
    private DatabaseReference mFirebaseChatRefReciver;
    FloatingActionButton sendBtn;



    private RelativeLayout mRlView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Relative view
        mRlView = (RelativeLayout) findViewById(R.id.rl_view);


        //Coger chat pasado como extra en el intent
        final Chat chat = (Chat) getIntent().getExtras().getSerializable("chat");

        //User sender
        userSender = chat.getMessageSender();
        //User reciver
        userReciver = chat.getMessageReciver();

        //Suscribirse al topic para recibir notificaciones de chat
        FirebaseMessaging.getInstance()
                .subscribeToTopic(userReciver);

        //Instance to Firebase database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        //Firebase ref to sender's chat
        mFirebaseChatRefSender = mFirebaseDatabase.getReference().child("chats").child(userSender).child(userReciver);

        //Firebase ref to reciver's chat
        mFirebaseChatRefReciver = mFirebaseDatabase.getReference().child("chats").child(userReciver).child(userSender);

        //Display the messages of the DB into the list view
        displayChatMessage();

        //OnClickListener to send messages
        sendBtn = (FloatingActionButton)findViewById(R.id.sender_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                //Get the message to string
                EditText messageInput = (EditText) findViewById(R.id.message_input);
                String content = messageInput.getText().toString();

                //Delete blank spaces of the messages
                content = content.trim();

                if (!content.isEmpty()) {

                    ChatTheme.getInstance(chat).sendChatMessage(content);

                    //Put the text field empty again
                    messageInput.setText("");
                }
            }
        });
    }

    @Override
    protected int currentMenuItemIndex() {
        return CHAT_ACTIVITY;
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_details:
                Chat chat = new Chat(userSender,userReciver);
                ChatTheme.getInstance(chat).sendContactDetails();
                break;
            case R.id.block_user:
                SettingsTheme.getInstance(userSender).userIsBlocked(userReciver, new BlockedResponse() {
                    @Override
                    public void isBlocked(boolean blocked, String userblocked) {
                        if (blocked) Toast.makeText(ChatActivity.this, "User unblocked successfully", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(ChatActivity.this, "User blocked successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                SettingsTheme.getInstance(userSender).blockUser(userReciver);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void displayChatMessage() {

        ListView messagesList = (ListView)findViewById(R.id.messages_list);

        //Firebase adapter for getting the messages
        FirebaseListAdapter<Message> adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message, mFirebaseChatRefSender) {
            @Override
            protected void populateView(View v, Message model, int position) {

                //Getting the textviews of the Message's layout
                TextView messageContent, messageSender, messageTime;
                messageContent = (TextView) v.findViewById(R.id.message_content);
                messageSender = (TextView) v.findViewById(R.id.message_sender);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                LinearLayout layoutMessageContent = (LinearLayout) v.findViewById(R.id.layout_message_content);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) layoutMessageContent.getLayoutParams();
                if (model.getMessageSender().equals(userSender)) {
                    //If it's a message from the sender use the green (message) and align right
                    layoutMessageContent.setBackgroundResource(R.drawable.ic_send_message);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                } else {
                    //If it's a message from the reciver use the orange (message) and align left
                    layoutMessageContent.setBackgroundResource(R.drawable.ic_recive_message);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }

                //Set with the content of the message, the message sender, and the time when the message was sent
                messageContent.setText(model.getMessageContent());
                messageSender.setText(model.getMessageSender());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
            }
        };

        //Use the firebase adapter on the listview
        messagesList.setAdapter(adapter);
    }
}
