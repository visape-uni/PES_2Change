package pes.twochange.domain.controller;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pes.twochange.domain.model.User;

public class UserController {

    private static UserController instance = null;
    private Context context;

    private UserController(Context context) {
        this.context = context;
    }

    private UserController() {

    }

    public static UserController getInstance(Context context) {
        if (instance == null) {
            instance = new UserController(context);
        }
        return instance;
    }

    public void login(String email, String password, final OnLogin onLogin) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    User loggedUser = new User(
                                            firebaseUser.getDisplayName(),
                                            firebaseUser.getEmail(),
                                            null
                                    );
                                    onLogin.onLoginSuccess(loggedUser);
                                } else {
                                    onLogin.onLoginFailure(task.getException().getMessage());
                                }
                            }
                        }
                );
    }

    public interface OnLogin {
        void onLoginSuccess(User user);
        void onLoginFailure(String message);
    }

    public User register(/* register parameters */) {
        return null;
    }

    public User checkSession() {
        return null;
    }



}
