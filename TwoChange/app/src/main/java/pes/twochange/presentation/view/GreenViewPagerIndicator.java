package pes.twochange.presentation.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import pes.twochange.R;


public class GreenViewPagerIndicator extends ViewPagerIndicator {
    public GreenViewPagerIndicator(@NonNull Context context) {
        super(context);
    }

    public GreenViewPagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GreenViewPagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs,
                                   int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getDrawable() {
        return R.drawable.green_circle;
    }
}
