package pes.twochange.presentation.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Profile;
import pes.twochange.domain.themes.ProfileTheme;
import pes.twochange.presentation.Config;

public class SearchProfileActivity extends AppCompatActivity implements TextWatcher {

    //Attributes
    EditText searchField;
    ListView profilesView;
    ArrayList<String> profilesArray;
    ArrayAdapter<String> profilesAdapter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile);

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        username = sp.getString("username", null);

        searchField = (EditText)findViewById(R.id.searchField);
        profilesView = (ListView)findViewById(R.id.profilesList);

        profilesArray = new ArrayList<>();
        profilesAdapter = new ArrayAdapter<>(SearchProfileActivity.this,
                android.R.layout.simple_list_item_1, profilesArray);
        profilesView.setAdapter(profilesAdapter);

        searchField.addTextChangedListener(this);

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
            new ProfileTheme().search(
                    newText,
                    new ProfileTheme.SearchResponse() {
                        @Override
                        public void listResponse(ArrayList<String> usernames, ArrayList<Profile> profiles) {
                            profilesAdapter = new ArrayAdapter<>(getApplicationContext(),
                                    android.R.layout.simple_list_item_1, usernames);
                            profilesView.setAdapter(profilesAdapter);
                        }

                        @Override
                        public void empty() {

                        }

                        @Override
                        public void failure(String message) {

                        }
                    }
            );
        }
    }
}
