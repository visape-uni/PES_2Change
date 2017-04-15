package pes.twochange.domain.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by kredes on 15/04/2017.
 * Represents an image that can be stored in Firebase.
 */

public class Image {
    private String id;
    private Uri uri;
    private Context context;


    /*
         --------------
        | CONSTRUCTORS |
         --------------
     */
    public Image(Context context) {
        this.context = context;
        id = null;
        uri = null;
    }

    public Image(Context context, String id) {
        this(context);
        this.id = id;
    }

    public Image(Context context, Uri uri) {
        this(context);
        this.uri = uri;
    }

    public Image(Context context, String id, Uri uri) {
        this(context);
        this.id = id;
        this.uri = uri;
    }


    /*
         ---------------------
        | GETTERS AND SETTERS |
         ---------------------
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }


    /*
         ----------------------
        | OTHER PUBLIC METHODS |
         ----------------------
     */
    public Bitmap toBitmap() throws IOException {
        if (uri != null) {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } else {
            // Get it from Firebase
        }
        return null;
    }
}
