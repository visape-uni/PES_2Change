package pes.twochange;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Login button + Pressed button listener
        Button loginBtn = (Button) findViewById(R.id.logInBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                EditText user = (EditText)findViewById(R.id.mailField);
                EditText pass = (EditText)findViewById(R.id.passwordField);
                if (user.getText().toString().trim().equals("") || user.getText() == null) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Fill in the Email field", Toast.LENGTH_LONG);
                    toast.show();
                } else if (!user.getText().toString().contains("@")) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Incorrect Email format", Toast.LENGTH_LONG);
                    toast.show();
                } else if (pass.getText().toString().trim().equals("") || pass.getText() == null) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Fill in the password field", Toast.LENGTH_LONG);
                    toast.show();
                } else if (/*logIn(user, pass)*/ true) {
                    Intent mainMenuIntent = new Intent (getApplicationContext(), zWorking.class);
                    startActivity(mainMenuIntent);
                } else {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Wrong user or password", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        //Google button + Pressed button listener
        Button googleBtn = (Button)findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent mainMenuIntent = new Intent (getApplicationContext(), zWorking.class);
                startActivity(mainMenuIntent);
            }
        });

        //New user button + Pressed button listener
        Button newUserBtn = (Button)findViewById(R.id.newUserBtn);
        newUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent mainMenuIntent = new Intent (getApplicationContext(), NewUser.class);
                startActivity(mainMenuIntent);
            }
        });
    }
}
