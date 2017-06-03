package pes.twochange.domain.themes;

import android.net.Uri;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pes.twochange.domain.callback.AdResponse;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Image;
import pes.twochange.domain.model.Product;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;
import pes.twochange.services.ImageManager;
import pes.twochange.services.ModelAdapterFactory;

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

    private static final String PRODUCTS_REFERENCE_NAME = "products";
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
        newOffered.setValue(new Product(ad.getTitle(), newAdRef.getKey(), ad.getUserName(), ad.getCategory(), ad.getRating()));

        List<String> imageIds = new ArrayList<>();
        ImageManager imageManager = ImageManager.getInstance();
        for (Image image : ad.getImagesFile()) {
            if (image != null) {
                String completePath = ad.getImagesPath() + image.getFirebaseName();
                imageManager.storeImage(completePath, image.getUri());
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

    public String save(final Product product) {
        return Firebase.getInstance().insert(PRODUCTS_REFERENCE_NAME,
                new ModelAdapterFactory<Product>().build(Product.class, product));
    }

    /* ------------------
        WANTED / OFFERED
       ------------------ */
    public void remove(String username, String key) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("lists")
                .child(username)
                .child("wanted")
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

    public void getWantedList(String username, final ProductListResponse response, final ErrorResponse error) {
        Firebase.getInstance().get(
                "lists/" + username + "/wanted",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        ArrayList<Product> adArrayList = new ArrayList<>();
                        GenericTypeIndicator<HashMap<String, Product>> typeIndicator =
                                new GenericTypeIndicator<HashMap<String, Product>>() {};
                        HashMap<String, Product> firebase = dataSnapshot.getValue(typeIndicator);
                        if (firebase != null) {
                            adArrayList = new ArrayList<>(firebase.values());
                        }
                        response.listResponse(adArrayList);
                    }

                    @Override
                    public void empty() {
                        response.listResponse(new ArrayList<Product>());
                    }

                    @Override
                    public void failure(String message) {
                        error.error(message);
                    }
                }
        ).list();
    }

    public void getOfferedList(final String username, final ListResponse response, final ErrorResponse error) {
        getAllAds(
                new ListResponse() {
                    @Override
                    public void listResponse(ArrayList<Ad> productItems) {
                        ArrayList<Ad> offered = new ArrayList<>();
                        for (Ad product: productItems) {
                            if (product.getUserName().equals(username)) {
                                offered.add(product);
                            }
                        }
                        response.listResponse(offered);
                    }
                }, error
        );
    }

    public void getAllAds(final ListResponse response, final ErrorResponse error) {
        Firebase.getInstance().get(
                "ads",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        ArrayList<Ad> adArrayList = new ArrayList<>();
                        GenericTypeIndicator<HashMap<String, Ad>> typeIndicator =
                                new GenericTypeIndicator<HashMap<String, Ad>>() {};
                        HashMap<String, Ad> firebase = dataSnapshot.getValue(typeIndicator);
                        if (firebase != null) {
                            adArrayList = new ArrayList<>(firebase.values());
                        }
                        response.listResponse(adArrayList);
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

    public void getAllProducts(final ProductListResponse response, final ErrorResponse error) {
        Firebase.getInstance().get(
                "products",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        ArrayList<Product> res = new ArrayList<>();
                        for (DataSnapshot productData: dataSnapshot.getChildren()) {
                            Product product = productData.getValue(Product.class);
                            product.setId(productData.getKey());
                            res.add(product);
                        }
                        response.listResponse(res);
                    }

                    @Override
                    public void empty() {
                        response.listResponse(new ArrayList<Product>());
                    }

                    @Override
                    public void failure(String message) {
                        error.error(message);
                    }
                }
        ).list();
    }

    public void storeImages(String path, ArrayList<String> images, ArrayList<Uri> imageUris) {
        int size = images.size() <= imageUris.size() ? images.size() : imageUris.size();
        for (int i = 0; i < size; i++) {
            ImageManager.getInstance().storeImage(path + images.get(i), imageUris.get(i));
        }
    }

    public interface ListResponse {
        void listResponse(ArrayList<Ad> productItems);
    }

    public interface ProductListResponse {
        void listResponse(ArrayList<Product> productItems);
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
