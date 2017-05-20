package pes.twochange.services;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

public class ImageManager {
    private static final ImageManager ourInstance = new ImageManager();

    public static ImageManager getInstance() {
        return ourInstance;
    }

    private ImageManager() {
    }

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

    public void getDownloadUrl(String path, String name, UrlResponse urlResponse) {
        getDownloadUrl(path + name, urlResponse);
    }

    public interface UrlResponse {
        void onSuccess(String url);
        void onFailure(String errorMessage);
    }


}
