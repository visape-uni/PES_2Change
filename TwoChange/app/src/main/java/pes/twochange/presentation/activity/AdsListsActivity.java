package pes.twochange.presentation.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pes.twochange.R;
import pes.twochange.domain.model.Product;
import pes.twochange.presentation.Config;

public class AdsListsActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseWantedList;
    private DatabaseReference mFirebaseOfferedList;
    String currentUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_lists);


        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        //Firebase database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Referencia al chat
        mFirebaseWantedList = mFirebaseDatabase.getReference().child("lists").child(currentUsername).child("wanted");
        mFirebaseOfferedList = mFirebaseDatabase.getReference().child("lists").child(currentUsername).child("offered");

        showWanted();
        showOffered();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addItem();
                break;
            case R.id.action_delete:
                deleteItem();
                break;
            case R.id.action_edit:
                editItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addItem() {
        final TextView setProductTitle = (TextView) findViewById(R.id.setTitle_txt);
        final FloatingActionButton btnSetProductTitle = (FloatingActionButton) findViewById(R.id.setTitle_btn);

        setProductTitle.setVisibility(View.VISIBLE);
        btnSetProductTitle.setVisibility(View.VISIBLE);

        btnSetProductTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                String productTitle = setProductTitle.getText().toString();
                productTitle = productTitle.trim();
                if (!productTitle.isEmpty()) {
                    mFirebaseWantedList.push().setValue(new Product(productTitle));

                    setProductTitle.setText("");
                }
                setProductTitle.setVisibility(View.GONE);
                btnSetProductTitle.setVisibility(View.GONE);
            }
        });



    }
    private void editItem() {

    }
    private void deleteItem() {

    }


    private void showWanted() {

        ListView wantedList = (ListView)findViewById(R.id.wanted_list_ad);
        FirebaseListAdapter<Product> adapter = new FirebaseListAdapter<Product>(this, Product.class, R.layout.message, mFirebaseWantedList) {
            @Override
            protected void populateView(View v, Product model, int position) {

                TextView productTitle;
                productTitle = (TextView) v.findViewById(R.id.product_title);

                productTitle.setText(model.getTitle());
            }
        };

        wantedList.setAdapter(adapter);
    }

    private void showOffered() {

        ListView offeredList = (ListView)findViewById(R.id.offered_list_ad);
        FirebaseListAdapter<Product> adapter = new FirebaseListAdapter<Product>(this, Product.class, R.layout.message, mFirebaseOfferedList) {
            @Override
            protected void populateView(View v, Product model, int position) {

                TextView productTitle;
                productTitle = (TextView) v.findViewById(R.id.product_title);

                productTitle.setText(model.getTitle());
            }
        };

        offeredList.setAdapter(adapter);
    }
}
