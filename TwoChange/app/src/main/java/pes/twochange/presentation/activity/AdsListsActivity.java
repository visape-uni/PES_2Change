package pes.twochange.presentation.activity;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Match;
import pes.twochange.domain.model.Product;
import pes.twochange.presentation.Config;

public class AdsListsActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseWantedList;
    private DatabaseReference mFirebaseOfferedList;
    private DatabaseReference mFirebaseCategories;
    String currentUsername = "";
    private static final String TAG = "AdsListsActivitiy";

    private FirebaseListAdapter<Product> offeredAdapter;
    private FirebaseListAdapter<Product> wantedAdapter;

    private ListView wantedList;
    private ListView offeredList;

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
        mFirebaseCategories = mFirebaseDatabase.getReference().child("categories");

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
                String categoryTitle = setCategory.getSelectedItem().toString();
                categoryTitle = categoryTitle.trim();
                if (!categoryTitle.isEmpty()) {
                    if (!isCategoryWanted(categoryTitle)) {
                        DatabaseReference newProduct = mFirebaseWantedList.push();
                        newProduct.setValue(new Product(categoryTitle, newProduct.getKey()));
                    }
                }
                setCategory.setVisibility(View.GONE);
                btnSetProductTitle.setVisibility(View.GONE);
            }
        });
    }

    private void editItem() {

    }

    private boolean isCategoryWanted (String categoryTitle) {
        boolean finded = false;
        int i = 0;
        while ((!finded) && (i < wantedAdapter.getCount())) {
            if (wantedAdapter.getItem(i).getName().equals(categoryTitle)) finded = true;
            ++i;
        }
        return finded;
    }

    private void makeMatch() {

        //Count matches
        int numMatches = 0;

        //Per cada ad a la offered list
        for (int i = 0; i < offeredList.getCount(); ++i) {
            //Per cada categoria de la wanted list
            for (int j = 0; j < wantedList.getCount(); ++j) {

                //Nom + key del producte
                Product auxProduct = offeredAdapter.getItem(i);

                //CONTINUAR AQUI!!!!!!!!!
                //Buscar producte a ads
                //Agafar la seva valoracio

                String categoria = wantedAdapter.getItem(j).getName();

                //Buscar 1 match del producte amb la categoria
                Ad matchedProduct = searchMatch(/*ValoracioProducte, */ categoria);

                //Guardar el match en la base de datos (KEY = key del producto que tiene la categoria que busca)
                String productKeySender = offeredList.getItemAtPosition(i).toString();
                Match match = new Match(currentUsername, matchedProduct.getUserName(), productKeySender, matchedProduct.getId());
                match.save();
            }
        }

        if (numMatches > 0) Toast.makeText(AdsListsActivity.this, "Congratulations you have " + numMatches + "matches.", Toast.LENGTH_LONG);
        else  Toast.makeText(AdsListsActivity.this, "Sorry there are no matches.", Toast.LENGTH_LONG);

    }

    private Ad searchMatch(/*int valoracio, */ String categoria) {

        DatabaseReference mRefMyCat = mFirebaseCategories.child(categoria);
        mRefMyCat.push().setValue("PROVA");

        //CONTINUAR AQUIIIIIIIII!!!!!!!!!!!!!!

        FirebaseListAdapter<Product> catAdapter = new FirebaseListAdapter<Product>(this, Product.class, R.layout.product, mRefMyCat) {
            @Override
            protected void populateView(View v, Product model, int position) {
                //AQUI NO ENTRA!
                Log.d(TAG, "HOLA");
            }
        };

        //GET COUNT DONA 0 ESTA MALAMENT
        Log.d(TAG, String.valueOf(catAdapter.getCount()));

        return null;
    }


    private void showWanted() {

        wantedList = (ListView)findViewById(R.id.wanted_list_ad);
        wantedAdapter = new FirebaseListAdapter<Product>(this, Product.class, R.layout.product, mFirebaseWantedList) {
            @Override
            protected void populateView(View v, Product model, int position) {

                TextView productTitle, productKey;
                productTitle = (TextView) v.findViewById(R.id.product_title);
                productKey = (TextView) v.findViewById(R.id.product_key);

                productTitle.setText(model.getName());
                productKey.setText(model.getKey());
            }
        };

        wantedList.setAdapter(wantedAdapter);
    }

    private void showOffered() {

        offeredList = (ListView)findViewById(R.id.offered_list_ad);
        offeredAdapter = new FirebaseListAdapter<Product>(this, Product.class, R.layout.product, mFirebaseOfferedList) {
            @Override
            protected void populateView(View v, Product model, int position) {

                TextView productTitle, productKey;
                productTitle = (TextView) v.findViewById(R.id.product_title);
                productKey = (TextView) v.findViewById(R.id.product_key);

                productTitle.setText(model.getName());
                productKey.setText(model.getKey());
            }
        };

        offeredList.setAdapter(offeredAdapter);
    }
}
