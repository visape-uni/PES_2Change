package pes.twochange.domain.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private static DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("ads");
    private static DatabaseReference mFirebaseOfferedList = FirebaseDatabase.getInstance().getReference().child("lists");
    private static DatabaseReference mFirebaseCategory = FirebaseDatabase.getInstance().getReference().child("categories");

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
    public void addImage(Image image) {
        for (int i = 0; i < images.size(); ++i) {
            if (images.get(i) == null) {
                setImageAt(i, image);
                break;
            }
        }
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
    public void rate(ProductState state, Integer year, Integer price) {
        int auxRating = 100;
        auxRating -= state.getPenalty();

        if (year != null) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            auxRating -= currentYear - year;
        }

        if (price != null) {
            int pricePoints = price / 500;    // 1 point each 500 €/$/?
            auxRating += pricePoints;
        }

        setRating(auxRating < 0 ? 0 : auxRating);
    }

    public void save() {
        DatabaseReference newAdRef = db.push();
        setId(newAdRef.getKey());
        newAdRef.setValue(this);

        DatabaseReference newOffered = mFirebaseOfferedList.child(this.getUserName()).child("offered").child(newAdRef.getKey());
        newOffered.setValue(new Product(this.getTitle(), newAdRef.getKey(), this.getUserName(), this.category, this.rating));


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

    public void update() {
        db.child(getId()).setValue(this);
    }

    public void delete() {
        db.child(getId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }

    public void delete(DatabaseReference.CompletionListener listener) {
        db.child(getId()).removeValue(listener);
    }

    public static void findById(String id, @NonNull ValueEventListener listener) {
        db.child(id).addListenerForSingleValueEvent(listener);
    }

    @Exclude public StorageReference getStorageReference() {
        return FirebaseStorage.getInstance().getReferenceFromUrl("gs://change-64bd0.appspot.com").child("ads").child(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (obj.getClass() != getClass())
            return false;

        return ((Ad) obj).getId().equals(getId());
    }
}
