package pes.twochange.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import pes.twochange.R;

public class NewUserActivity extends AppCompatActivity {
    //Attributes
    private FirebaseAuth mAuth;
    private static final String TAG = "NewProfileActivity";

    //Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);


        //Next Step button + Pressed button listener
        Button nextBtn = (Button)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Context context = getApplicationContext();
                final EditText mailText = (EditText)findViewById(R.id.mailField);
                final EditText passText = (EditText)findViewById(R.id.passField);
                final EditText repePassText = (EditText)findViewById(R.id.repeatPassField);
                final String mail = mailText.getText().toString().trim();
                final String pass = passText.getText().toString().trim();
                String repePass = repePassText.getText().toString().trim();
                if (mail.isEmpty() || mailText.getText() == null) {
                    Toast.makeText(context, "Fill in the Email field", Toast.LENGTH_LONG).show();
                    mailText.requestFocus();
                } else if (pass.isEmpty() || passText.getText() == null) {
                    Toast.makeText(context, "Fill in the password field", Toast.LENGTH_LONG).show();
                    passText.requestFocus();
                } else if (repePass.isEmpty() || repePassText.getText() == null) {
                    Toast.makeText(context, "Fill in the repeated password field", Toast.LENGTH_LONG).show();
                    repePassText.requestFocus();
                } else if (!(repePassText.getText().toString().equals(passText.getText().toString()))) {
                    Toast.makeText(context, "Both passwords must be equal", Toast.LENGTH_LONG).show();
                    repePassText.setText("");
                    repePassText.requestFocus();
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(NewUserActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                Intent newProfile = new Intent(getApplicationContext(), NewProfileActivity.class);
                                newProfile.putExtra("mail", mail);
                                newProfile.putExtra("password", pass);
                                startActivity(newProfile);
                                finish();
                            } else {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    //TODO Fix: Quan hauria de saltar aquesta excepcio, salta la Unknown
                                    Toast.makeText(NewUserActivity.this, "Error: Password must be larger", Toast.LENGTH_LONG).show();
                                    passText.setText("");
                                    passText.requestFocus();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(NewUserActivity.this, "Error: Invalid Email Format", Toast.LENGTH_LONG).show();
                                    mailText.setText("");
                                    mailText.requestFocus();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(NewUserActivity.this, "Error: This Email is Already Registered", Toast.LENGTH_LONG).show();
                                    mailText.setText("");
                                    mailText.requestFocus();
                                } catch(Exception e) {
                                    Toast.makeText(NewUserActivity.this, "Unknown Error: Check your credentials", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
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
