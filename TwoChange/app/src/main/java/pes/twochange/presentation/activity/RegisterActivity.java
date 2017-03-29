package pes.twochange.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pes.twochange.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Sign Up button + Pressed button listener
        Button googleBtn = (Button)findViewById(R.id.signUpBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                EditText nameText = (EditText)findViewById(R.id.nameField);
                EditText mailText = (EditText)findViewById(R.id.mailField);
                EditText passText = (EditText)findViewById(R.id.passwordField);
                EditText repePassText = (EditText)findViewById(R.id.repeatPassField);
                String name = nameText.getText().toString().trim();
                String mail = mailText.getText().toString().trim();
                String pass = passText.getText().toString().trim();
                if (name.isEmpty() || nameText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the name field", Toast.LENGTH_LONG).show();
                } else if (mail.isEmpty() || mailText.getText() == null) {
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
                    Toast.makeText(context, "Fill in the repeated password field", Toast.LENGTH_LONG).show();
                } else {
                    Intent mainMenuIntent = new Intent (getApplicationContext(), zWorking.class);
                    startActivity(mainMenuIntent);
                }
            }
        });
    }
}
