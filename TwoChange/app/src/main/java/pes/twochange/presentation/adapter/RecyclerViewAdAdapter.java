package pes.twochange.presentation.adapter;

import android.content.Context;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;
import pes.twochange.services.ImageManager;


public class RecyclerViewAdAdapter extends RecyclerViewItemAdapter {

    private ArrayList<Ad> ads;
    private Context context;

    public RecyclerViewAdAdapter(ArrayList<Ad> ads,
                                 OnRecyclerViewItemClickListener listener,
                                 OnRecyclerViewItemLongClickListener longListener,
                                 Context context
    ) {
        super(listener, longListener);
        this.ads = ads;
        this.context = context;
    }

    @Override
    public void onBindViewHolderData(final RecyclerViewItemAdapter.ProductHolder holder, int position) {
        Ad ad = ads.get(position);
        ImageManager.getInstance().getDownloadUrl(
                "test/tom.jpg",
                new ImageManager.UrlResponse() {
                    @Override
                    public void onSuccess(String url) {
                        Picasso.with(context).load(url).into(holder.image);
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                }
        );
        holder.title.setText(ad.getTitle());
        holder.itemView.setOnLongClickListener(null);
    }

    @Override
    public int getItemCount() {
        return ads != null ? ads.size() : 0;
    }
}
