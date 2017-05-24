package pes.twochange.presentation.adapter;

import android.app.Activity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> implements View.OnClickListener{

    public static class AdViewHolder extends RecyclerView.ViewHolder {

        private class AsyncImageToBitmap extends AsyncTask<Void, Void, Boolean> {
            private static final int TIMEOUT = 2500;

            private Bitmap bitmap = null;
            private boolean failure = false;
            private Activity activity;

            public AsyncImageToBitmap(Activity activity) {
                super();
                this.activity = activity;
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                // If we already downloaded the image previously
                for (Image i : ad.getImages()) {
                    if (i != null && i.getUri() != null) {
                        try {
                            this.bitmap = i.toBitmap();
                            break;
                        } catch (IOException e) {
                            failure = true;
                            Log.e(TAG, "Exception", e);
                        }
                    }
                }

                if (bitmap == null) {
                    db.child(ad.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean hasImages = false;
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.getKey().equals("images")) {
                                    hasImages = true;
                                    break;
                                }
                            }

                            if (!hasImages) return;

                            for (DataSnapshot img : dataSnapshot.child("images").getChildren()) {
                                final String filename = (String) img.getValue();
                                StorageReference storage = ad.getStorageReference();

                                final File path = new File(String.format("%s/%s", TMP_IMAGE_LOCATION.toString(), ad.getId()));

                                final File tmp;
                                final String[] things = filename.split("\\.");

                                try {
                                    path.mkdirs();
                                    tmp = File.createTempFile(things[0], "." + things[1], path);

                                    FileDownloadTask t = storage.child("images").child(filename).getFile(tmp);

                                    t.addOnSuccessListener(activity, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
                                                failure = true;
                                                Log.e(TAG, "Exception", e);
                                            }
                                        }
                                    });

                                    t.addOnFailureListener(activity, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG,
                                                    String.format("Failed to download image %s to path %s",
                                                            filename, tmp.toString()));
                                            Log.e(TAG, "Exception", e);
                                        }
                                    });
                                } catch (IOException e) {
                                    Log.e(TAG, "Exception", e);
                                }
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
/*
                    db.child(ad.getId()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            if (!dataSnapshot.getKey().equals("images")) {
                                return;
                            }

                            final String filename = (String) dataSnapshot.getValue();
                            StorageReference storage = ad.getStorageReference();

                            final File path = new File(String.format("%s/%s", TMP_IMAGE_LOCATION.toString(), ad.getId()));

                            final File tmp;
                            final String[] things = filename.split("\\.");

                            try {
                                path.mkdirs();
                                tmp = File.createTempFile(things[0], "." + things[1], path);

                                FileDownloadTask t = storage.child("images").child(filename).getFile(tmp);

                                t.addOnSuccessListener(activity, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
                                            failure = true;
                                            Log.e(TAG, "Exception", e);
                                        }
                                    }
                                });

                                t.addOnFailureListener(activity, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG,
                                                String.format("Failed to download image %s to path %s",
                                                        filename, tmp.toString()));
                                        Log.e(TAG, "Exception", e);
                                    }
                                });
                            } catch (IOException e) {
                                Log.e(TAG, "Exception", e);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });*/

                    int elapsed = 0;
                    while (bitmap == null) {
                        if (failure || elapsed >= TIMEOUT) {
                            Log.e(TAG, "Failed to load image");
                            return false;
                        }
                        try {
                            Thread.sleep(100);
                            elapsed += 100;
                            Log.i(TAG, "I am waiting.");
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Exception", e);
                        }
                    }
                }

                Log.i(TAG, "IMAGE LOADED SUCCESSFULLY!");

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result)
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
        private Activity activity;


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

            new AsyncImageToBitmap(activity).execute();
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
