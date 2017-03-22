package com._2change;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Victor on 22/03/2017.
 */
public class UserController {

    private static FirebaseAuth mAuth;

    public static boolean register(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password);

    }

}
