package pes.twochange.domain.themes;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pes.twochange.domain.model.Match;
import pes.twochange.domain.model.Product;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;

public class MatchTheme {

    private static final MatchTheme ourInstance = new MatchTheme();
    private static final String TAG = "MatchTheme";
    private Map<String, Product> auxCandidateMatches = new HashMap<>();
    private ArrayList<Product> offered;
    private ArrayList<Product> wanted;
    private Map<String, Match> myMatches;
    private String username;
    private DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference firebaseMatches = mainRef.child("matches");
    private DatabaseReference firebaseProducts = mainRef.child("products");
    private DatabaseResponse callback;

    public static MatchTheme getInstance() {
        return ourInstance;
    }

    public void getMatches(String username, final MatchesFinished response,
                           final MatchTheme.ErrorResponse errorResponse) {
        Firebase.getInstance().get(
                "matches/" + username,
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String, Match>> typeIndicator =
                                new GenericTypeIndicator<HashMap<String, Match>>() {};
                        response.onFinish(dataSnapshot.getValue(typeIndicator));
                    }

                    @Override
                    public void empty() {
                        response.onFinish(new HashMap<String, Match>());
                    }

                    @Override
                    public void failure(String message) {
                        errorResponse.error(message);
                    }
                }
        );
    }

    public void makeMatches(final String currentUsername, final Map<String, Match> matches,
                            ArrayList<Product> wanted, final MatchesFinished result) {

        username = currentUsername;
        firebaseMatches = firebaseMatches.child(username);
        this.myMatches = matches == null ? new HashMap<String, Match>() : matches;
        this.wanted = wanted == null ? new ArrayList<Product>() : wanted;

        offered = new ArrayList<>();

        callback = new DatabaseResponse() {
            @Override
            public void success(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    product.setId(child.getKey());
                    Log.v("MATCH", product.getCategory() + " " + product.getName());
                    if (product.getUsername().equals(username)) {
                        offered.add(product);
                    } else if (isCategoryWanted(product.getCategory())) {
                        auxCandidateMatches.put(product.getId(), product);
                    }
                }

                for (Product offeredProductToCompare : offered) {
                    Iterator it = auxCandidateMatches.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Product> pair = (Map.Entry) it.next();

                        Product possibleMatch = pair.getValue();
                        String productKey = offeredProductToCompare.getId();
                        if (!isMatched(productKey.concat(possibleMatch.getId()))
                                && isMatch(offeredProductToCompare, possibleMatch)) {
                            Match match = new Match(username, possibleMatch.getUsername(),
                                    productKey, possibleMatch.getId(), possibleMatch.getCategory());
                            firebaseMatches.child(match.getProductKeySender()
                                    .concat(match.getProductKeyReciver())).setValue(match);
                            myMatches.put(match.getProductKeySender()
                                    .concat(match.getProductKeyReciver()),match);
                        }
                    }
                }

                result.onFinish(myMatches);

//                for (int i = 0; i < offeredProd.size(); ++i) {
//                    Iterator it = auxCandidateMatches.entrySet().iterator();
//
//                    //RECORRER TODOS LOS MATCHES POSIBLES
//                    while (it.hasNext()) {
//                        Map.Entry<String, Ad> pair = (Map.Entry)it.next();
//
//                        //Posible match product
//                        Product posMatch = new Product(pair.getValue().getTitle(), pair.getKey().toString(), pair.getValue().getUserName(), pair.getValue().getCategory(), pair.getValue().getRating());
//
//                        //Offered product to compare match
//                        Ad auxAd = offeredProd.get(i);
//
//                        Product auxProduct = new Product(auxAd.getTitle(), auxAd.getId(), auxAd.getUserName(), auxAd.getCategory(), auxAd.getRating());
//
//                        //Key del producto del usuario sender con el que se quiere hacer el match
//                        String productKeySender = auxProduct.getId();
//
//                        //SI es match, AÑADIR posMatch A LOS MATCHES DE LA BD
//                        if (!(isMatched(productKeySender.concat(posMatch.getId()), myMatches))&&(isMatch(auxProduct, posMatch))) {
//
//                            //crear match y guardarlo en la BD
//                            Match match = new Match(username, posMatch.getUsername(), productKeySender, posMatch.getId(), posMatch.getCategory());
//                            mFirebaseMatches.child(match.getProductKeySender().concat(match.getProductKeyReciver())).setValue(match);
//                            myMatches.put(match.getProductKeySender().concat(match.getProductKeyReciver()),match);
//                        }
//                    }
//                }



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

        firebaseProducts.addListenerForSingleValueEvent(new ValueEventListener() {
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
                callback.failure(databaseError.toString());
            }
        });
    }

    private boolean isMatched(String key) {
        return (myMatches.containsKey(key));
    }

    private boolean isCategoryWanted(String category) {
        for (Product product : wanted) {
            if (product.getCategory().equals(category)) return true;
        }
        return false;
    }

    private boolean isMatch (Product offeredProd, Product posMatchProd) {
        int rateVariable = 10;

        return (offeredProd.getRating() >= (posMatchProd.getRating() - rateVariable))
                && (offeredProd.getRating() <= (posMatchProd.getRating() + rateVariable));
    }

    public MatchTheme setWanted(ArrayList<Product> wanted) {
        this.wanted = wanted;
        return this;
    }

    public interface MatchesFinished {
        void onFinish(Map<String, Match> myMatches);
    }

    public interface ErrorResponse {
        void error(String error);
    }

//    private boolean isCategoryWanted (String categoryTitle, ArrayList<Product> wantedProd) {
//        boolean finded = false;
//        int i = 0;
//        while ((!finded) && (i < wantedProd.size())) {
//            if (wantedProd.get(i).getName().equals(categoryTitle)) finded = true;
//            ++i;
//        }
//        return finded;
//    }



//    private boolean isMatched(String key, Map<String,Match> myMatches) {
//        return (myMatches.containsKey(key));
//    }
}


