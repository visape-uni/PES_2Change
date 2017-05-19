package pes.twochange.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;


public abstract class RecyclerViewItemAdapter extends RecyclerView.Adapter<RecyclerViewItemAdapter.ProductHolder> {

    protected ArrayList items;
    protected OnRecyclerViewItemClickListener listener;
    protected OnRecyclerViewItemLongClickListener longListener;

    public RecyclerViewItemAdapter(ArrayList items,
                                   OnRecyclerViewItemClickListener listener,
                                   OnRecyclerViewItemLongClickListener longListener
    ) {
        this.items = items;
        this.listener = listener;
        this.longListener = longListener;
    }

    @Override
    public RecyclerViewItemAdapter.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_row, parent);
        return new ProductHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
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
