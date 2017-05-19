package pes.twochange.domain.themes;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import pes.twochange.domain.model.Ad;
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

    public static AdTheme getInstance() {
        return ourInstance;
    }

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
