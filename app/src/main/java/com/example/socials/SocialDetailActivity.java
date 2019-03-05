package com.example.socials;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class SocialDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Social social;
    ImageView imageView = findViewById(R.id.edImageView);
    TextView titleLabel = findViewById(R.id.edTitleLabel);
    TextView subtitleLabel = findViewById(R.id.edSubtitleLabel);
    TextView descriptionLabel = findViewById(R.id.edDescriptionLabel);
    Button likeButton = findViewById(R.id.edLikeButton);
    TextView posterView = findViewById(R.id.edPosterLabel);
    TextView likeLabel = findViewById(R.id.edLikeLabel);

    private DatabaseReference socialRef;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_detail);

        getSupportActionBar();

        likeButton.setOnClickListener(this);

        // Get the social and set up the screen
        social = (Social) getIntent().getExtras().get("social");
        setTitle(Objects.requireNonNull(social).getName());

        // Set up the image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(social.getPhotoLink());
        Glide.with(getBaseContext()).using(new FirebaseImageLoader()).load(storageReference).into(imageView);

        updateUI();
        observeSocial();
    }

    // Start an observer to listen to changes at the social node.
    private void observeSocial() {
        socialRef = FirebaseDatabase.getInstance().getReference().child("socials").child(social.getId());
        listener = socialRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                social = Social.parseSocial(dataSnapshot);
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    // Disconnect the listener when this screen is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socialRef != null) socialRef.removeEventListener(listener);
    }

    // Most of the UI-updating methods are stored in this function
    // This function is called in onCreate as well as when the listener triggers.
    private void updateUI() {
        titleLabel.setText(social.getName());
        subtitleLabel.setText(social.getFormattedDate());
        descriptionLabel.setText(social.getDescription());
        if (social.getInterested() == null) {
            likeLabel.setText("RSVP 0");
        } else {
            String text = "RSVP " + social.getInterested().size();
            likeLabel.setText(text);
        }

        final Boolean isInterested = social.getInterested().contains(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (isInterested) {
            likeButton.setText(R.string.BTN_RSVPed);
        } else {
            likeButton.setText(R.string.BTN_RSVP);
        }

        posterView.setText(social.getPosterName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edLikeButton: likeButtonTapped();
        }
    }

    // Handles tapping of the like button
    private void likeButtonTapped() {
        final Boolean isInterested = social.getInterested().contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        if (isInterested) {
            social.updateInterested(false);
        } else {
            social.updateInterested(true);
        }
    }
}
