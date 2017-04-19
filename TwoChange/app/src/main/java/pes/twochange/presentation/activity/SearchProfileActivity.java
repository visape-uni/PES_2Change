package pes.twochange.presentation.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Profile;

public class SearchProfileActivity extends AppCompatActivity {
    //Attributes
    EditText searchField;
    ListView profilesView;
    ArrayList<String> profilesArray;
    ArrayAdapter<String> profilesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile);
        searchField = (EditText)findViewById(R.id.searchField);
        profilesView = (ListView)findViewById(R.id.profilesList);
        profilesArray = new ArrayList<String>();
        profilesArray.add("Me cago en su puta madre");
        profilesArray.add("No va");
        profilesArray.add("Firebase Sucks");
        profilesArray.add("Hasta los cojones");
        profilesAdapter = new ArrayAdapter<String>(SearchProfileActivity.this,
                android.R.layout.simple_list_item_1, profilesArray);
        profilesView.setAdapter(profilesAdapter);
        //
        /*DatabaseReference myDatabase;
        myDatabase = FirebaseDatabase.getInstance().getReference();
        myDatabase.child("change-64bd0/profile/XTaOaXFHRCgqWTsoTWgJ0BGsGj32").child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String prof = dataSnapshot.getValue(String.class);
                profilesArray.add(prof);
                profilesAdapter.notifyDataSetChanged();
                Log.d("HOLAAAAAA", prof);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });*/
    }
}
