package pes.twochange.presentation.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import pes.twochange.R;
import pes.twochange.domain.model.Chat;

public class MenuProvisionalActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivitiy";

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

                FirebaseUser userSender = FirebaseAuth.getInstance().getCurrentUser();
                if (userSender != null) {
                    Intent chatIntent = new Intent (getApplicationContext(), ChatActivity.class);
                    String userReciverUid = "DEEgGOdjjmVkR29uEtvNi0W2zrv1";
                    if (userSender.getUid().equals("DEEgGOdjjmVkR29uEtvNi0W2zrv1")) userReciverUid = "PtkvVdIGqdVzx5KJ35t2OJ1wXKm2";
                    Chat chat = new Chat(userSender.getUid(),userReciverUid);
                    //Chat chat = new Chat(userReciverUid, userSender.getUid());
                    chatIntent.putExtra("chat", chat);
                    startActivity(chatIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "You must LogIn first!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MenuProvisionalActivity.this, LoginActivtiy.class));
                    finish();
                }
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

        Button logOutBtn = (Button) findViewById(R.id.logOutBtn);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));
                FirebaseAuth.getInstance().signOut();
                Log.d(TAG, String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));
                startActivity(new Intent(MenuProvisionalActivity.this, LoginActivtiy.class));
                finish();
            }
        });
    }
}
