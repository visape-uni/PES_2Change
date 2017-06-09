package pes.twochange.domain.themes;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.database.ChildEventListener;
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
import pes.twochange.domain.model.ModelAdapter;
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


    public void save(final Ad ad, final AdResponse callback, Context context) {
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
                String filename = image.getFirebaseName();
                ad.getImages().add(filename);

                String completePath = ad.getImagesPath() + filename;
                imageManager.storeImage(completePath, image.getUri(), context);
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

    public void delete(Product product) {
        Firebase.getInstance().delete("products", product.getId());
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

    public void getFirst(int n, final AdResponse callback) {
        FirebaseDatabase.getInstance().getReference()
                .child(ADS_CHILD)
                .orderByKey()
                .limitToFirst(n)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Ad ad = dataSnapshot.getValue(Ad.class);
                        callback.onSuccess(ad);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onFailure(databaseError.getMessage());
                    }
                });
    }

    public void getNext(int n, final String startKey, final AdResponse callback) {
        FirebaseDatabase.getInstance().getReference()
                .child(ADS_CHILD)
                .orderByKey()
                .startAt(startKey)
                .limitToFirst(n)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Ad ad = dataSnapshot.getValue(Ad.class);
                        callback.onSuccess(ad);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onFailure(databaseError.getMessage());
                    }
                });
    }



    public String save(final Product product) {
        return Firebase.getInstance().insert(PRODUCTS_REFERENCE_NAME,
                new ModelAdapterFactory<Product>().build(Product.class, product));
    }

    public void update(final Product product) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("products")
                .child(product.getId());
        ref.child("name").setValue(product.getName());
        ref.child("description").setValue(product.getDescription());
        ref.child("category").setValue(product.getCategory());
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
                        ArrayList<Product> productArrayList = new ArrayList<>();
                        GenericTypeIndicator<HashMap<String, Product>> typeIndicator =
                                new GenericTypeIndicator<HashMap<String, Product>>() {};
                        HashMap<String, Product> firebase = dataSnapshot.getValue(typeIndicator);
                        if (firebase != null) {
                            productArrayList = new ArrayList<>(firebase.values());
                        }
                        response.listResponse(productArrayList);
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

    public void getOfferedList(final String username, final ProductListResponse response, final ErrorResponse error) {
        getAllProducts(
                new ProductListResponse() {
                    @Override
                    public void listResponse(ArrayList<Product> productItems) {
                        ArrayList<Product> offered = new ArrayList<>();
                        for (Product product: productItems) {
                            if (product.getUsername().equals(username)) {
                                offered.add(product);
                            }
                        }
                        response.listResponse(offered);
                    }
                }, error
        );
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

    public void storeImages(String path, ArrayList<String> images, ArrayList<Uri> imageUris,
                            Context context) {
        int size = images.size() <= imageUris.size() ? images.size() : imageUris.size();
        for (int i = 0; i < size; i++) {
            ImageManager.getInstance().storeImage(path + images.get(i), imageUris.get(i), context);
        }
    }

    public void getProduct(final String id, final ProductResponse productResponse) {
        Firebase.getInstance().get(
                "products",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        Product product = dataSnapshot.getValue(Product.class);
                        product.setId(id);
                        productResponse.success(product);
                    }

                    @Override
                    public void empty() {
                        productResponse.error("No product");
                    }

                    @Override
                    public void failure(String message) {
                        productResponse.error("Something went wrong :(");
                    }
                }
        ).byId(id);
    }

    public void postWanted(String username, String category) {
        Product product = new Product();
        product.setCategory(category);
        String path = "lists/" + username + "/wanted";
        ModelAdapter<Product> pma = new ModelAdapterFactory<Product>().build(Product.class, product);
        Firebase.getInstance().insert(path, pma);
    }

    public interface ProductResponse {
        void success(Product product);
        void error(String error);
    }

    public interface ListResponse {
        void listResponse(ArrayList<Ad> productItems);
    }

    public interface ProductListResponse {
        void listResponse(ArrayList<Product> productItems);
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
