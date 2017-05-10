package pes.twochange.domain.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kredes on 07/04/2017.
 */

public class Ad extends Model {

    public enum ProductState {
        NEW(0),
        ALMOST_NEW(5),
        GOOD(10),
        BAD(15),
        BROKEN(30);

        private int penalty;

        ProductState(int penalty) {
            this.penalty = penalty;
        }

        public static ProductState from(String s) {
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


    private static final int MAX_IMAGES = 4;
    private static final String OUT_OF_BOUNDS_MESSAGE = "Image index must be between 0 and " + MAX_IMAGES;
    private static DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://change-64bd0.firebaseio.com/").child("ads");
    private static DatabaseReference mFirebaseOfferedList = FirebaseDatabase.getInstance().getReference().child("lists");

    private Profile user;
    private String userName;

    private String title;
    private String description;
    private int rating;
    private String category;

    private List<Image> images;

    private List<String> wants;


    /*
         --------------
        | CONSTRUCTORS |
         --------------
     */
    public Ad() {
        rating = 0;
        images = new ArrayList<>(MAX_IMAGES);
        wants = new ArrayList<>();

        for (int i = 0; i < MAX_IMAGES; ++i) {
            images.add(i, null);
        }
    }

    public Ad(Profile user, String title, String description) {
        this();
        this.user = user;
        this.title = title;
        this.description = description;
    }

    /*
         ---------------------
        | GETTERS AND SETTERS |
         ---------------------
     */
    @Exclude public Profile getUser() {
        return user;
    }
    @Exclude public void setUser(Profile user) {
        this.user = user;
        setUserName(user.getUsername());
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude public List<Image> getImages() {
        return images;
    }
    public void setImageAt(int index, Image image) {
        if (index < 0 || index > MAX_IMAGES - 1) throw new IndexOutOfBoundsException(OUT_OF_BOUNDS_MESSAGE);
        images.set(index, image);
    }
    public void removeImageAt(int index) {
        if (index < 0 || index > MAX_IMAGES - 1) throw new IndexOutOfBoundsException(OUT_OF_BOUNDS_MESSAGE);
        images.set(index, null);
    }
    public Image getImageAt(int index) {
        if (index < 0 || index > MAX_IMAGES - 1) throw new IndexOutOfBoundsException(OUT_OF_BOUNDS_MESSAGE);
        return images.get(index);
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        if (rating > 100)
            this.rating = 100;
        else
            this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /*
         ----------------------
        | OTHER PUBLIC METHODS |
         ----------------------
     */
    public void rate(ProductState state, int year, int price) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int pricePoints = price/500;    // 1 point each 500 €/$/?

        int auxRating = 100;
        auxRating -= state.getPenalty();
        auxRating -= currentYear - year;
        auxRating += pricePoints;

        setRating(auxRating);
    }

    public void save() {
        DatabaseReference newAdRef = db.push();
        setId(newAdRef.getKey());
        newAdRef.setValue(this);

        DatabaseReference newOffered = mFirebaseOfferedList.child(this.getUserName()).child("offered").child(newAdRef.getKey());
        newOffered.setValue(new Product(this.getTitle(), newAdRef.getKey()));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReferenceFromUrl("gs://change-64bd0.appspot.com").child("ads").child(getId()).child("images");

        List<String> imageIds = new ArrayList<>();
        for (Image image : images) {
            if (image != null) {
                image.save(storageRef);
                imageIds.add(image.getId() + image.getFormat().getExtension());
            }
        }

        newAdRef.child("images").setValue(imageIds);
    }
}
