package pes.twochange.presentation.adapter;

import android.view.View;

import java.util.ArrayList;

import pes.twochange.domain.model.Product;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

public class RecyclerViewWantedAdapter extends RecyclerViewItemAdapter {

    public RecyclerViewWantedAdapter(
            ArrayList<Product> products,
            OnRecyclerViewItemClickListener listener,
            OnRecyclerViewItemLongClickListener longListener
    ) {
        super(products, listener, longListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdAdapter.ProductHolder holder, int position) {
        final int finalPosition = position;
        Product product = (Product) items.get(finalPosition);
        holder.title.setText(product.getTitle());
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
