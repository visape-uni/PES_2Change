package pes.twochange.presentation.model;

import android.graphics.Bitmap;

public class ProductItem {
    private String key;
    private String title;
    private Bitmap image;

    public ProductItem(String key, String title, Bitmap image) {
        this.key = key;
        this.title = title;
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
