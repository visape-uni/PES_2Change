package pes.twochange.presentation.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.services.ImageManager;

/**
 * Created by kredes on 03/05/2017.
 */

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> implements View.OnClickListener{

    public static class AdViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "AdViewHolder";

        private TextView title, rating, description;
        private CardView card;
        private ImageView image;

        private Ad ad;

        private Context context;
        private Activity activity;
        private static final ImageManager imageManager = ImageManager.getInstance();


        public AdViewHolder(View itemView, int deviceWidth, Activity activity) {
            super(itemView);

            this.context = activity.getApplicationContext();
            this.activity = activity;

            title = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            rating = (TextView) itemView.findViewById(R.id.rating);
            image = (ImageView) itemView.findViewById(R.id.image);
            card = (CardView) itemView.findViewById(R.id.card);

            image.getLayoutParams().width = deviceWidth/2;
            image.getLayoutParams().height = deviceWidth/2;

            card.getLayoutParams().width = deviceWidth/2;
        }

        public void bindAd(final Ad ad) {
            this.ad = ad;

            title.setText(ad.getTitle());
            rating.setText(String.format("%d/100", ad.getRating()));
            description.setText(ad.getDescription());

            for (String path : ad.getImages())
                if (path != null) {
                    imageManager.putImageIntoView(path, context, image);
                    break;
                }
        }

        public Ad getAd() {
            return ad;
        }
    }

    private List<Ad> ads;
    private int deviceWidth;
    private Context context;
    private Activity activity;
    private View.OnClickListener listener;

    public AdAdapter(List<Ad> ads, int deviceWidth, Activity activity) {
        this.ads = ads;
        this.deviceWidth = deviceWidth;
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    public AdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ad, parent, false);

        itemView.setOnClickListener(this);

        return new AdViewHolder(itemView, deviceWidth, activity);
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


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }


    public void add(Ad ad) {
        ads.add(ad);
        notifyDataSetChanged();
    }

    public void update(Ad ad) {
        ads.set(ads.indexOf(ad), ad);
        notifyDataSetChanged();
    }
}
