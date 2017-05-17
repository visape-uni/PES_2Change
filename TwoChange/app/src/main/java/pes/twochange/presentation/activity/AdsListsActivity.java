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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Match;
import pes.twochange.domain.model.Product;
import pes.twochange.presentation.Config;
import pes.twochange.services.DatabaseResponse;

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
                        Log.d(TAG, "ADD TO WANTED");
                        DatabaseReference newProduct = mFirebaseWantedList.push();
                        newProduct.setValue(new Product(categoryTitle, newProduct.getKey()));
                    } else {
                        Toast.makeText(AdsListsActivity.this, categoryTitle + " was already in the list.", Toast.LENGTH_SHORT);
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
        Map<String, String> candidateMatches = new HashMap<>();

        for (int i = 0; i < wantedList.getCount(); ++i) {
            String categoria = wantedAdapter.getItem(i).getName();
            //Buscar matches candidatos para esta categoria
            candidateMatches = getCandidateMatches(categoria);
        }


        //RETORNA 0, ha de retornar els candiates!!!!!;
        Log.d(TAG, "CANDIDATE MATCHES: " + candidateMatches.size());

        for (int i = 0; i < offeredList.getCount(); ++i) {

            Iterator it = candidateMatches.entrySet().iterator();
            //Offered product to compare match
            Product auxProduct = offeredAdapter.getItem(i);
            //RECORRER TODOS LOS MATCHES POSIBLES
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Log.d(TAG, pair.getKey() + " = " + pair.getValue());

                Product posMatch = new Product(pair.getValue().toString(), pair.getKey().toString());
                if (isMatch(auxProduct, posMatch)) {
                    //AÑADIR posMatch A LOS MATCHES DE LA BD

                    //Key del producto del usuario sender con el que se quiere hacer el match
                    String productKeySender = offeredList.getItemAtPosition(i).toString();

                    //crear match y guardarlo en la BD
                    Match match = new Match(currentUsername, posMatch.getName(), productKeySender, posMatch.getKey());
                    match.save();

                    //inc contador de matches
                    ++numMatches;
                }
                //it.remove();
            }
        }

        if (numMatches > 0) Toast.makeText(AdsListsActivity.this, "Congratulations you have " + numMatches + "matches.", Toast.LENGTH_LONG);
        else  Toast.makeText(AdsListsActivity.this, "Sorry there are no matches.", Toast.LENGTH_LONG);
    }

    private boolean isMatch (Product offeredProd, Product posMatchProd) {
        //FALTA IMPLEMENTAR

        Ad offeredAd = getAdOfProduct(offeredProd);
        Ad posMatchAd = getAdOfProduct(posMatchProd);

        int rateVariable = 10;

        //Si tienen rating similares (+10 o -10) es MATCH
        if ((offeredAd.getRating() >= (posMatchAd.getRating() - rateVariable)) && (offeredAd.getRating() <= (posMatchAd.getRating() + rateVariable))) return true;
        else return false;
    }

    private Map<String, String> getCandidateMatches (String categoria) {
        final Map<String,String> aux = new HashMap<String, String>();

        final DatabaseResponse callback = new DatabaseResponse() {
            @Override
            public void success(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    aux.put(d.getKey().toString(), d.getValue().toString());
                    Log.d(TAG, d.getValue().toString());
                }
            }
            @Override
            public void empty() {
                Log.d(TAG, "EMPTY");
            }
            @Override
            public void failure(String message) {
                Log.d(TAG, "Something went wrong: " + message);
            }
        };

        DatabaseReference mRefMyCat = mFirebaseCategories.child(categoria);

        mRefMyCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "ENTRAS?");
                if (dataSnapshot == null) {
                    callback.empty();
                } else {
                    callback.success(dataSnapshot);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TRACTAR ERROR
                callback.failure(databaseError.toString());
            }
        });

        return aux;
    }


    /*private void getPosibleMatches (Map<String,String> posibleMatches) {
        boolean finded = false;
        Iterator it = posibleMatches.entrySet().iterator();

        while (!finded && it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Log.d(TAG, pair.getKey() + " = " + pair.getValue());
            it.remove();
        }

        //for (Map.Entry<String,String> entry : posibleMatches.entrySet()) {

    }*/


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
