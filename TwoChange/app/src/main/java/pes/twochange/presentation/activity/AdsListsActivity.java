package pes.twochange.presentation.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
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
    private static final String TAG = "AdsListsActivitiy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_lists);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_ads_lists);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        //Firebase database
        final FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Referencia al chat
        mFirebaseWantedList = mFirebaseDatabase.getReference().child("lists").child(currentUsername).child("wanted");
        mFirebaseOfferedList = mFirebaseDatabase.getReference().child("lists").child(currentUsername).child("offered");

        //delete from wanted list
        final ListView lv = (ListView) findViewById(R.id.wanted_list_ad);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(AdsListsActivity.this);
                adb.setTitle("Delete?");

                final TextView keyProduct = (TextView) v.findViewById(R.id.product_key);
                TextView nameProduct = (TextView) v.findViewById(R.id.product_title);

                adb.setMessage("Are you sure you want to delete " + nameProduct.getText() + "?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseWantedList.child(keyProduct.getText().toString()).removeValue();
                    }});
                adb.show();

                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, final int position, long id) {

                final TextView setProductTitle = (TextView) findViewById(R.id.setTitle_txt);
                final FloatingActionButton btnSetProductTitle = (FloatingActionButton) findViewById(R.id.setTitle_btn);

                final TextView keyProduct = (TextView) v.findViewById(R.id.product_key);
                TextView nameProduct = (TextView) v.findViewById(R.id.product_title);

                setProductTitle.setVisibility(View.VISIBLE);
                btnSetProductTitle.setVisibility(View.VISIBLE);

                setProductTitle.setText(nameProduct.getText());

                btnSetProductTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        String productTitle = setProductTitle.getText().toString();
                        productTitle = productTitle.trim();
                        if (!productTitle.isEmpty()) {
                            DatabaseReference editProduct = mFirebaseWantedList.child(keyProduct.getText().toString());
                            editProduct.child("title").setValue(productTitle);


                            setProductTitle.setText("");
                        }
                        setProductTitle.setVisibility(View.GONE);
                        btnSetProductTitle.setVisibility(View.GONE);
                    }
                });

            }
        });


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
                addItemToWanted();
                break;
            case R.id.action_match:
                makeMatch();
                break;
            case R.id.action_edit:
                editItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addItemToWanted() {
        final Spinner setCategory = (Spinner) findViewById(R.id.Category_Spn);
        final FloatingActionButton btnSetProductTitle = (FloatingActionButton) findViewById(R.id.setTitle_btn);

        setCategory.setVisibility(View.VISIBLE);
        btnSetProductTitle.setVisibility(View.VISIBLE);

        btnSetProductTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                String productTitle = setCategory.getSelectedItem().toString();
                productTitle = productTitle.trim();
                if (!productTitle.isEmpty()) {
                    DatabaseReference newProduct =  mFirebaseWantedList.push();
                    newProduct.setValue(new Product(productTitle, newProduct.getKey()));


                    //setProductTitle.setText("");
                }
                setCategory.setVisibility(View.GONE);
                btnSetProductTitle.setVisibility(View.GONE);
            }
        });
    }

    private void editItem() {

    }
    private void makeMatch() {

        //Make match

    }


    private void showWanted() {

        ListView wantedList = (ListView)findViewById(R.id.wanted_list_ad);
        FirebaseListAdapter<Product> adapter = new FirebaseListAdapter<Product>(this, Product.class, R.layout.product, mFirebaseWantedList) {
            @Override
            protected void populateView(View v, Product model, int position) {

                TextView productTitle, productKey;
                productTitle = (TextView) v.findViewById(R.id.product_title);
                productKey = (TextView) v.findViewById(R.id.product_key);

                productTitle.setText(model.getTitle());
                productKey.setText(model.getKey());
            }
        };

        wantedList.setAdapter(adapter);
    }

    private void showOffered() {

        final ListView offeredList = (ListView)findViewById(R.id.offered_list_ad);
        FirebaseListAdapter<Product> adapter = new FirebaseListAdapter<Product>(this, Product.class, R.layout.product, mFirebaseOfferedList) {
            @Override
            protected void populateView(View v, final Product model, int position) {

                final TextView productTitle, productKey;
                productTitle = (TextView) v.findViewById(R.id.product_title);
                productKey = (TextView) v.findViewById(R.id.product_key);


                productTitle.setText(model.getTitle());
                productKey.setText(model.getKey());

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.d("AdClicked","AdClicked"+model.getKey());
                        Intent adIntent = new Intent(v.getContext(),AdActivity.class);
                        adIntent.putExtra("adId", model.getKey());
                        v.getContext().startActivity(adIntent);
                    }
                });
            }
        };
        /*offeredList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Log.d("AdClicked","AdClicked");
            }
        });*/

        offeredList.setAdapter(adapter);
    }
}
