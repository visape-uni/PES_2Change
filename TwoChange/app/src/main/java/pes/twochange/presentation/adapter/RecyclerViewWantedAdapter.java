package pes.twochange.presentation.adapter;

import android.view.View;

import java.util.ArrayList;

import pes.twochange.domain.model.Product;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

public class RecyclerViewWantedAdapter extends RecyclerViewItemAdapter {

    private ArrayList<Product> products;

    public RecyclerViewWantedAdapter(
            ArrayList<Product> products,
            OnRecyclerViewItemClickListener listener,
            OnRecyclerViewItemLongClickListener longListener
    ) {
        super(listener, longListener);
        this.products = products;

    }

    @Override
    public void onBindViewHolderData(RecyclerViewAdAdapter.ProductHolder holder, int position) {
        Product product = products.get(position);
        holder.image.setVisibility(View.GONE);
        holder.title.setText(product.getName());
        /*
        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(Config.GREEN_50);
        } else {
            holder.itemView.setBackgroundColor(Config.WHITE);
        }
        */
    }

    @Override
    public int getItemCount() {
        return (products != null) ? products.size() : 0;
    }


}
