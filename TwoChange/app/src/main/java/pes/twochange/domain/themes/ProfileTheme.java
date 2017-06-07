package pes.twochange.domain.themes;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.ModelAdapter;
import pes.twochange.domain.model.Profile;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;

public class ProfileTheme implements ModelAdapter<Profile> {

    private final static ProfileTheme instance = new ProfileTheme();
    private Profile profile;

    public static ProfileTheme getInstance() {
        return instance;
    }

    public static ProfileTheme getInstance(Profile profile) {
        instance.profile = profile;
        return instance;
    }

    public void updateProfile(final ProfileResponse profileResponse) {

        Firebase.getInstance().get(
                "profile",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        update();
                        profileResponse.success(profile);
                    }

                    @Override
                    public void empty() {
                        insert();
                        profileResponse.success(profile);
                    }

                    @Override
                    public void failure(String message) {
                        profileResponse.failure("Something went wrong :(");
                    }
                }
        ).byId(profile.getUsername());
    }

    private void insert() {
        Firebase.getInstance().insert("profile", profile.getUsername(), this);
    }

    private void update() {
        Firebase.getInstance().update("profile", profile.getUsername(), this);
    }

    public void find(final String uid, final ProfileResponse profileResponse) {
        Firebase.getInstance().get(
                "profile",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        if (profile == null) {
                            profileResponse.failure("Cannot find any profile");
                        } else {
                            profileResponse.success(profile);
                        }
                    }

                    @Override
                    public void empty() {
                        profileResponse.failure("Cannot find any profile");
                    }

                    @Override
                    public void failure(String message) {
                        profileResponse.failure("Something went wrong :(");
                    }
                }
        ).by("uid", uid);
    }

    public void search(final String username, final SearchResponse searchResponse) {
        Firebase.getInstance().get(
                "profile",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        ArrayList<String> usernames = new ArrayList<>();
                        ArrayList<Profile> profiles = new ArrayList<>();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Profile profile = ds.getValue(Profile.class);
                            profiles.add(profile);
                            usernames.add(profile.getUsername());
                        }

                        searchResponse.listResponse(usernames, profiles);
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
        ).with("username", username);
    }

    public interface SearchResponse {
        void listResponse(ArrayList<String> usernames, ArrayList<Profile> profiles);
        void empty();
        void failure(String message);
    }

    public void isRated(String userRating, final DatabaseResponse callback) {
        DatabaseReference mFirebaseRates = FirebaseDatabase.getInstance().getReference().child("rates").child(profile.getUsername()).child(userRating);

        mFirebaseRates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
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

    public void rate(float rate, String userRating) {
        if (profile.getRate() < 0) {
            profile.setRate(0);
        }
        float actualRate = profile.getRate()*profile.getNumRates();
        profile.incNumRates();
        profile.setRate((actualRate+rate)/profile.getNumRates());

        DatabaseReference mFirebaseRates = FirebaseDatabase.getInstance().getReference().child("rates").child(profile.getUsername()).child(userRating);
        mFirebaseRates.setValue(rate);

        //actualitzar el profile
        update();
    }



    public void get(final String username, final ProfileResponse profileResponse) {
        Firebase.getInstance().get(
                "profile",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        profileResponse.success(profile);
                    }

                    @Override
                    public void empty() {
                        profileResponse.failure("No profile with username = " + username);
                    }

                    @Override
                    public void failure(String message) {
                        profileResponse.failure("Something went wrong :(");
                    }
                }
        ).byId(username);
    }

    @Override
    public Class classType() {
        return Profile.class;
    }

    @Override
    public Profile object() {
        return profile;
    }

    /*
    public void updateImage(final String id, final Bitmap image, final ImageResponse imageResponse) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://change-64bd0.appspot.com/");
        final StorageReference reference = storageReference.child("profile/" + id + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageResponse.failure(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageResponse.success(image);
            }
        });
    }

    public void getImage(String id, final ImageResponse imageResponse) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://change-64bd0.appspot.com/");
        final StorageReference reference = storageReference.child("profile/" + id + ".jpg");

        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                long size = storageMetadata.getSizeBytes();
                reference.getBytes(size).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageResponse.success(image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imageResponse.failure(e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageResponse.failure(e.getMessage());
            }
        });
    }
    */
}
