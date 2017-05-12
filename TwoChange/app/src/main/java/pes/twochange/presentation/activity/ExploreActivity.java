package pes.twochange.presentation.activity;

import android.os.Bundle;

import pes.twochange.R;

public class ExploreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        toolbar.setTitle("Explore");

        int fragment = getIntent().getExtras().getInt(FRAGMENT_EXTRA, ADS_LIST);


    }

    @Override
    protected int currentMenuItemIndex() {
        return 0;
    }
}
