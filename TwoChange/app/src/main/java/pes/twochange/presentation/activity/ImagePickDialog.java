package pes.twochange.presentation.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by kredes on 15/04/2017.
 */

public class ImagePickDialog extends DialogFragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private Context context;

    public interface ImagePickListener {
        void onImageSourceSelected(ImageSource source, int imageButtonTag);
    }

    public enum ImageSource {
        GALLERY, CAMERA
    }

    private ImagePickListener imagePickListener;
    private int imageButtonTag;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ImagePickListener so we can send events to the host
            imagePickListener = (ImagePickListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ImagePickListener");
        }


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] items = {"Gallery", "Camera"};

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle("Add an image")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        ImageSource source = null;
                        if (item == 0) source = ImageSource.GALLERY;
                        else source = ImageSource.CAMERA;

                        imagePickListener.onImageSourceSelected(source, imageButtonTag);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }

    public void setImageButtonTag(int tag) {
        this.imageButtonTag = tag;
    }

    public int getImageButtonTag() {
        return imageButtonTag;
    }

}
