package pes.twochange.domain.themes;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import pes.twochange.domain.callback.ProfileResponse;
import pes.twochange.domain.model.ModelAdapter;
import pes.twochange.domain.model.Profile;
import pes.twochange.services.DatabaseResponse;
import pes.twochange.services.Firebase;

public class ProfileTheme implements ModelAdapter<Profile> {

    private Profile profile;

    public ProfileTheme(Profile profile) {
        this.profile = profile;
    }

    public ProfileTheme() {}

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
                        String id = insert();
                        profile.setId(id);
                        profileResponse.success(profile);
                    }

                    @Override
                    public void failure(String message) {
                        profileResponse.failure("Something went wrong :(");
                    }
                }
        ).by("uid", profile.getUid());
    }

    private String insert() {
        return Firebase.getInstance().insert("profile", this);
    }

    private void update() {
        Firebase.getInstance().update("profile", profile.getId(), this);
    }


    public void get(final String uid, final ProfileResponse profileResponse) {
        Firebase.getInstance().get(
                "profile",
                new DatabaseResponse() {
                    @Override
                    public void success(DataSnapshot dataSnapshot) {
                        Log.v("ProfileTheme", dataSnapshot.getValue().toString());
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        profile.setId(dataSnapshot.getKey());
                        profileResponse.success(profile);
                    }

                    @Override
                    public void empty() {
                        profileResponse.failure("No profile with uid = " + uid);
                    }

                    @Override
                    public void failure(String message) {
                        profileResponse.failure("Something went wrong :(");
                    }
                }
        ).by("uid", uid);

    }

    @Override
    public Class classType() {
        return Profile.class;
    }

    @Override
    public Profile object() {
        Profile result = profile;
        profile.setId(null);
        return result;
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
