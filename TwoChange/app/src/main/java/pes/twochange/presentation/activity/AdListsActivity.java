package pes.twochange.presentation.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import pes.twochange.R;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.fragment.AdListFragment;

public class AdListsActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private String username;
    private AdListFragment fragment;
    private final String WANTED = "wanted";
    private final String OFFERED = "offered";

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        fragment = null;
        boolean toggleFragment;
        switch (item.getItemId()) {
            case R.id.navigation_wanted:
                if (currentFragmentIs(OFFERED)) {
                    fragment = AdListFragment.newInstance(username, WANTED);
                }
                break;

            case R.id.navigation_offered:if (
                    currentFragmentIs(WANTED)) {
                fragment = AdListFragment.newInstance(username, OFFERED);
            }
                break;
        }
        toggleFragment = fragment != null;
        if (toggleFragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.content, fragment);
            transaction.commit();
        }
        return toggleFragment;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_lists);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        fragment = AdListFragment.newInstance(username, WANTED);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content, fragment);
        transaction.commit();

    }

    private boolean currentFragmentIs(String name) {
        return fragment.getList().equalsIgnoreCase(name);
    }
}
