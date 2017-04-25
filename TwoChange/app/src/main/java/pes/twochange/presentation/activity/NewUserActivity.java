package pes.twochange.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import pes.twochange.R;

public class NewUserActivity extends AppCompatActivity {
    //Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        //Next Step button + Pressed button listener
        Button nextBtn = (Button)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                EditText mailText = (EditText)findViewById(R.id.mailField);
                EditText passText = (EditText)findViewById(R.id.passField);
                EditText repePassText = (EditText)findViewById(R.id.repeatPassField);
                String mail = mailText.getText().toString().trim();
                String pass = passText.getText().toString().trim();
                if (mail.isEmpty() || mailText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the Email field", Toast.LENGTH_LONG).show();
                } else if (!mail.contains("@")) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Incorrect Email format", Toast.LENGTH_LONG).show();
                } else if (pass.isEmpty() || passText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the password field", Toast.LENGTH_LONG).show();
                } else if (pass.isEmpty() || repePassText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the repeated password field", Toast.LENGTH_LONG).show();
                } else if (!(repePassText.getText().toString().equals(passText.getText().toString()))) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Both passwords must be equal", Toast.LENGTH_LONG).show();
                } else {
                    Intent newProfile = new Intent(getApplicationContext(), EditProfileActivity.class);
                    newProfile.putExtra("mail", mail);
                    newProfile.putExtra("password", pass);
                    startActivity(newProfile);
                    finish();
                }
            }
        });

        //Matches both passwords in real time and changes foreground color
        final EditText repePassText = (EditText)findViewById(R.id.repeatPassField);
        repePassText.addTextChangedListener((new TextWatcher(){
            EditText passText = (EditText)findViewById(R.id.passField);
            public void afterTextChanged(Editable s) {
                if (!(repePassText.getText().toString().equals(passText.getText().toString()))) {
                    repePassText.setTextColor(Color.RED);
                } else {
                    repePassText.setTextColor(Color.BLACK);
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        }));
    }
}
