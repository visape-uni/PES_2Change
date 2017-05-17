package pes.twochange.domain.themes;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Profile;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;

/**
 * Created by vilhjalmr on 5/10/17.
 */

public class AdTheme {
    //Attributes
    private Ad product;

    public AdTheme(Ad product) {
        this.product = product;
    }

    public AdTheme() {}

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

    //Conjunt de resultats de la cerca
    public interface SearchResponse {
        void listResponse(ArrayList<String> titles, ArrayList<Ad> products);
        void empty();
        void failure(String message);
    }
}
