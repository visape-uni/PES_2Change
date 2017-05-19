package pes.twochange.presentation.adapter;

import android.view.View;

import java.util.ArrayList;

import pes.twochange.presentation.model.ProductItem;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

public class RecyclerViewWantedProductAdapter extends RecyclerViewProductAdapter {

    public RecyclerViewWantedProductAdapter(
            ArrayList<ProductItem> products,
            OnRecyclerViewItemClickListener listener,
            OnRecyclerViewItemLongClickListener longListener
    ) {
        super(products, listener, longListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewProductAdapter.ProductHolder holder, int position) {
        final int finalPosition = position;
        ProductItem product = products.get(finalPosition);
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
