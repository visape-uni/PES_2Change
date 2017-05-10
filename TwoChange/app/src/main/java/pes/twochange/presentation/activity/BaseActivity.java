package pes.twochange.presentation.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import pes.twochange.R;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int[] MENU_IDs = { R.id.home, R.id.post, R.id.chat, R.id.profile, R.id.help,
            R.id.about, R.id.logout };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.base_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openDrawer,
                R.string.closeDrawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        MenuItem currentMenuItem = navigationView.getMenu().getItem(currentMenuItemIndex());
        int colorPrimary = getResourceColor(R.color.colorPrimary);
        PorterDuff.Mode srcAtop = PorterDuff.Mode.SRC_ATOP;
        currentMenuItem.getIcon().setColorFilter(colorPrimary, srcAtop);
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        getLayoutInflater().inflate(layoutResID, frameLayout);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == MENU_IDs[currentMenuItemIndex()]) {
            return false;
        } else {
            switch (item.getItemId()) {
                case R.id.home:
                    // startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    break;

                case R.id.post:
                    // startActivity(new Intent(getApplicationContext(), AdActivity.class));
                    break;

                case R.id.chat:
                    // startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                    break;

                case R.id.profile:
                    // startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    break;

                case R.id.help:
                    // startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                    break;

                case R.id.about:
                    // startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    break;

                case R.id.logout:
                    // Logout
                    break;
            }
            return false;
        }
    }

    protected abstract int currentMenuItemIndex();

    protected int getResourceColor(int colorId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return getColor(colorId);
        } else {
            return getResources().getColor(colorId);
        }
    }


}
