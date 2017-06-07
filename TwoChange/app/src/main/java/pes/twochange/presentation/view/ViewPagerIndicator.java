package pes.twochange.presentation.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import pes.twochange.R;


public abstract class ViewPagerIndicator extends LinearLayoutCompat {
    private static final float SCALE = 1.6F;
    private static final float NO_SCALE = 1.0F;
    private static final int DEF_VALUE = 10;
    private int mPageCount;
    private int mSelectedIndex;
    private int mItemSize;
    private int mDelimiterSize;
    @NonNull
    private final List<ImageView> mIndexImages;
    @Nullable
    private ViewPager.OnPageChangeListener mListener;

    public ViewPagerIndicator(@NonNull Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mItemSize = DEF_VALUE;
        this.mDelimiterSize = DEF_VALUE;
        this.mIndexImages = new ArrayList<>();
        this.setOrientation(0);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ViewPagerIndicator, 0, 0);

        try {
            this.mItemSize = attributes
                    .getDimensionPixelSize(R.styleable.ViewPagerIndicator_itemSize, DEF_VALUE);
            this.mDelimiterSize = attributes
                    .getDimensionPixelSize(R.styleable.ViewPagerIndicator_delimiterSize, DEF_VALUE);
        } finally {
            attributes.recycle();
        }

        if(this.isInEditMode()) {
            this.createEditModeLayout();
        }

    }

    private void createEditModeLayout() {
        for(int i = 0; i < 5; ++i) {
            FrameLayout boxedItem = this.createBoxedItem(i);
            this.addView(boxedItem);
            if(i == 1) {
                View item = boxedItem.getChildAt(0);
                ViewGroup.LayoutParams layoutParams = item.getLayoutParams();
                layoutParams.height = (int)((float)layoutParams.height * SCALE);
                layoutParams.width = (int)((float)layoutParams.width * SCALE);
                item.setLayoutParams(layoutParams);
            }
        }

    }

    public void setupWithViewPager(@NonNull ViewPager viewPager) {
        this.setPageCount(viewPager.getAdapter().getCount());
        viewPager.addOnPageChangeListener(new ViewPagerIndicator.OnPageChangeListener());
    }

//    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
//        this.mListener = listener;
//    }

    private void setSelectedIndex(int selectedIndex) {
        if(selectedIndex >= 0 && selectedIndex <= this.mPageCount - 1) {
            ImageView unselectedView = this.mIndexImages.get(this.mSelectedIndex);
            unselectedView.animate().scaleX(NO_SCALE).scaleY(NO_SCALE).setDuration(300L).start();
            ImageView selectedView = this.mIndexImages.get(selectedIndex);
            selectedView.animate().scaleX(SCALE).scaleY(SCALE).setDuration(300L).start();
            this.mSelectedIndex = selectedIndex;
        }
    }

    private void setPageCount(int pageCount) {
        this.mPageCount = pageCount;
        this.mSelectedIndex = 0;
        this.removeAllViews();
        this.mIndexImages.clear();

        for(int i = 0; i < pageCount; ++i) {
            this.addView(this.createBoxedItem(i));
        }

        this.setSelectedIndex(this.mSelectedIndex);
    }

    @NonNull
    private FrameLayout createBoxedItem(int position) {
        FrameLayout box = new FrameLayout(this.getContext());
        ImageView item = this.createItem();
        box.addView(item);
        this.mIndexImages.add(item);
        LayoutParams boxParams = new LayoutParams((int) ((float) this.mItemSize * SCALE),
                (int) ((float) this.mItemSize * SCALE));
        if(position > 0) {
            boxParams.setMargins(this.mDelimiterSize, 0, 0, 0);
        }

        box.setLayoutParams(boxParams);
        return box;
    }

    @NonNull
    private ImageView createItem() {
        ImageView index = new ImageView(this.getContext());
        FrameLayout.LayoutParams indexParams = new FrameLayout.LayoutParams(this.mItemSize,
                this.mItemSize);
        indexParams.gravity = 17;
        index.setLayoutParams(indexParams);
        index.setImageResource(getDrawable());
        index.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return index;
    }

    public abstract int getDrawable();

    private class OnPageChangeListener implements ViewPager.OnPageChangeListener {
        private OnPageChangeListener() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(ViewPagerIndicator.this.mListener != null) {
                ViewPagerIndicator.this.mListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }

        }

        public void onPageSelected(int position) {
            ViewPagerIndicator.this.setSelectedIndex(position);
            if(ViewPagerIndicator.this.mListener != null) {
                ViewPagerIndicator.this.mListener.onPageSelected(position);
            }

        }

        public void onPageScrollStateChanged(int state) {
            if(ViewPagerIndicator.this.mListener != null) {
                ViewPagerIndicator.this.mListener.onPageScrollStateChanged(state);
            }

        }
    }
}
