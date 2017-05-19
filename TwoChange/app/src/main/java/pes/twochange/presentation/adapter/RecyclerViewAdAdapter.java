package pes.twochange.presentation.adapter;

import android.view.View;

import java.util.ArrayList;

import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;


public class RecyclerViewAdAdapter extends RecyclerViewItemAdapter {

    protected OnRecyclerViewItemClickListener listener;
    protected OnRecyclerViewItemLongClickListener longListener;

    public RecyclerViewAdAdapter(ArrayList<Ad> ads,
                                 OnRecyclerViewItemClickListener listener,
                                 OnRecyclerViewItemLongClickListener longListener
    ) {
        super(ads, listener, longListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewItemAdapter.ProductHolder holder, int position) {
        final int finalPosition = position;
        Ad ad = (Ad) items.get(finalPosition);
            // TODO load image
//        holder.image.setImageBitmap(product.getImage());
        holder.title.setText(ad.getTitle());
        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRecyclerViewItemClickListener(finalPosition);
                    }
                }
        );
        holder.itemView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longListener.onRecyclerViewItemLongClickListener(finalPosition);
                        return true;
                    }
                }
        );
    }
}
