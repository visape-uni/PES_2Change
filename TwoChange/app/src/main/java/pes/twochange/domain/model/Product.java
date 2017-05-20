package pes.twochange.domain.model;

/**
 * Created by Victor on 01/05/2017.
 */

public class Product {
    String name;
    String key;

    public Product() {
    }

    public Product(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
