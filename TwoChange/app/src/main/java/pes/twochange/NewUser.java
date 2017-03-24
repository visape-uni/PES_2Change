package pes.twochange;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NewUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        //Sign Up button + Pressed button listener
        Button googleBtn = (Button)findViewById(R.id.signUpBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent mainMenuIntent = new Intent (getApplicationContext(), zWorking.class);
                startActivity(mainMenuIntent);
            }
        });
    }
}
