package com.example.socials;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        //if user is not logged in -> login page
        if (user == null) {
            Log.d(TAG, "User is not signed in. Go to IncorrectActivity.");
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else {
            Log.d(TAG, "User is signed in. Go to LoadingActivity.");
            Intent i = new Intent(this, FeedActivity.class);
            startActivity(i);
        }
    }
}
