package com.example.socials;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";
    RecyclerView recyclerView = findViewById(R.id.feedRecyclerView);

    private DatabaseReference postsRef;

    private FeedAdapter adapter;

    MenuItem newPostButton;
    MenuItem logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        getSupportActionBar();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FeedAdapter(FeedActivity.this, null);
        recyclerView.setAdapter(adapter);

        postsRef = FirebaseDatabase.getInstance().getReference().child("socials");

        postsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Social social = Social.parseSocial(dataSnapshot);
                if (social == null) {
                    Log.d(TAG, "Error parsing social!");
                } else {
                    adapter.addSocial(social);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Social social = Social.parseSocial(dataSnapshot);
                if (social == null) {
                    Log.d(TAG, "Error parsing social!");
                } else {
                    adapter.updateSocial(social);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Social social = Social.parseSocial(dataSnapshot);
                if (social == null) {
                    Log.d(TAG, "Error parsing removed social!");
                } else {
                    adapter.removeSocial(social);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    // Adds the done button to the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        newPostButton = menu.add(Menu.NONE, 1000, Menu.NONE, "Create");
        newPostButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        logoutButton = menu.add(Menu.NONE, 1000, Menu.NONE, "Log Out");
        logoutButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    // Called when done button is tapped
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == newPostButton) {
            newPostTapped();
        } else if (item == logoutButton) {
            logoutTapped();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutTapped() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(FeedActivity.this, LoginActivity.class));
    }

    private void newPostTapped() {
        startActivity(new Intent(FeedActivity.this, CreateSocialActivity.class));
    }
}
