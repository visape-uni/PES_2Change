package pes.twochange.presentation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import pes.twochange.R;
import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.controller.AuthActivity;
import pes.twochange.presentation.controller.ChatActivity;

public class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivitiy";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String currentUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_provisional);

        // region getting username from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        final String currentUsername = sharedPreferences.getString("username", null);
        // endregion

        TextView uidLbl = (TextView)findViewById(R.id.uidLbl);
        if (currentUsername != null) {
            uidLbl.setText(currentUsername);
        }

        // Esto no se pregunta antes?
        /*
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //No logeado
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    finish();
                }
            }
        };
        */


        //Show chats button + Pressed button listener
        Button showChatsBtn = (Button)findViewById(R.id.showChatsBtn);
        showChatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                ProfileTheme.getInstance().get(currentUsername, new ProfileResponse() {
                            @Override
                            public void success(Profile profile) {
                                Intent showChats = new Intent(getApplicationContext(), RecyclerChatActivity.class);
                                showChats.putExtra("currentUserName",profile.getUsername());
                                startActivity(showChats);
                            }

                            @Override
                            public void failure(String s) {
                                // TODO: cntrol de errores
                            }
                });
            }
        });

        //Open chat button + Pressed button listener
        Button openChatBtn = (Button)findViewById(R.id.openChatBtn);
        openChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                ProfileTheme.getInstance().get(currentUsername, new ProfileResponse() {
                            @Override
                            public void success(Profile profile) {
                                if (profile.getUsername() != null) {
                                    Intent chatIntent = new Intent (getApplicationContext(), ChatActivity.class);
                                    String userReciver = "adri1";
                                    if (profile.getUsername().equals("adri1")) userReciver = "visape";
                                    Chat chat = new Chat(profile.getUsername(),userReciver);
                                    chatIntent.putExtra("chat", chat);
                                    startActivity(chatIntent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "You must LogIn first!", Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    Intent i = new Intent(getApplicationContext(), AuthActivity.class);
                                    i.putExtra("startPoint", "LOGIN");
                                    startActivity(i);
                                    finish();
                                }
                            }

                            @Override
                            public void failure(String s) {
                                // TODO: cntrol de errores
                            }
                        }
                );
            }
        });

        //Add post button + Pressed button listener
        Button addPostBtn = (Button)findViewById(R.id.addPostBtn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, PostAdActivity.class);
                startActivity(intent);
            }
        });


        //View Profile button + Pressed button listener
        Button viewProfileBtn = (Button)findViewById(R.id.viewProfileBtn);
        viewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileNoActivity.class));
            }
        });

        //Show chats button + Pressed button listener
        Button searchUserBtn = (Button)findViewById(R.id.searchUserBtn);
        searchUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });


        Button logoutBtn = (Button) findViewById(R.id.logOutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Log.d(TAG, String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), AuthActivity.class);
                i.putExtra("startPoint", "LOGIN");
                startActivity(i);
                finish();
            }
        });

        Button adListBtn = (Button)findViewById(R.id.adListBtn);
        adListBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdList2Activity.class));
            }
        });
    }
}
