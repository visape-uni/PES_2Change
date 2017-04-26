package pes.twochange.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pes.twochange.R;
import pes.twochange.domain.model.ModelAdapter;
import pes.twochange.domain.model.Profile;
import pes.twochange.services.Firebase;

public class NewProfileActivity extends AppCompatActivity {
    //Attributes
    private FirebaseAuth mAuth;
    private static final String TAG = "NewProfileActivity";

    //Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        //Next Step button + Pressed button listener
        Button nextBtn = (Button)findViewById(R.id.FinishBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                EditText userNameText = (EditText)findViewById(R.id.userNameField);
                EditText nameText = (EditText)findViewById(R.id.nameField);
                EditText surnameText = (EditText)findViewById(R.id.surnameField);
                final String userName = userNameText.getText().toString().trim();
                final String name = nameText.getText().toString().trim();
                final String surname = surnameText.getText().toString().trim();
                //Nomes obligatori posar el nom i el username
                if (name.isEmpty() || nameText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the Name field", Toast.LENGTH_LONG).show();
                } else if (surname.isEmpty() || surnameText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the Surname field", Toast.LENGTH_LONG).show();
                } else if (userName.isEmpty() || userNameText.getText() == null) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Fill in the Username field", Toast.LENGTH_LONG).show();
                } else {
                    //Comprova que no existeix un altre user amb aquest username
                    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference mFirebaseRef = mFirebaseDatabase.getReference().child("profile");
                    mFirebaseRef.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Context context = getApplicationContext();
                                Toast.makeText(context, "This Username is already used", Toast.LENGTH_LONG).show();
                            }
                            else {
                                EditText phoneText = (EditText)findViewById(R.id.phoneField);
                                EditText addressText = (EditText)findViewById(R.id.addressField);
                                EditText cityText = (EditText)findViewById(R.id.cityField);
                                EditText zipText = (EditText)findViewById(R.id.zipField);
                                EditText stateText = (EditText)findViewById(R.id.stateField);
                                EditText countryText = (EditText)findViewById(R.id.countryField);
                                Intent thisIntent = getIntent();
                                final String mail = thisIntent.getStringExtra("mail");
                                final String pass = thisIntent.getStringExtra("password");
                                final String phone = phoneText.getText().toString().trim();
                                final String addressStr = addressText.getText().toString().trim();
                                final String city = cityText.getText().toString().trim();
                                final String zip = zipText.getText().toString().trim();
                                final String state = stateText.getText().toString().trim();
                                final String country = countryText.getText().toString().trim();
                                //Add this new user & profile to Firebase
                                mAuth = FirebaseAuth.getInstance();
                                mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(NewProfileActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                        if (task.isSuccessful()) {
                                            logIn(mail, pass);
                                            //Creem la instancia de Profile a partir dels strings
                                            String uid = mAuth.getCurrentUser().getUid();
                                            Profile.Address ad = new Profile.Address(addressStr, zip, city, state, country);
                                            //Creacio del profile i pujarlo a Firebase
                                            Profile.PhoneNumber ph = new Profile.PhoneNumber(34, phone);
                                            Profile prof = new Profile(userName, uid, name, surname, ph, ad);
                                            updateProfile(prof);
                                            //Tanquem aquesta activity i anem al Main Menu
                                            Context context = getApplicationContext();
                                            Toast.makeText(context, "User successfully created", Toast.LENGTH_LONG).show();
                                            Intent mainMenu = new Intent(getApplicationContext(), MainMenuActivity.class);
                                            UserProfileChangeRequest updateProf = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
                                            mAuth.getCurrentUser().updateProfile(updateProf).
                                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {}
                                                    });
                                            startActivity(mainMenu);
                                            finish();
                                        } else if (!task.isComplete()) {
                                            Toast.makeText(NewProfileActivity.this, "Error creating user",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        });
    }

    //Crida a la creacio de profile
    private void updateProfile(final Profile prof) {
        Firebase.getInstance().insert(
            "profile",
            prof.getUsername(),
            new ModelAdapter<Profile>() {
                @Override
                public Class classType() {
                    return Profile.class;
                }

                @Override
                public Profile object() {
                    return prof;
                }
            }
        );
    }

    private void logIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(NewProfileActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                } else {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
