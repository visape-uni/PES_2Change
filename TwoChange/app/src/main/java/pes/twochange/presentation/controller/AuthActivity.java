package pes.twochange.presentation.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import pes.twochange.R;
import pes.twochange.presentation.fragment.LoginFragment;

public class AuthActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        fragmentManager = getSupportFragmentManager();
        fragment = LoginFragment.newInstance();
        addFragment("login");
        // google stuff
    }

    private void addFragment(String tag) {
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onLoginClick(String email, String password) {

    }

    @Override
    public void onRegisterClick() {
//        fragment = RegisterUserFragment.newInstance();
        addFragment("register user");
    }

    @Override
    public void onLoginWithGoogle() {

    }
}
