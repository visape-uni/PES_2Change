package pes.twochange.presentation.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pes.twochange.R;

public class RecyclerViewImagesAdapter extends
        RecyclerView.Adapter<RecyclerViewImagesAdapter.ViewHolder> {

    private Context activity;
    private ArrayList<Uri> uris;

    public RecyclerViewImagesAdapter(Context activitys) {
        this.activity = activity;
    }

    public void setUris(ArrayList<Uri> uris) {
        this.uris = uris;
    }

    @Override
    public RecyclerViewImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerViewImagesAdapter.ViewHolder holder, int position) {
        Picasso.with(activity).load(uris.get(position)).into(holder.imageView);
//        holder.imageView.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
