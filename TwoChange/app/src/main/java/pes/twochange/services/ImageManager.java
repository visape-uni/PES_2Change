package pes.twochange.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ImageManager {

    private static final ImageManager ourInstance = new ImageManager();

    public static ImageManager getInstance() {
        return ourInstance;
    }

    private ImageManager() {
    }

    // Give the path of the image (for example: test/tom.jpg) and a callback
    // onSuccess(String url) gives the URL to download the image
    // onFailure(String errorMessage) gives the message of the exception if there's an error
    public void getDownloadUrl(String completePath, final UrlResponse urlResponse) {
        FirebaseStorage.getInstance().getReference().child(completePath)
                .getDownloadUrl()
                .addOnSuccessListener(
                        new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                urlResponse.onSuccess(uri.toString());
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                urlResponse.onFailure(e.getMessage());
                            }
                        }
                );
    }

    public interface UrlResponse {
        void onSuccess(String url);
        void onFailure(String errorMessage);
    }

    public void storeImage(String completePath, Bitmap bitmap, final UploadResponse response) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child(completePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                response.onFailure(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                if (downloadUri != null) {
                    response.onSuccess(downloadUri.toString());
                } else {
                    response.onSuccess(null);
                }
            }
        });

    }

    public interface UploadResponse {
        void onSuccess(@Nullable  String url);
        void onFailure(String errorMessage);
    }

}
