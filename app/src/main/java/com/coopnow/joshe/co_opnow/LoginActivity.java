package com.coopnow.joshe.co_opnow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Author:       Joshua Esquilin
// Date:         4/30/2018
// Description:  LoginActivity handles the login functions of the app by checking for user sign in
//               and user up with the Fire Base Database.

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference fireDatabase;
    private FirebaseAuth fireAutho;

    private EditText loginEmailField;
    private EditText loginPasswordField;
    private Button loginSignInButton;
    private Button loginSignUpButton;

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fireDatabase = FirebaseDatabase.getInstance().getReference();
        fireAutho = FirebaseAuth.getInstance();

        loginEmailField = findViewById(R.id.field_email);
        loginPasswordField = findViewById(R.id.field_password);
        loginSignInButton = findViewById(R.id.button_sign_in);
        loginSignUpButton = findViewById(R.id.button_sign_up);

        loginSignInButton.setOnClickListener(this);
        loginSignUpButton.setOnClickListener(this);

        sharedPrefs = getSharedPreferences("username", Context.MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check authority status on Activity start
        if (fireAutho.getCurrentUser() != null) {
            onAuthSuccess(fireAutho.getCurrentUser());
        }
    }

    private void signIn() {
        Log.d("LoginActivity", "signIn");
        if (!validateForm()) {
            return;
        }

        String email = loginEmailField.getText().toString();
        String password = loginPasswordField.getText().toString();

        // Connects to the FireBase Database to see if the user is signed up already
        fireAutho.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("LoginActivity", "signIn:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d("LoginActivity", "signUp");
        if (!validateForm()) {
            return;
        }

        String email = loginEmailField.getText().toString();
        String password = loginPasswordField.getText().toString();

        // Connects to the FireBase Database to add the user to the FireBase Database
        fireAutho.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("LoginActivity", "createUser:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Store username in SharedPrefs
        sharedPrefsEditor.putString("username", username);
        sharedPrefsEditor.apply();

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainMenu
        startActivity(new Intent(LoginActivity.this, MainMenu.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;

        // Checks to make sure the user entered an input
        if (TextUtils.isEmpty(loginEmailField.getText().toString())) {
            loginEmailField.setError("Required");
            result = false;
        } else {
            loginEmailField.setError(null);
        }

        // Checks to make sure the user entered an input
        if (TextUtils.isEmpty(loginPasswordField.getText().toString())) {
            loginPasswordField.setError("Required");
            result = false;
        } else {
            loginPasswordField.setError(null);
        }

        return result;
    }

    // Uses the User model to craft a User in the FireBase Database
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        fireDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
            signIn();
        } else if (i == R.id.button_sign_up) {
            signUp();
        }
    }
}
