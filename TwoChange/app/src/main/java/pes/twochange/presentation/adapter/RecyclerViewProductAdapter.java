package pes.twochange.presentation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
        String imageId = product.getImageAt(0).getId();
        String extension = product.getImageAt(0).getFormat().getExtension();
        String imagePath = "ads/" + productId + "/" + imageId + extension;
        ImageManager.getInstance().putImageIntoView(imagePath, context, holder.imageView);
        holder.titleTextView.setText(product.getTitle());
        holder.ratingTextView.setText(String.format(Locale.FRANCE, "%d", product.getRating()));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView titleTextView;
        TextView ratingTextView;
        ImageView imageView;

        public ProductHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
//            this.titleTextView = (TextView) itemView.findViewById(R.id.product_item_title);
//            this.ratingTextView = (TextView) itemView.findViewById(R.id.product_item_rating);
//            this.imageView = (ImageView) itemView.findViewById(R.id.product_item_image);
        }
    }
}
