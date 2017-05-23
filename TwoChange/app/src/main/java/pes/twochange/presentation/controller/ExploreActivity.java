package pes.twochange.presentation.controller;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image1.setDrawingCacheEnabled(true);
                        image1.buildDrawingCache();
                        Bitmap bitmap = image1.getDrawingCache();

                        String path = "test/" + System.currentTimeMillis() + ".jpg";

                        ImageManager.getInstance().storeImage(
                                path,
                                bitmap,
                                new ImageManager.UploadResponse() {
                                    @Override
                                    public void onSuccess(@Nullable String url) {
                                        Toast.makeText(ExploreActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Toast.makeText(ExploreActivity.this, "FAILURE: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }
        );

    }

    @Override
    protected int currentMenuItemIndex() {
        return EXPLORE_ACTIVITY;
    }
}
