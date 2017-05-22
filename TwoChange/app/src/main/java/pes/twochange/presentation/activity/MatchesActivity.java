package pes.twochange.presentation.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import pes.twochange.R;
import pes.twochange.domain.model.Match;
import pes.twochange.presentation.Config;

public class MatchesActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseMatchesRef;
    private String currentUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);


        //Get reference to matches from DB
        mFirebaseMatchesRef = FirebaseDatabase.getInstance().getReference().child("matches").child(currentUsername);

        displayMatches();
    }

    private void displayMatches() {

        ListView matchesList = (ListView)findViewById(R.id.matches_list);
        FirebaseListAdapter<Match> adapter = new FirebaseListAdapter<Match>(this, Match.class, R.layout.message, mFirebaseMatchesRef) {
            @Override
            protected void populateView(View v, Match model, int position) {

                TextView myProduct, otherProduct, otherUsername, status;
                myProduct = (TextView) v.findViewById(R.id.myProductMatchName);
                otherProduct = (TextView) v.findViewById(R.id.otherProductMatchName);
                otherUsername = (TextView) v.findViewById(R.id.usernameMatch);
                status = (TextView) v.findViewById(R.id.statusMatch);

                myProduct.setText(model.getProductKeySender());
                otherProduct.setText(model.getProductKeyReciver());
                otherUsername.setText(model.getUsernameReciver());
                status.setText(model.getState().toString());
            }
        };


        matchesList.setAdapter(adapter);
    }
}
