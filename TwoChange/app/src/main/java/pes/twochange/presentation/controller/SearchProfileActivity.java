package pes.twochange.presentation.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.adapter.ProfileAdapter;

public class SearchProfileActivity extends BaseActivity implements TextWatcher, AdapterView.OnItemClickListener{

    private EditText searchField;
    private ListView profilesView;

    private ArrayList<Profile> profiles;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        username = sp.getString("username", null);

        searchField = (EditText)findViewById(R.id.searchField);
        profilesView = (ListView)findViewById(R.id.profilesList);

        profiles = new ArrayList<>();

        searchField.addTextChangedListener(this);
        profilesView.setOnItemClickListener(this);
    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String newText = s.toString();
        if (newText.length() > 2) {
            new ProfileTheme().search(newText, new ProfileTheme.SearchResponse() {
                @Override
                public void listResponse(ArrayList<String> usernames, ArrayList<Profile> profilesArray) {
                    profiles = profilesArray;
                    setList();
                }

                @Override
                public void empty() {

                }

                @Override
                public void failure(String message) {

                }
            });
        }
    }

    private void setList() {

        ProfileAdapter profileAdapter = new ProfileAdapter(this, R.layout.profile, profiles);
        profilesView.setAdapter(profileAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedUsername = profiles.get(position).getUsername();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        if (!selectedUsername.equals(username)) {
            intent.putExtra("usernameProfile", selectedUsername);
        }
        startActivity(intent);
        finish();
    }


        @Override
    protected int currentMenuItemIndex() {
        return SEARCH_PROFILE_ACTIVITY;
    }
}
