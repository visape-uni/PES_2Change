package pes.twochange.domain.model;

/**
 * Created by Victor on 01/05/2017.
 */

public class Product {
    String title;

    public Product() {
    }

    public Product(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
