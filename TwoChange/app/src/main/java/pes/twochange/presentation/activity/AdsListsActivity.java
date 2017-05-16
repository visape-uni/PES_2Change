package pes.twochange.presentation.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pes.twochange.R;
import pes.twochange.domain.model.Product;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.adapter.ProductFirebaseListAdapter;

public class AdsListsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private DatabaseReference mFirebaseWantedList;
    private DatabaseReference mFirebaseOfferedList;
    private static final String TAG = "AdsListsActivity";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_lists);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_ads_lists);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        //Firebase database
        final FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Lists reference
        mFirebaseWantedList = mFirebaseDatabase.getReference().child("lists").child(username).child("wanted");
        mFirebaseOfferedList = mFirebaseDatabase.getReference().child("lists").child(username).child("offered");

        //remove from wanted list
        ListView listView = (ListView) findViewById(R.id.wanted_list_ad);
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        showWanted();
        showOffered();
    }

    @Override
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        AlertDialog.Builder adb=new AlertDialog.Builder(AdsListsActivity.this);
        adb.setTitle("Delete?");

        final TextView keyProduct = (TextView) v.findViewById(R.id.product_key);
        TextView nameProduct = (TextView) v.findViewById(R.id.product_title);

        adb.setMessage("Are you sure you want to remove " + nameProduct.getText() + "?");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String key = keyProduct.getText().toString();
                AdTheme.Wanted.getInstance(username).remove(key);
            }});
        adb.show();

        return true;
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
            case R.id.action_delete:
                deleteItem();
                break;
            case R.id.action_edit:
                editItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addItemToWanted() {
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
                    DatabaseReference newProduct =  mFirebaseWantedList.push();
                    newProduct.setValue(new Product(productTitle, newProduct.getKey()));


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
        ListView wantedList = (ListView) findViewById(R.id.wanted_list_ad);
        wantedList.setAdapter(new ProductFirebaseListAdapter(
                this,
                Product.class,
                R.layout.product,
                AdTheme.Wanted.getInstance(username).getReference()
        ));
    }

    private void showOffered() {
        ListView wantedList = (ListView) findViewById(R.id.offered_list_ad);
        wantedList.setAdapter(new ProductFirebaseListAdapter(
                this,
                Product.class,
                R.layout.product,
                AdTheme.Offered.getInstance(username).getReference()
        ));
    }
}
