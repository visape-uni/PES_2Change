package pes.twochange.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.model.Image;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by kredes on 03/05/2017.
 */

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {

    public static class AdViewHolder extends RecyclerView.ViewHolder {

        private class AsyncImageToBitmap extends AsyncTask<Void, Void, Boolean> {
            private Bitmap bitmap = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                // If we already downloaded the image previously
                for (Image i : ad.getImages()) {
                    if (i != null && i.getUri() != null) {
                        try {
                            this.bitmap = i.toBitmap();
                        } catch (IOException e) {
                            Log.e(TAG, e.getStackTrace().toString());
                        }
                    }
                }

                db.child(ad.getId()).child("images").limitToFirst(1).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final String filename = (String) dataSnapshot.getValue();
                        StorageReference storage = ad.getStorageReference();

                        final File path = new File(String.format("%s/%s", TMP_IMAGE_LOCATION.toString(), ad.getId()));

                        final File tmp;
                        final String[] things = filename.split("\\.");

                        try {
                            path.mkdirs();
                            tmp = File.createTempFile(things[0], "." + things[1], path);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        FileDownloadTask t = storage.child("images").child(filename).getFile(tmp);

                        t.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.i(TAG,
                                        String.format("Downloaded image %s to path %s",
                                                filename, tmp.toString()));

                                String id = things[0];
                                Uri uri = Uri.fromFile(tmp);
                                Image i = new Image(getApplicationContext(), id, uri);

                                ad.addImage(i);

                                try {
                                    bitmap = i.toBitmap();
                                } catch (IOException e) {
                                    Log.e(TAG, e.getStackTrace().toString());
                                }
                            }
                        });

                        t.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG,
                                        String.format("Failed to download image %s to path %s",
                                                filename, tmp.toString()));
                                e.printStackTrace();
                            }
                        });

                        while (bitmap == null) {
                            try {
                                Thread.sleep(100);
                                Log.i(TAG, "I am waiting.");
                            } catch (InterruptedException e) {
                                Log.e(TAG, e.getStackTrace().toString());
                            }
                        }

                        Log.i(TAG, "IMAGE LOADED ASYNCHRONOUSLY!");
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (bitmap != null)
                    image.setImageBitmap(bitmap);
            }


        }






        private static final File TMP_IMAGE_LOCATION =
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/2change/tmp");;
        private static DatabaseReference db =
                FirebaseDatabase.getInstance().getReferenceFromUrl("https://change-64bd0.firebaseio.com/").child("ads");
        private static final String TAG = "AdViewHolder";

        private TextView title, rating, description;
        private CardView card;
        private ImageView image;

        private Ad ad;

        private Context context;


        public AdViewHolder(View itemView, int deviceWidth, Context context) {
            super(itemView);

            this.context = context.getApplicationContext();

            title = (TextView) itemView.findViewById(R.id.title);
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

            new AsyncImageToBitmap().execute();
        }
    }

    private List<Ad> ads;
    private int deviceWidth;
    private Context context;

    public AdAdapter(List<Ad> ads, int deviceWidth, Context context) {
        this.ads = ads;
        this.deviceWidth = deviceWidth;
        this.context = context.getApplicationContext();
    }

    @Override
    public AdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ad, parent, false);

        return new AdViewHolder(itemView, deviceWidth, context);
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
        notifyDataSetChanged();
    }
}
