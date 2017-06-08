package pes.twochange.presentation.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import pes.twochange.presentation.controller.ExploreActivity;
import pes.twochange.presentation.fragment.ImageFragment;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> images;

    public ImagePagerAdapter(FragmentManager fragmentManager, ArrayList<String> images) {
        super(fragmentManager);
        this.images = images;
        Log.w(ExploreActivity.TAG, "ImagePageAdapter " + images.size());
    }

    @Override
    public int getCount() {
        return images != null ? images.size() : 0;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageFragment.newInstance(images.get(position));
    }
}
