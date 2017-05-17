package pes.twochange.domain.model;

/**
 * Created by Victor on 01/05/2017.
 */

public class Product {
    String title;
    String key;

    public Product() {
    }

    public Product(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
