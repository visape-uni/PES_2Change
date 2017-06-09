package pes.twochange.presentation.adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;


public class RecyclerViewImagesAdapter extends
        RecyclerView.Adapter<RecyclerViewImagesAdapter.ViewHolder> {

    private Context activity;
    private OnRecyclerViewItemLongClickListener listener;
    private ArrayList<Uri> uris;

    public RecyclerViewImagesAdapter(Context activity,
                                     OnRecyclerViewItemLongClickListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void setUris(ArrayList<Uri> uris) {
        this.uris = uris;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_view,
                null);
        return new RecyclerViewImagesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewImagesAdapter.ViewHolder holder, int position) {
        final Uri imgUri = uris.get(position);
        try {
            Bitmap thumbnail =
                    MediaStore.Images.Thumbnails.getThumbnail(
                            activity.getContentResolver(),
                            ContentUris.parseId(uris.get(position)),
                            MediaStore.Images.Thumbnails.MICRO_KIND, null);
            holder.imageView.setImageBitmap(thumbnail);



        } catch (NumberFormatException e) {
            String[] toScan = {imgUri.getPath()};

            MediaScannerConnection.scanFile(activity, toScan, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            if (uri != null) {
                                final Bitmap thumbnail =
                                        MediaStore.Images.Thumbnails.getThumbnail(
                                                activity.getContentResolver(),
                                                ContentUris.parseId(uri),
                                                MediaStore.Images.Thumbnails.MICRO_KIND, null);

                                ((Activity) activity).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.imageView.setImageBitmap(thumbnail);
                                    }
                                });
                            } else {
                                String s = "test";
                            }
                        }
                    });
        }


        //Picasso.with(activity).load(uris.get(position)).into(holder.imageView);
        holder.imageView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return listener.onRecyclerViewItemLongClickListener(
                                holder.getAdapterPosition());
                    }
                }
        );

    }

    @Override
    public int getItemCount() {
        return uris == null ? 0 : uris.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

        public void displayImage(Bitmap image) {

        }
    }
}
