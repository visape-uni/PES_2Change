package pes.twochange.presentation.activity;

import android.os.Bundle;

import pes.twochange.R;

public class ExploreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        toolbar.setTitle("Explore");
    }

    @Override
    protected int currentMenuItemIndex() {
        return 0;
    }
}
