package pes.twochange.domain.model;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Victor on 01/05/2017.
 */

public class Product {
    private String name;
    private String description;
    private String id;
    private String username;
    private String category;
    private int rating;
    private ArrayList<String> images;

    public Product() {
    }

    public Product(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public Product(String name, String id, String username, String category, int rating) {
        this.name = name;
        this.id = id;
        this.username = username;
        this.category = category;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategory () {
        return this.category;
    }

    public void setCategory (String category) {
        this.category = category;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating (int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public static String generateImageName() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Calendar.getInstance().getTime());
    }

    @Exclude
    public ArrayList<String> getUrls() {
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            urls.add(String.format("product/%s/%s", id, images.get(i)));
        }
        return urls;
    }

    public enum Status {
        NEW(0),
        ALMOST_NEW(5),
        GOOD(10),
        BAD(15),
        BROKEN(30);

        private int penalty;

        Status(int penalty) {
            this.penalty = penalty;
        }

        public static Status from(String s) {
            String state = s.split("-")[0].trim();
            switch (state) {
                case "New":
                    return NEW;
                case "Almost new":
                    return ALMOST_NEW;
                case "Good":
                    return GOOD;
                case "Regular":
                    return BAD;
                case "Broken":
                    return BROKEN;
                default:
                    return null;
            }
        }

        public int getPenalty() {
            return penalty;
        }
    }

    public void rate(Status state, Integer price) {
        int auxRating = 50;
        auxRating -= state.getPenalty();

        if (price != null) {
            int pricePoints = price / 10;    // 1 point each 500 €/$/?
            auxRating += pricePoints;
        }
        auxRating = auxRating < 0 ? 0 : auxRating;
        auxRating = auxRating > 100 ? 100 : auxRating;
        setRating(auxRating);
    }
}
