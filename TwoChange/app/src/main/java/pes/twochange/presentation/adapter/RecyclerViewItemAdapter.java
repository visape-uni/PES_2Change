package pes.twochange.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import pes.twochange.R;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.presentation.view.OnRecyclerViewItemLongClickListener;


public abstract class RecyclerViewItemAdapter extends RecyclerView.Adapter<RecyclerViewItemAdapter.ProductHolder> {

    protected OnRecyclerViewItemClickListener listener;
    protected OnRecyclerViewItemLongClickListener longListener;

    public RecyclerViewItemAdapter(OnRecyclerViewItemClickListener listener,
                                   OnRecyclerViewItemLongClickListener longListener
    ) {
        this.listener = listener;
        this.longListener = longListener;
    }

    @Override
    public RecyclerViewItemAdapter.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_row, null);
        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdAdapter.ProductHolder holder, int position) {
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
        onBindViewHolderData(holder, position);
    }

    public abstract void onBindViewHolderData(RecyclerViewAdAdapter.ProductHolder holder, int position);

    protected class ProductHolder extends RecyclerView.ViewHolder {

        protected View itemView;
        protected ImageView image;
        protected TextView title;

        protected ProductHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.image = (ImageView) itemView.findViewById(R.id.product_item_row_image);
            this.title = (TextView) itemView.findViewById(R.id.product_item_row_title);
        }
    }
}
