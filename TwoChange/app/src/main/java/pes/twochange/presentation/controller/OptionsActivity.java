package pes.twochange.presentation.controller;

import android.os.Bundle;

import pes.twochange.R;

public class OptionsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }

    @Override
    protected int currentMenuItemIndex() {
        return getIntent().getExtras().getInt("item", 4);
    }
}
