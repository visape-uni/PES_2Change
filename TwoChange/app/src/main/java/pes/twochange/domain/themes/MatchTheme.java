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
import pes.twochange.domain.model.ModelAdapter;
import pes.twochange.domain.model.Product;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;
import pes.twochange.services.ModelAdapterFactory;

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

    public void getMatches(String username, final MatchesResponse response,
                           final MatchTheme.ErrorResponse errorResponse) {
        Log.v("GETMATCHES", username);
        Firebase.getInstance().get(
                "matches/" + username,
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        Log.v("GETMATCHES", dataSnapshot.toString());
                        GenericTypeIndicator<HashMap<String, Match>> typeIndicator =
                                new GenericTypeIndicator<HashMap<String, Match>>() {};
                        response.success(dataSnapshot.getValue(typeIndicator));
                    }

                    @Override
                    public void empty() {
                        response.success(new HashMap<String, Match>());
                    }

                    @Override
                    public void failure(String message) {
                        errorResponse.error(message);
                    }
                }
        ).list();
    }

    public void getProductsMatch(final Match match, final MatchResponse response, final ErrorResponse errorResponse) {
        AdTheme.getInstance().getProduct(
                match.getProductKeyReceiver(),
                new AdTheme.ProductResponse() {
                    @Override
                    public void success(Product product) {
                        final Product wantedProduct = product;
                        AdTheme.getInstance().getProduct(
                                match.getProductKeySender(),
                                new AdTheme.ProductResponse() {
                                    @Override
                                    public void success(Product offeredProduct) {
                                        response.success(wantedProduct, offeredProduct, match);
                                    }

                                    @Override
                                    public void error(String error) {
                                        errorResponse.error(error);
                                    }
                                }
                        );
                    }

                    @Override
                    public void error(String error) {
                        errorResponse.error(error);
                    }
        });
    }

    public void makeMatches(final String currentUsername, final Map<String, Match> matches,
                            ArrayList<Product> wanted, final MatchesResponse result) {

        username = currentUsername;
        firebaseMatches = mainRef.child("matches").child(username);
        this.myMatches = matches == null ? new HashMap<String, Match>() : matches;
        this.wanted = wanted == null ? new ArrayList<Product>() : wanted;

        offered = new ArrayList<>();

        callback = new DatabaseResponse() {
            @Override
            public void success(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    product.setId(child.getKey());
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
                                    .concat(match.getProductKeyReceiver())).setValue(match);
                            myMatches.put(match.getProductKeySender()
                                    .concat(match.getProductKeyReceiver()),match);
                        }
                    }
                }

                result.success(myMatches);
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

    public void decline(Match match) {
        match.setStatus(Match.Status.DENIED);
        update(match);
    }

    public void accept(Match match) {
        match.setStatus(Match.Status.ACCEPTED);
        update(match);
    }

    public void update(Match match) {
        ModelAdapter<Match> matchModelAdapter = new ModelAdapterFactory<Match>().build(Match.class,
                match);
        Firebase.getInstance().update("matches/" + match.getUsernameSender(),
                match.getProductKeySender() + match.getProductKeyReceiver(),
                matchModelAdapter);
    }

    public void deleteMatchesWith(final String id) {
        FirebaseDatabase.getInstance().getReference().child("matches").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds0 : dataSnapshot.getChildren()) {
                            for (DataSnapshot ds1 : ds0.getChildren()) {
                                if (ds1.getKey().contains(id)) {
                                    ds1.getRef().removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public interface MatchesResponse {
        void success(Map<String, Match> myMatches);
    }

    public interface MatchResponse {
        void success(Product wantedProduct, Product offeredProduct, Match match);
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


