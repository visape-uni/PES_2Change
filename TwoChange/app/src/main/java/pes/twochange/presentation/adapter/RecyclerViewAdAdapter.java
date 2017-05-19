package pes.twochange.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;


public class RecyclerViewAdAdapter extends RecyclerView.Adapter<RecyclerViewAdAdapter.ProductHolder> {

    protected ArrayList<Ad> ads;
    protected OnRecyclerViewItemClickListener listener;
    protected OnRecyclerViewItemLongClickListener longListener;

    public RecyclerViewAdAdapter(ArrayList<Ad> ads,
                                 OnRecyclerViewItemClickListener listener,
                                 OnRecyclerViewItemLongClickListener longListener
    ) {
        this.ads = ads;
        this.listener = listener;
        this.longListener = longListener;
    }

    @Override
    public RecyclerViewAdAdapter.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_row, parent);
        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdAdapter.ProductHolder holder, int position) {
        final int finalPosition = position;
        Ad ad = ads.get(finalPosition);
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

    @Override
    public int getItemCount() {
        return ads != null ? ads.size() : 0;
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView image;
        TextView title;

        ProductHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.image = (ImageView) itemView.findViewById(R.id.product_item_row_image);
            this.title = (TextView) itemView.findViewById(R.id.product_item_row_title);
        }
    }
}
