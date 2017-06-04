package pes.twochange.domain.themes;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Match;
import pes.twochange.domain.model.Product;
import pes.twochange.services.DatabaseResponse;

/**
 * Created by Visape on 24/05/2017.
 */

public class MatchTheme {

    private static final MatchTheme ourInstance = new MatchTheme();
    private static final String TAG = "MatchTheme";
    private Map<String,Ad> auxCandidateMatches = new HashMap<String, Ad>();

    public static MatchTheme getInstance() {
        return ourInstance;
    }

    public void makeMatches (final String currentUsername, final ArrayList<Product> wantedProd, final Map<String, Match> myMatches) {

        //Referencia als matches del user
        final DatabaseReference mFirebaseMatches = FirebaseDatabase.getInstance().getReference().child("matches").child(currentUsername);
        DatabaseReference mFirebaseAds = FirebaseDatabase.getInstance().getReference().child("ads");
        //final Map<String,Match> myMatches = new HashMap<>();
        final ArrayList<Ad> offeredProd = new ArrayList<>();

        final DatabaseResponse callback = new DatabaseResponse() {
            @Override
            public void success(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    if (d.getValue(Ad.class).getUserName().equals(currentUsername)) offeredProd.add(d.getValue(Ad.class));
                    else if (isCategoryWanted(d.getValue(Ad.class).getCategory(), wantedProd)) {
                        auxCandidateMatches.put(d.getKey().toString(), d.getValue(Ad.class));
                    }
                }

                for (int i = 0; i < offeredProd.size(); ++i) {
                    Iterator it = auxCandidateMatches.entrySet().iterator();

                    //RECORRER TODOS LOS MATCHES POSIBLES
                    while (it.hasNext()) {
                        Map.Entry<String, Ad> pair = (Map.Entry)it.next();

                        //Posible match product
                        Product posMatch = new Product(pair.getValue().getTitle(), pair.getKey().toString(), pair.getValue().getUserName(), pair.getValue().getCategory(), pair.getValue().getRating());

                        //Offered product to compare match
                        Ad auxAd = offeredProd.get(i);

                        Product auxProduct = new Product(auxAd.getTitle(), auxAd.getId(), auxAd.getUserName(), auxAd.getCategory(), auxAd.getRating());

                        //Key del producto del usuario sender con el que se quiere hacer el match
                        String productKeySender = auxProduct.getId();

                        //SI es match, AÑADIR posMatch A LOS MATCHES DE LA BD
                        if (!(isMatched(productKeySender.concat(posMatch.getId()), myMatches))&&(isMatch(auxProduct, posMatch))) {

                            //crear match y guardarlo en la BD
                            Match match = new Match(currentUsername, posMatch.getUsername(), productKeySender, posMatch.getId(), posMatch.getCategory());
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

    /*public Map<String,Match> getMyMatches (String currentUsername, final Map<String,Match> myMatches) {

        DatabaseReference mFirebaseMatches = FirebaseDatabase.getInstance().getReference().child("matches").child(currentUsername);

        final DatabaseResponse callback = new DatabaseResponse() {
            @Override
            public void success(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    myMatches.put(d.getId().toString(), d.getValue(Match.class));
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

    }*/

    private boolean isCategoryWanted (String categoryTitle, ArrayList<Product> wantedProd) {
        boolean finded = false;
        int i = 0;
        while ((!finded) && (i < wantedProd.size())) {
            if (wantedProd.get(i).getName().equals(categoryTitle)) finded = true;
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

    private boolean isMatched(String key, Map<String,Match> myMatches) {
        return (myMatches.containsKey(key));
    }
}


