package pes.twochange.presentation.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;

public class AdActivity extends BaseActivity {
    private TextView titleTextView;
    private TextView usernameTextView;
    private TextView descriptionTextView;
    private RatingBar ratingBar;
    private TextView categoryTextView;
    //private ViewFlipper photosViewFlipper;

    private String adId;
    private Ad adSelected;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        toolbar.setTitle("Ads");

        titleTextView = (TextView) findViewById(R.id.ad_title);
        usernameTextView = (TextView) findViewById(R.id.ad_user);
        descriptionTextView = (TextView) findViewById(R.id.ad_description);
        ratingBar = (RatingBar) findViewById(R.id.ad_rating);
        categoryTextView = (TextView) findViewById(R.id.ad_category);
        //photosViewFlipper = (ViewFlipper) findViewById(R.id.ad_photos);

        adId = getIntent().getStringExtra("adId");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("ads").child(adId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adSelected = dataSnapshot.getValue(Ad.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        titleTextView.setText(adSelected.getTitle());
        usernameTextView.setText(adSelected.getUserName());
        descriptionTextView.setText(adSelected.getDescription());
        ratingBar.setRating((float)adSelected.getRating());
        categoryTextView.setText(adSelected.getCategory());


    }

    @Override
    protected int currentMenuItemIndex() {
        return 1;
    }
}
