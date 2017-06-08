package pes.twochange.presentation.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.adapter.RecyclerChatAdapter;
import pes.twochange.presentation.controller.BaseActivity;


public class RecyclerChatActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> users;
    private DatabaseReference mFirebaseChats;
    private FirebaseAuth mAuth;


    public RecyclerChatActivity() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.users = new ArrayList<>();
        setContentView(R.layout.fragment_recycler_chat);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_chat);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        String user = sp.getString("username", null);
        
        mAdapter = new RecyclerChatAdapter(this, users, user);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected int currentMenuItemIndex() {
        return CHAT_ACTIVITY;
    }


}
