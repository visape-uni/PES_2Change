package pes.twochange.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import pes.twochange.R;

public class ImageManager {

    private static final ImageManager ourInstance = new ImageManager();
    private static final String IMAGE_DEBUG_TAG = "COMPRESS BITMAP DEBUG";

    public static ImageManager getInstance() {
        return ourInstance;
    }

    private ImageManager() {
    }

    public void putImageIntoView(String completePath, final Context context, final ImageView imageView) {
        getDownloadUrl(
                completePath,
                new UrlResponse() {
                    @Override
                    public void onSuccess(String url) {
                        Picasso.with(context).load(url).placeholder(R.drawable.progress_animation)
                                .error(R.mipmap.placeholder).into(imageView);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        //Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // Give the path of the image (for example: test/tom.jpg) and a callback
    // onSuccess(String url) gives the URL to download the image
    // onFailure(String errorMessage) gives the message of the exception if there's an error
    private void getDownloadUrl(String completePath, final UrlResponse urlResponse) {
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

    interface UrlResponse {
        void onSuccess(String url);
        void onFailure(String errorMessage);
    }

    private void storeImage(String completePath, Bitmap bitmap) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child(completePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imageReference.putBytes(data);
    }

    public void storeImage(String completePath, Uri fileUri, Context context) {
        Bitmap bitmap = compressBitmap(fileUri, context);
        storeImage(completePath, bitmap);
    }

    private Bitmap compressBitmap(Uri fileUri, Context context) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),fileUri);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            int nh = (int) (height * (400.0 / width));
            return Bitmap.createScaledBitmap(bitmap, 400, nh, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
