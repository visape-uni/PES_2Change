package pes.twochange.presentation.model;

import android.graphics.Bitmap;

import pes.twochange.domain.model.Product;

public class ProductItem extends Product {
    private Bitmap image;

    public ProductItem(String key, String title, Bitmap image) {
        super(key, title);
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }
}
