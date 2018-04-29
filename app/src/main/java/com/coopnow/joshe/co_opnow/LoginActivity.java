package com.coopnow.joshe.co_opnow;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference fireDatabase;
    private FirebaseAuth fireAutho;

    private EditText loginEmailField;
    private EditText loginPasswordField;
    private Button loginSignInButton;
    private Button loginSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fireDatabase = FirebaseDatabase.getInstance().getReference();
        fireAutho = FirebaseAuth.getInstance();

        // Views
        loginEmailField = findViewById(R.id.field_email);
        loginPasswordField = findViewById(R.id.field_password);
        loginSignInButton = findViewById(R.id.button_sign_in);
        loginSignUpButton = findViewById(R.id.button_sign_up);

        // Click listeners
        loginSignInButton.setOnClickListener(this);
        loginSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
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
        if (TextUtils.isEmpty(loginEmailField.getText().toString())) {
            loginEmailField.setError("Required");
            result = false;
        } else {
            loginEmailField.setError(null);
        }

        if (TextUtils.isEmpty(loginPasswordField.getText().toString())) {
            loginPasswordField.setError("Required");
            result = false;
        } else {
            loginPasswordField.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        fireDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

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
