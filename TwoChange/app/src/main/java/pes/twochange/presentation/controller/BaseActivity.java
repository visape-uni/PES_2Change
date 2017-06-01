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
import pes.twochange.presentation.activity.LoginActivity;
import pes.twochange.presentation.activity.ProfileActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private final static int[] MENU_IDs = { R.id.explore, R.id.lists, R.id.chat, R.id.profile, R.id.settings,
            R.id.help, R.id.about, R.id.logout };

//    protected final static String FRAGMENT_EXTRA = "fragment-extra-int";
    protected final static int EXPLORE_ACTIVITY = 0;
    protected final static int AD_ACTIVITY = 1;
    protected final static int CHAT_ACTIVITY = 2;
    protected final static int PROFILE_ACTIVITY = 3;
    protected final static int SETTINGS_ACTIVITY = 4;
    protected final static int HELP_ACTIVITY = 5;
    protected final static int ABOUT_ACTIVITY = 6;

    protected Toolbar toolbar;
    protected FragmentManager fragmentManager;

    private NavigationView.OnNavigationItemSelectedListener listener =
    new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.explore:
                    startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
                    break;

                case R.id.lists:
                    startActivity(new Intent(getApplicationContext(), ListsActivity.class));
                    break;

                case R.id.chat:
//                    startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                    break;

                case R.id.profile:
                    intent = new Intent(getApplicationContext(), pes.twochange.presentation.controller.ProfileActivity.class);
                    //intent.putExtra("usernameProfile", /*TODO pasar nom del usuari que es vol veure el perfil*/);
                    startActivity(intent);
                    break;

                case R.id.settings:
                    intent = new Intent(getApplicationContext(), OptionsActivity.class);
                    intent.putExtra("item", 4);
                    startActivity(intent);
                    break;

                case R.id.help:
                    intent = new Intent(getApplicationContext(), OptionsActivity.class);
                    intent.putExtra("item", 5);
                    startActivity(intent);
                    break;

                case R.id.about:
                    intent = new Intent(getApplicationContext(), OptionsActivity.class);
                    intent.putExtra("item", 6);
                    startActivity(intent);
                    break;

                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

    protected void addFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .add(contentResId, fragment)
                .addToBackStack(tag)
                .commit();
    }

    protected void replaceFragment(int contentResId, Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(contentResId, fragment)
                .commit();
    }

    protected void displayFragment(int contentResId, Fragment fragment) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(contentResId, fragment);
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
