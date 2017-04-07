package pes.twochange.presentation.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pes.twochange.R;
import pes.twochange.domain.model.Chat;

public class MenuProvisionalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_provisional);

        //Show chats button + Pressed button listener
        Button showChatsBtn = (Button)findViewById(R.id.showChatsBtn);
        showChatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ADRI CONECTATE AQUI", Toast.LENGTH_LONG).show();
            }
        });

        //Open chat button + Pressed button listener
        Button openChatBtn = (Button)findViewById(R.id.openChatBtn);
        openChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                Intent chatIntent = new Intent (getApplicationContext(), ChatActivity.class);
                FirebaseUser userSender = FirebaseAuth.getInstance().getCurrentUser();
                String userReciverUid = "23fdgdfF";
                Chat chat = new Chat(userSender.getUid(),userReciverUid);
                chatIntent.putExtra("chat", chat);
                startActivity(chatIntent);
                Toast.makeText(getApplicationContext(), "VICTOR CONECTATE AQUI", Toast.LENGTH_LONG).show();
            }
        });

        //Add post button + Pressed button listener
        Button addPostBtn = (Button)findViewById(R.id.addPostBtn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ANDRES CONECTATE AQUI", Toast.LENGTH_LONG).show();
            }
        });

        //View Profile button + Pressed button listener
        Button viewProfileBtn = (Button)findViewById(R.id.viewProfileBtn);
        viewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "FELIX CONECTATE AQUI", Toast.LENGTH_LONG).show();
            }
        });

        //Show chats button + Pressed button listener
        Button searchUserBtn = (Button)findViewById(R.id.searchUserBtn);
        searchUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "GUILLE CONECTATE AQUI", Toast.LENGTH_LONG).show();
            }
        });
    }
}
