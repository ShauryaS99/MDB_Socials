package com.example.socials;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class User {
    private static final String TAG = "User";
    private String name;
    private String id;
    private String email;

    public User(String name, String id, String email) {
        this.name = name;
        this.id = id;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static void writeToFirebase(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("users").child(user.getId());
        ref.child("name").setValue(user.getName());
        ref.child("email").setValue(user.getEmail());
    }

    public static User parseFromFirebase(DataSnapshot snapshot) {
        String id = snapshot.getKey();
        String name = snapshot.child("name").getValue(String.class);
        String email = snapshot.child("email").getValue(String.class);
        return new User(name, id, email);
    }

    public static void createUser(final String email, String password, final String name, Context context, final CreateUserHandler handler) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Successfully created a new account!");
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();
                            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG,"Updated display name: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                    } else {
                                        Log.e(TAG, "Failed to update display name!");
                                    }
                                }
                            });

                            // Upload user object
                            User user = new User(name, firebaseUser.getUid(), email);
                            User.writeToFirebase(user);

                            handler.completion(null);
                        } else {
                            handler.completion(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                        }
                    }
                });
    }
}

interface CreateUserHandler {
    public void completion(String error);
}
