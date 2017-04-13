package pes.twochange.domain.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by kredes on 07/04/2017.
 */

public class Ad extends Model {
    private Profile user;
    private String title;
    private String description;
    private String state;
    private List<Bitmap> images;
    private List<String> wants;

}
