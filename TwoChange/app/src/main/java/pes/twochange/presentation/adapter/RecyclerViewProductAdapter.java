package pes.twochange.presentation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;
import pes.twochange.services.ImageManager;

public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.ProductHolder> {

    private Context context;
    private ArrayList<Ad> productArrayList;
    private OnRecyclerViewItemClickListener listener;

    public RecyclerViewProductAdapter(Context context, ArrayList<Ad> productArrayList,
                                      OnRecyclerViewItemClickListener listener) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.listener = listener;
    }

    public void setProductArrayList(ArrayList<Ad> productArrayList) {
        this.productArrayList = productArrayList;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_view,
                null);
        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, int position) {
        Ad product = productArrayList.get(position);
        String productId = product.getId();
        if (product.getImages().size() > 0) {
            String imageName = product.getImages().get(0);
            String imagePath = "ads/" + productId + "/images/" + imageName;
            Log.v("PRODUCT", "path: " + imagePath);
            ImageManager.getInstance().putImageIntoView(imagePath, context, holder.imageView);
        } else {
            holder.imageView.setImageResource(R.mipmap.placeholder);
        }
        holder.titleTextView.setText(product.getTitle());
        holder.ratingTextView.setText(String.format(Locale.FRANCE, "%d", product.getRating()));
        holder.categoryTextView.setText(product.getCategory());
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView titleTextView;
        TextView ratingTextView;
        TextView categoryTextView;
        ImageView imageView;

        public ProductHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.titleTextView = (TextView) itemView.findViewById(R.id.product_item_title);
            this.ratingTextView = (TextView) itemView.findViewById(R.id.product_item_rating);
            this.categoryTextView = (TextView) itemView.findViewById(R.id.product_item_category);
            this.imageView = (ImageView) itemView.findViewById(R.id.product_item_image);
        }
    }
}
