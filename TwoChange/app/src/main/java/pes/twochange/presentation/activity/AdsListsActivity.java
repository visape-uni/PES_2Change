package pes.twochange.presentation.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Match;
import pes.twochange.domain.model.Product;
import pes.twochange.domain.themes.MatchTheme;
import pes.twochange.presentation.Config;
import pes.twochange.services.DatabaseResponse;

public class AdsListsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private DatabaseReference mFirebaseWantedList;
    private DatabaseReference mFirebaseOfferedList;
    private DatabaseReference mFirebaseAds;
    private DatabaseReference mFirebaseMatches;

    private String currentUsername = "";
    private static final String TAG = "AdsListsActivitiy";

    private FirebaseListAdapter<Product> offeredAdapter;
    private FirebaseListAdapter<Product> wantedAdapter;

    private ListView wantedList;
    private ListView offeredList;

    private Map<String,Ad> auxCandidateMatches = new HashMap<String, Ad>();
    private Map<String,Match> myMatches = new HashMap<String, Match>();

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
        mFirebaseAds = mFirebaseDatabase.getReference().child("ads");
        mFirebaseMatches = mFirebaseDatabase.getReference().child("matches").child(currentUsername);

        getMyMatches();


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
//                AdTheme.Wanted.getInstance(username).remove(key);
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
            case R.id.action_match:
                MatchTheme.getInstance().makeMatches(currentUsername, offeredAdapter, wantedAdapter, myMatches);
                break;
            case R.id.action_edit:

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

                        //no username y rating = -1 (no rate)
                        newProduct.setValue(new Product(categoryTitle, newProduct.getKey(), null, null, -1));
                    } else {
                        Toast.makeText(AdsListsActivity.this, categoryTitle + " was already in the list.", Toast.LENGTH_SHORT);
                    }
                }
                setCategory.setVisibility(View.GONE);
                btnSetProductTitle.setVisibility(View.GONE);
            }
        });
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

    private boolean isMatch (Product offeredProd, Product posMatchProd) {
        //rate permision
        int rateVariable = 10;

        //Si tienen rating similares (+10 o -10) es MATCH
        if ((offeredProd.getRating() >= (posMatchProd.getRating() - rateVariable)) && (offeredProd.getRating() <= (posMatchProd.getRating() + rateVariable))) return true;
        else return false;
    }

    //Fa el match amb els anuncis de la BD i els guarda a la BD com a matches
    private void makeMatches () {

        Toast.makeText(AdsListsActivity.this, "Matchig has started, we will notificate you when it finish.", Toast.LENGTH_LONG).show();

        final DatabaseResponse callback = new DatabaseResponse() {
            @Override
            public void success(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    if (isCategoryWanted(d.getValue(Ad.class).getCategory())) {
                        auxCandidateMatches.put(d.getKey().toString(), d.getValue(Ad.class));
                    }
                }

                for (int i = 0; i < offeredAdapter.getCount(); ++i) {
                    Iterator it = auxCandidateMatches.entrySet().iterator();

                    //RECORRER TODOS LOS MATCHES POSIBLES
                    while (it.hasNext()) {
                        Map.Entry<String, Ad> pair = (Map.Entry)it.next();

                        //Posible match product
                        Product posMatch = new Product(pair.getValue().getTitle(), pair.getKey().toString(), pair.getValue().getUserName(), pair.getValue().getCategory(), pair.getValue().getRating());

                        //Offered product to compare match
                        Product auxProduct = offeredAdapter.getItem(i);

                        //Key del producto del usuario sender con el que se quiere hacer el match
                        String productKeySender = auxProduct.getKey();

                        //SI es match, AÑADIR posMatch A LOS MATCHES DE LA BD
                        if (!(posMatch.getUsername().equals(currentUsername))&&!(isMatched(productKeySender.concat(posMatch.getKey())))&&(isMatch(auxProduct, posMatch))) {

                            //crear match y guardarlo en la BD
                            Match match = new Match(currentUsername, posMatch.getUsername(), productKeySender, posMatch.getKey(), posMatch.getCategory());
                            mFirebaseMatches.child(match.getProductKeySender().concat(match.getProductKeyReciver())).setValue(match);
                            myMatches.put(match.getProductKeySender().concat(match.getProductKeyReciver()),match);
                        }
                    }
                }

                //NOTIFICAR QUE MATCH ACABADO

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

        mFirebaseAds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
    }

    //Agafa de la BD els matches de l'usuari i els guarda al map "myMatches"
    private void getMyMatches () {

        final DatabaseResponse callback = new DatabaseResponse() {
            @Override
            public void success(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    if (isCategoryWanted(d.getValue(Match.class).getCategoryProductReciver())) {
                        myMatches.put(d.getKey().toString(), d.getValue(Match.class));
                    }
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

        mFirebaseMatches.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
    }

    private boolean isMatched(String key) {
        return (myMatches.containsKey(key));
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
            protected void populateView(View v, final Product model, int position) {

                TextView productTitle, productKey;
                productTitle = (TextView) v.findViewById(R.id.product_title);
                productKey = (TextView) v.findViewById(R.id.product_key);

                productTitle.setText(model.getName());
                productKey.setText(model.getKey());

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent adIntent = new Intent(v.getContext(),AdActivity.class);
                        adIntent.putExtra("adId", model.getKey());
                        v.getContext().startActivity(adIntent);
                    }
                });
            }
        };
        offeredList.setAdapter(offeredAdapter);

    }
}
