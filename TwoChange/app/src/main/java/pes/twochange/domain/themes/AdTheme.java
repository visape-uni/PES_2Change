package pes.twochange.domain.themes;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import pes.twochange.domain.callback.AdResponse;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Image;
import pes.twochange.domain.model.Product;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;

public class AdTheme {
    //Attributes
    private Ad product;

    public AdTheme(Ad product) {
        this.product = product;
    }

    public AdTheme() {}

    private static final AdTheme ourInstance = new AdTheme();
    private static final String REFERENCE = "lists";
    private static String user;

    private static final String ADS_CHILD = "ads";

    public static AdTheme getInstance() {
        return ourInstance;
    }


    public void save(final Ad ad, final AdResponse callback) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        DatabaseReference adsRef = db.child(ADS_CHILD);
        DatabaseReference offeredListRef = db.child("lists");

        DatabaseReference newAdRef = adsRef.push();
        ad.setId(newAdRef.getKey());

        DatabaseReference newOffered = offeredListRef.child(ad.getUserName()).child("offered").child(newAdRef.getKey());
        newOffered.setValue(new Product(ad.getTitle(), newAdRef.getKey(), ad.getUserName()));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReferenceFromUrl("gs://change-64bd0.appspot.com").child("ads").child(ad.getId()).child("images");

        List<String> imageIds = new ArrayList<>();
        for (Image image : ad.getImages()) {
            if (image != null) {
                image.save(storageRef);
                imageIds.add(image.getId() + image.getFormat().getExtension());
            }
        }

        DatabaseReference.CompletionListener listener = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) callback.onSuccess(ad);
                else callback.onFailure(databaseError.getMessage());
            }
        };

        newAdRef.setValue(ad, listener);
        newAdRef.child("images").setValue(imageIds);
    }

    public void update(final Ad ad, final AdResponse callback) {
        DatabaseReference.CompletionListener listener = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) callback.onSuccess(ad);
                else callback.onFailure(databaseError.getMessage());
            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child(ADS_CHILD)
                .child(ad.getId())
                .setValue(ad, listener);
    }

    public void delete(final Ad ad, final AdResponse callback) {
        DatabaseReference.CompletionListener listener = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) callback.onSuccess(ad);
                else callback.onFailure(databaseError.getMessage());
            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child(ADS_CHILD)
                .child(ad.getId())
                .removeValue(listener);
    }

    public void findById(String id, final AdResponse callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ad ad = dataSnapshot.getValue(Ad.class);
                ad.setId(dataSnapshot.getKey());
                callback.onSuccess(ad);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child(ADS_CHILD)
                .child(id)
                .addListenerForSingleValueEvent(listener);
    }



    /* ------------------
        WANTED / OFFERED
       ------------------ */
    public void remove(String username, String list, String key) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(REFERENCE)
                .child(username)
                .child(list)
                .child(key)
                .removeValue();
    }

    //Fa la cerca dels productes segons el nom introduit per l'usuari
    public void search(String productName, final SearchResponse searchResponse) {
        productName = productName.toUpperCase();
        Firebase.getInstance().get(
                "ads",
                new DatabaseResponse() {

                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        ArrayList<String> titles = new ArrayList<>();
                        ArrayList<Ad> products = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Ad product = ds.getValue(Ad.class);
                            products.add(product);
                            titles.add(product.getTitle());
                        }
                        searchResponse.listResponse(titles, products);
                    }

                    @Override
                    public void empty() {
                        searchResponse.empty();
                    }

                    @Override
                    public void failure(String message) {
                        searchResponse.failure(message);
                    }
                }
        ).with("title", productName);
    }

    public void getWantedList(String username, final WantedResponse response, final ErrorResponse error) {
        Firebase.getInstance().get(
                "lists/" + username + "/wanted",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        ArrayList<Product> products = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            products.add(ds.getValue(Product.class));
                        }
                        response.wantedListResponse(products);
                    }

                    @Override
                    public void empty() {
                        response.wantedListResponse(new ArrayList<Product>());
                    }

                    @Override
                    public void failure(String message) {
                        error.error(message);
                    }
                }
        ).list();
    }

    public void getOfferedList(String username, final ListResponse response, final ErrorResponse error) {
        Firebase.getInstance().get(
                "lists/" + username + "/offered",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        ArrayList<Ad> ads = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ads.add(ds.getValue(Ad.class));
                        }
                        response.listResponse(ads);
                    }

                    @Override
                    public void empty() {
                        response.listResponse(new ArrayList<Ad>());
                    }

                    @Override
                    public void failure(String message) {
                        error.error(message);
                    }
                }
        ).list();
    }

    public interface ListResponse {
        void listResponse(ArrayList<Ad> productItems);
    }

    public interface WantedResponse {
        void wantedListResponse(ArrayList<Product> productItems);
    }

    public interface ErrorResponse {
        void error(String error);
    }

    //Conjunt de resultats de la cerca
    public interface SearchResponse {
        void listResponse(ArrayList<String> titles, ArrayList<Ad> products);
        void empty();
        void failure(String message);
    }
}
