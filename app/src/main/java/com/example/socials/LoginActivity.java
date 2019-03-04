package com.example.socials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    static final int REQUEST_ACCT = 0;  // The request code
    private FirebaseAuth mAuth;


    EditText emailtext = findViewById(R.id.input_email);
    EditText passwordtext = findViewById(R.id.input_password);
    Button loginbtn = findViewById(R.id.btn_login);
    TextView createacct = findViewById(R.id.link_signup);





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mAuth = FirebaseAuth.getInstance();

        createacct.setOnClickListener(this);
        loginbtn.setOnClickListener(this);
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            return;
        }

        //loginbtn.setEnabled(true);



        String email = emailtext.getText().toString();
        String password = passwordtext.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Signing In");
                            Intent i = new Intent(LoginActivity.this, FeedActivity.class);
                            startActivity(i);
                        } else {
                            Log.d(TAG, "Sign in failed! " + task.getException().getLocalizedMessage());
                            Toast.makeText(getBaseContext(), "Your Email or Password was incorrect.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailtext.getText().toString();
        String password = passwordtext.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailtext.setError("Enter a valid email address");
            valid = false;
        } else {
            emailtext.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 30) {
            passwordtext.setError("Password must be between 6-30 alphanumeric characters");
            valid = false;
        } else {
            passwordtext.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        // Main activity is now back of stack
        moveTaskToBack(true);
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link_signup: sign_up();
            case R.id.btn_login: login();
        }
    }

    private void sign_up() {
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivityForResult(intent, REQUEST_ACCT);
        finish();
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
