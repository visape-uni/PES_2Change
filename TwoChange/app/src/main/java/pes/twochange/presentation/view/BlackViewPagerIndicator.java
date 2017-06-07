package pes.twochange.presentation.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import pes.twochange.R;


public class BlackViewPagerIndicator extends ViewPagerIndicator {
    public BlackViewPagerIndicator(@NonNull Context context) {
        super(context);
    }

    public BlackViewPagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BlackViewPagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs,
                                   int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getDrawable() {
        return R.drawable.black_circle;
    }
}
