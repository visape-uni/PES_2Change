package pes.twochange.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import pes.twochange.R;

public class NewProfileActivity extends AppCompatActivity {
    //Attributes
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "NewProfileActivity";

    //Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {}
        };

        //Next Step button + Pressed button listener
        Button nextBtn = (Button)findViewById(R.id.FinishBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent thisIntent = getIntent();
                String mail = thisIntent.getStringExtra("mail");
                String pass = thisIntent.getStringExtra("password");
                EditText nameText = (EditText)findViewById(R.id.nameField);
                EditText surnameText = (EditText)findViewById(R.id.surnameField);
                EditText phoneText = (EditText)findViewById(R.id.phoneField);
                EditText addressText = (EditText)findViewById(R.id.addressField);
                EditText cityText = (EditText)findViewById(R.id.cityField);
                String name = nameText.getText().toString().trim();
                String surname = surnameText.getText().toString().trim();
                String phone = phoneText.getText().toString().trim();
                String addressStr = addressText.getText().toString().trim();
                String city = cityText.getText().toString().trim();
                if (name.isEmpty() || nameText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the Name field", Toast.LENGTH_LONG).show();
                } else if (surname.isEmpty() || surnameText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the Surname field", Toast.LENGTH_LONG).show();
                } else if (phone.isEmpty() || phoneText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the Phone field", Toast.LENGTH_LONG).show();
                } else if (addressStr.isEmpty() || addressText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the Address field", Toast.LENGTH_LONG).show();
                }else if (city.isEmpty() || cityText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the City field", Toast.LENGTH_LONG).show();
                } else {
                    //Add this new user & profile to Firebase
                    //TODO: Afegir el profile
                    mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(NewProfileActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                Context context = getApplicationContext();
                                Toast.makeText(context, "User successfully created", Toast.LENGTH_LONG).show();
                                Intent newMainMenu = new Intent(context, MenuProvisionalActivity.class);
                                startActivity(newMainMenu);
                                finish();
                            } else if (!task.isComplete()) {
                                Toast.makeText(NewProfileActivity.this, "Error",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
