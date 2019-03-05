package com.example.socials;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CustomViewHolder>{
    private static final String TAG = "FeedAdapter";
    private Context context;
    private ArrayList<Social> data;

    FeedAdapter(Context context, ArrayList<Social> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new CustomViewHolder(view);
    }

    public void addSocial(Social social) {
        if (data == null) data = new ArrayList<>();
        data.add(0, social);

    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        final Social social = data.get(position);
        holder.titleLabel.setText(social.getName());
        String subtitle = social.getPosterName() + " • " + social.getFormattedDate();
        holder.subtitleLabel.setText(subtitle);
        holder.descriptionLabel.setText(social.getDescription());

        String likedText = "❤ " + social.getInterested().size();
        holder.likedLabel.setText(likedText);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SocialDetailActivity.class);
                intent.putExtra("social", social);
                v.getContext().startActivity(intent);
            }
        });

        final Boolean isInterested = social.getInterested().contains(FirebaseAuth.getInstance().getCurrentUser().getUid());

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInterested) {
                    social.updateInterested(false);
                } else {
                    social.updateInterested(true);
                }
            }
        });

        if (isInterested) {
            holder.likeButton.setText("LIKED!");
        } else {
            holder.likeButton.setText("LIKE");
        }

        // Glide.with(context).load(social.getPhotoLink()).into(holder.imageView);
        Log.d(TAG, "\n\n\n\n" + social.getPhotoLink());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(social.getPhotoLink());
        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).fitCenter().into(holder.imageView);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        return data.size();
    }

    public void updateSocial(Social social) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(social.getId())) {
                data.set(i, social);
            }
        }
    }

    public void removeSocial(Social social) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(social.getId())) {
                Log.d(TAG, "Removed a social!");
                data.remove(i);
                return;
            }
        }
    }

    /**
     * A card displayed in the RecyclerView
     */
    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView titleLabel;
        TextView subtitleLabel;
        TextView descriptionLabel;
        TextView likedLabel;
        Button likeButton;
        ImageView imageView;

        public CustomViewHolder (View view) {
            super(view);
            titleLabel = view.findViewById(R.id.cvTitle);
            subtitleLabel = view.findViewById(R.id.cvDetail);
            descriptionLabel = view.findViewById(R.id.cvDescription);
            likedLabel = view.findViewById(R.id.cvLikedLabel);
            likeButton = view.findViewById(R.id.cvRSVPButton);
            imageView = view.findViewById(R.id.cvImageView);
        }
    }
}
