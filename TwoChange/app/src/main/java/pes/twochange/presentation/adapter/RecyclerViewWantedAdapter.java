package pes.twochange.presentation.adapter;

import android.view.View;

import java.util.ArrayList;

import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

public class RecyclerViewWantedAdapter extends RecyclerViewAdAdapter {

    public RecyclerViewWantedAdapter(
            ArrayList<Ad> ads,
            OnRecyclerViewItemClickListener listener,
            OnRecyclerViewItemLongClickListener longListener
    ) {
        super(ads, listener, longListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdAdapter.ProductHolder holder, int position) {
        final int finalPosition = position;
        Ad ad = ads.get(finalPosition);
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
