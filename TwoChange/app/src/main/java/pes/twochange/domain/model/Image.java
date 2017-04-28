package pes.twochange.domain.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kredes on 15/04/2017.
 * Represents an image that can be stored in Firebase.
 */

public class Image {

    public enum Format {
        JPG(".jpg"),
        JPEG(".jpeg"),
        PNG(".png");

        private String extension;

        Format(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

        public static Format fromExtension(String extension) {
            switch (extension) {
                case ".jpg":
                    return JPG;
                case ".jpeg":
                    return JPEG;
                case ".png":
                    return PNG;
                default:
                    return null;
            }
        }
    }


    private String id;
    private Uri uri;
    private Format format;
    private Context context;


    /*
         --------------
        | CONSTRUCTORS |
         --------------
     */
    public Image(Context context) {
        this.context = context.getApplicationContext();
        id = Image.generateName();
        uri = null;
    }

    public Image(Context context, String id) {
        this(context);
        this.id = id;
    }

    public Image(Context context, Uri uri) {
        this(context);
        setUri(uri);
    }

    public Image(Context context, String id, Uri uri) {
        this(context);
        this.id = id;
        setUri(uri);
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

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            format = Format.fromExtension(filePath.substring(filePath.lastIndexOf(".")));
        } else {
            format = null;
        }
        cursor.close();

        /*
        try {
            ContentUris.parseId(uri);
            this.uri = uri;
        } catch (NumberFormatException e) { //  /path/to/image.jpg
            String uriStr = uri.toString().split("fileprovider")[1];
            this.uri = Uri.parse(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + uriStr
            );
        }
        */
    }

    public Format getFormat() { return format; }


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

    public static String generateName() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Calendar.getInstance().getTime());
    }

    public void save(StorageReference ref) {
        ref = ref.child(id + format.getExtension());
        ref.putFile(getUri());
    }
}
