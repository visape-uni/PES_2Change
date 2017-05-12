package pes.twochange.presentation.activity;

import android.os.Bundle;

import pes.twochange.R;

public class AdActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        toolbar.setTitle("Ads");
    }

    @Override
    protected int currentMenuItemIndex() {
        return 1;
    }
}
