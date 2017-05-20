package pes.twochange.presentation.adapter;

import java.util.ArrayList;

import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;


public class RecyclerViewAdAdapter extends RecyclerViewItemAdapter {

    private ArrayList<Ad> ads;

    public RecyclerViewAdAdapter(ArrayList<Ad> ads,
                                 OnRecyclerViewItemClickListener listener,
                                 OnRecyclerViewItemLongClickListener longListener
    ) {
        super(listener, longListener);
        this.ads = ads;
    }

    @Override
    public void onBindViewHolderData(RecyclerViewItemAdapter.ProductHolder holder, int position) {
        Ad ad = ads.get(position);
//        TODO load image
//        holder.image.setImageBitmap(product.getImage());
        holder.title.setText(ad.getTitle());
        holder.itemView.setOnLongClickListener(null);
    }

    @Override
    public int getItemCount() {
        return ads != null ? ads.size() : 0;
    }
}
