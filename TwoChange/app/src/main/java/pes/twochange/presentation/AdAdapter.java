package pes.twochange.presentation;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Image;

/**
 * Created by kredes on 03/05/2017.
 */

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        private TextView title, rating, description;
        private CardView card;
        private ImageView image;

        public AdViewHolder(View itemView, int deviceWidth) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            rating = (TextView) itemView.findViewById(R.id.rating);
            image = (ImageView) itemView.findViewById(R.id.image);
            card = (CardView) itemView.findViewById(R.id.card);

            image.getLayoutParams().width = deviceWidth/2;
            image.getLayoutParams().height = deviceWidth/2;

            card.getLayoutParams().width = deviceWidth/2;
        }

        public void bindAd(Ad ad) {
            title.setText(ad.getTitle());
            rating.setText(String.format("%d/100", ad.getRating()));
            description.setText(ad.getDescription());

            for (Image i : ad.getImages()) {
                if (i != null) {
                    try {
                        image.setImageBitmap(i.toBitmap());
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private List<Ad> ads;
    private int deviceWidth;

    public AdAdapter(List<Ad> ads, int deviceWidth) {
        this.ads = ads;
        this.deviceWidth = deviceWidth;
    }

    @Override
    public AdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ad, parent, false);

        return new AdViewHolder(itemView, deviceWidth);
    }


    @Override
    public void onBindViewHolder(AdViewHolder holder, int position) {
        Ad ad = ads.get(position);
        holder.bindAd(ad);
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public void add(Ad ad) {
        ads.add(ad);
    }
}
