package pes.twochange.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.presentation.model.ProductItem;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;


public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.ProductHolder> {

    private ArrayList<ProductItem> products;
    private OnRecyclerViewItemClickListener listener;
    private OnRecyclerViewItemLongClickListener longListener;

    public RecyclerViewProductAdapter(ArrayList<ProductItem> products,
                                      OnRecyclerViewItemClickListener listener,
                                      OnRecyclerViewItemLongClickListener longListener
    ) {
        this.products = products;
        this.listener = listener;
        this.longListener = longListener;
    }

    @Override
    public RecyclerViewProductAdapter.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_row, null);
        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewProductAdapter.ProductHolder holder, int position) {
        final int finalPosition = position;
        ProductItem product = products.get(finalPosition);
//        holder.image.setImageBitmap(product.image);
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

    @Override
    public int getItemCount() {
        return products.size();
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
