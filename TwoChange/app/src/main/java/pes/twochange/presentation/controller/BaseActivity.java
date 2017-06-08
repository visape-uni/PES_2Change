package pes.twochange.presentation.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

import pes.twochange.R;

public abstract class BaseActivity extends AppCompatActivity {

    private final static int[] MENU_IDs = { R.id.explore, R.id.lists, R.id.chat, R.id.profile,
            R.id.searchProfile, R.id.about, R.id.logout };

    protected final static int EXPLORE_ACTIVITY = 0;
    protected final static int LISTS_ACTIVITY = 1;
    protected final static int CHAT_ACTIVITY = 2;
    protected final static int PROFILE_ACTIVITY = 3;
    protected final static int SEARCH_PROFILE_ACTIVITY = 4;
    protected final static int ABOUT_ACTIVITY = 5;

    protected Toolbar toolbar;
    protected FragmentManager fragmentManager;

    private NavigationView.OnNavigationItemSelectedListener listener =
    new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            if (MENU_IDs[currentMenuItemIndex()] == item.getItemId()) {
                return true;
            }
            switch (item.getItemId()) {
                case R.id.explore:
                    startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
                    break;

                case R.id.lists:
                    startActivity(new Intent(getApplicationContext(), ListsActivity.class));
                    break;

                case R.id.chat:
                    startActivity(new Intent(getApplicationContext(), RecyclerChatActivity.class));
                    break;

                case R.id.profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    break;

                case R.id.searchProfile:
                    startActivity(new Intent(getApplicationContext(), SearchProfileActivity.class));
                    break;

                case R.id.about:
                    startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    break;

                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(getApplicationContext(), AuthActivity.class);
                    i.putExtra("startPoint", "LOGIN");
                    startActivity(i);
                    finish();
                    break;
            }
            finish();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.base_activity);

        fragmentManager = getSupportFragmentManager();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        navigationView.setNavigationItemSelectedListener(listener);

        fragmentManager = getSupportFragmentManager();
    }

    // adds the given fragment to the front of the fragment stack
    protected void addFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .add(contentResId, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    // replaces the front fragment with the given fragment
    protected void replaceFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .replace(contentResId, fragment, tag)
                .commit();
    }

    // deletes all the fragments of the stack and displays the given one
    protected void displayFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(contentResId, fragment, tag);
    }



    @Override
    public void setContentView(int layoutResID) {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        getLayoutInflater().inflate(layoutResID, frameLayout);
    }

    protected abstract int currentMenuItemIndex();

    protected int getResourceColor(int colorId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return getColor(colorId);
        } else {
            return getResources().getColor(colorId, null);
        }
    }


}
