package pes.twochange.domain.themes;

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
    public void search(final String productName, final SearchResponse searchResponse) {
        Firebase.getInstance().get(
                "ads",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        ArrayList<String> ids = new ArrayList<>();
                        ArrayList<Profile> products = new ArrayList<>();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Profile profile = ds.getValue(Profile.class);
                            products.add(profile);
                            ids.add(profile.getUsername());
                        }
                        searchResponse.listResponse(ids, products);
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
        //TODO: La cerca retorna els noms dels productes pero volem els seus ids.
        ).with("username", productName);
    }

    //Conjunt de resultats de la cerca
    public interface SearchResponse {
        void listResponse(ArrayList<String> ids, ArrayList<Profile> products);
        void empty();
        void failure(String message);
    }
}
