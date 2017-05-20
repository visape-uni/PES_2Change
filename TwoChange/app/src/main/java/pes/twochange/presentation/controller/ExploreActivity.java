package pes.twochange.presentation.controller;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import pes.twochange.R;
import pes.twochange.services.ImageManager;

public class ExploreActivity extends BaseActivity {

    private ImageView image1;
    private ImageView image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        toolbar.setTitle(R.string.explore);

        image1 = (ImageView) findViewById(R.id.image1);

        // interestellar.jpg
        ImageManager.getInstance().getDownloadUrl(
                "interestellar.jpg",
                new ImageManager.UrlResponse() {
                    @Override
                    public void onSuccess(String url) {
                        Picasso.with(getApplicationContext()).load(url).into(image1);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.wtf("HOTFIX", errorMessage);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );


        image2 = (ImageView) findViewById(R.id.image2);

        // test/tom.jpg
        ImageManager.getInstance().getDownloadUrl(
                "test/tom.jpg",
                new ImageManager.UrlResponse() {
                    @Override
                    public void onSuccess(String url) {
                        Picasso.with(getApplicationContext()).load(url).into(image2);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.wtf("HOTFIX", errorMessage);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    @Override
    protected int currentMenuItemIndex() {
        return EXPLORE_ACTIVITY;
    }
}
