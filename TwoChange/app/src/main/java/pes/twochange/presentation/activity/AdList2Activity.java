package pes.twochange.presentation.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.presentation.adapter.AdAdapter;

import static pes.twochange.presentation.activity.PostAdActivity.REQUEST_WRITE_EXTERNAL_STORAGE;

public class AdList2Activity extends AppCompatActivity {

    private static final String TAG = "AdList2Activity";
    private static final File TMP_IMAGE_LOCATION =
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/2change/tmp");

    private AdTheme adTheme;
    private static DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://change-64bd0.firebaseio.com/").child("ads");

    private RecyclerView recView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);

        recView = (RecyclerView) findViewById(R.id.recView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;

        final AdAdapter adapter = new AdAdapter(new ArrayList<Ad>(), deviceWidth, this);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recView.setItemAnimator(new DefaultItemAnimator());

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdAdapter.AdViewHolder avh = (AdAdapter.AdViewHolder) recView.getChildViewHolder(v);

                //Intent adIntent = new Intent(v.getContext(), AdActivity.class);
                Intent adIntent = new Intent(v.getContext(), PostAdActivity.class);
                adIntent.putExtra("adId", avh.getAd().getId());
                adIntent.putExtra("edition", true);
                startActivity(adIntent);
            }
        });

        if (!TMP_IMAGE_LOCATION.exists()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        db.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Ad ad = dataSnapshot.getValue(Ad.class);
                adapter.add(ad);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter.update(dataSnapshot.getValue(Ad.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Nothing
        } else {
            switch (requestCode) {
                case REQUEST_WRITE_EXTERNAL_STORAGE:
                    if (!TMP_IMAGE_LOCATION.mkdirs())
                        Log.e(TAG, "Unable to create directory or directory already exists: " + TMP_IMAGE_LOCATION.toString());
                    else
                        Log.i(TAG, "Created directory: " + TMP_IMAGE_LOCATION.toString());
                    break;
            }
        }
    }
}