package pes.twochange.presentation.activity;


import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import pes.twochange.R;


public class RecyclerChatActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> users;
    private DatabaseReference mFirebaseChats;


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

        String user = getIntent().getStringExtra("currentUserUID");

        //nombres de prueba
        /*users.add("Félix");
        users.add("Princesa Chicle");
        users.add("Guille");
        users.add("Adri");
        users.add("Lars Ulrich (cuando tenía pelo)");
        users.add("Víctor");
        users.add("Donald Trump");
        users.add("Andrés");
        users.add("Lisa Simpson");
        users.add("Aa Mama");
        users.add("Adri, otra vez");
        users.add("Sujeto de prueba Huevo Huevo 7");*/


        mAdapter = new RecyclerChatAdapter(this, users, user);
        mRecyclerView.setAdapter(mAdapter);

    }


}
