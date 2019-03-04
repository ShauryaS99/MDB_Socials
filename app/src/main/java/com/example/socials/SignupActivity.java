package com.example.socials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;

    EditText nametext = findViewById(R.id.input_name);
    EditText emailtext = findViewById(R.id.input_email);
    EditText passwordtext = findViewById(R.id.input_password);
    EditText confirm_pass = findViewById(R.id.confirm_password);
    Button signup_btn = findViewById(R.id.btn_fromsignup);
    TextView logacct = findViewById(R.id.link_login);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        signup_btn.setOnClickListener(this);
        logacct.setOnClickListener(this);
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            return;
        }

        signup_btn.setEnabled(false);



        final String name = nametext.getText().toString();
        final String email = emailtext.getText().toString();
        String password = passwordtext.getText().toString();

        User.createUser(email, password, name, this, new CreateUserHandler() {
            @Override
            public void completion(String error) {
                if (error == null) {
                    // Success!
                    signup_btn.setEnabled(true);
                    startActivity(new Intent(SignupActivity.this, FeedActivity.class));
                } else {
                    // Failure! We have an error message to display.
                    signup_btn.setEnabled(true);
                    Log.d(TAG,"Account creation failed: " + error);
                    Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean validate() {
        boolean valid = true;

        String name = nametext.getText().toString();
        String email = emailtext.getText().toString();
        String password = passwordtext.getText().toString();
        String reEnterPassword = confirm_pass.getText().toString();

        if (name.isEmpty()) {
            nametext.setError("Please Enter A Name");
            valid = false;
        } else {
            nametext.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailtext.setError("Enter a valid email address");
            valid = false;
        } else {
            emailtext.setError(null);
        }


        if (password.isEmpty() || password.length() < 6 || password.length() > 30) {
            passwordtext.setError("Check password length");
            valid = false;
        } else {
            passwordtext.setError(null);
        }

        if (!(reEnterPassword.equals(password))) {
            confirm_pass.setError("Passwords don't match");
            valid = false;
        } else {
            confirm_pass.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fromsignup: signup();
            case R.id.link_login: login();
        }
    }

    private void login() {
        // Finish the registration screen and return to the Login activity
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
