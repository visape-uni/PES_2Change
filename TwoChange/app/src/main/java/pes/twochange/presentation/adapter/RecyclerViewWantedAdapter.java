package pes.twochange.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Product;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;

public class RecyclerViewWantedAdapter extends RecyclerView.Adapter<RecyclerViewWantedAdapter.ProductHolder> {

    protected OnRecyclerViewItemClickListener listener;
    protected OnRecyclerViewItemLongClickListener longListener;
    private ArrayList<Product> products;

    public RecyclerViewWantedAdapter(
            ArrayList<Product> products,
            OnRecyclerViewItemClickListener listener,
            OnRecyclerViewItemLongClickListener longListener
    ) {
        this.listener = listener;
        this.longListener = longListener;
        this.products = products;

    }

    @Override
    public RecyclerViewWantedAdapter.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_row,
                null);
        return new RecyclerViewWantedAdapter.ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewWantedAdapter.ProductHolder holder, int position) {
        final int finalPosition = position;
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
                        return longListener.onRecyclerViewItemLongClickListener(finalPosition);
                    }
                }
        );
        Product product = products.get(position);
        holder.title.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return (products != null) ? products.size() : 0;
    }

    public void setProductArrayList(ArrayList<Product> products) {
        this.products = products;
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        protected View itemView;
        protected TextView title;

        protected ProductHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.title = (TextView) itemView.findViewById(R.id.product_item_row_title);
        }
    }


}
