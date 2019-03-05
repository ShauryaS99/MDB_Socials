package com.example.socials;

import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static java.lang.Math.toIntExact;

public class Social implements Parcelable, Comparable {
    private String id;
    private String name;
    private String description;
    private Date date;
    private String posterName;
    private String posterId;
    private ArrayList<String> interested;

    public Social(String id, String name, String description, Date date, String posterName, String posterId, ArrayList<String> interested) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.posterName = posterName;
        this.posterId = posterId;
        this.interested = interested;
    }

    protected Social(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        posterName = in.readString();
        posterId = in.readString();
        interested = in.createStringArrayList();
        date = new Date(in.readLong());
    }

    public static final Creator<Social> CREATOR = new Creator<Social>() {
        @Override
        public Social createFromParcel(Parcel in) {
            return new Social(in);
        }

        @Override
        public Social[] newArray(int size) {
            return new Social[size];
        }
    };

    public static void writeSocial(final Social social, Uri image, final DatabaseReference pushRef, final CreateSocialHandler handler) {
        // Get the reference to the image object in Firebase Storage
//        StorageReference mstorageRef;
//        mstorageRef = FirebaseStorage.getInstance().getReference();
//        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
//        StorageReference riversRef = mstorageRef.child("images/rivers.jpg");

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-8ccd1.appspot.com/");
        StorageReference imageRef = storageRef.child(pushRef.getKey() + ".png");

        // Upload the image object
        imageRef.putFile(image).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completion(e.getLocalizedMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upon uploading the image object, push the event data to the server
                Social.uploadSocial(social, pushRef);
                handler.completion(null);
            }
        });

    }

    public static Social parseSocial(DataSnapshot snapshot) {
        String id = snapshot.getKey();
        String name = snapshot.child("name").getValue(String.class);
        String photoLink = snapshot.child("photoLink").getValue(String.class);
        String description = snapshot.child("description").getValue(String.class);
        Long longValue = snapshot.child("date").getValue(Long.class);
        if (longValue == null) return null;
        Date date = new Date(longValue);
        String posterName = snapshot.child("posterName").getValue(String.class);
        String posterId = snapshot.child("posterId").getValue(String.class);
        ArrayList<String> interested = new ArrayList<>();
        for (DataSnapshot child : snapshot.child("interested").getChildren()) {
            interested.add(child.getKey());
        }
        return new Social(id, name, description, date, posterName, posterId, interested);
    }

    public void updateInterested(Boolean b) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        if (!b) b = null;
        ref.child("socials").child(id).child("interested").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(b);
    }

    public static DatabaseReference getPushRef() {
        return FirebaseDatabase.getInstance().getReference().child("socials").push();
    }

    public static void uploadSocial(Social social, DatabaseReference ref) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", social.name);
        map.put("description", social.description);
        map.put("date", social.date.getTime());
        map.put("posterName", social.posterName);
        map.put("posterId", social.posterId);

        if (social.interested != null) {
            HashMap<String, Boolean> interested = new HashMap<>();
            for (String interestedUserId : social.interested) {
                interested.put(interestedUserId, true);
            }
            map.put("interested", interested);
        }

        ref.setValue(map);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoLink() {
        return id + ".png";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public String getFormattedDate() {
        String myFormat = "E, MMM d, yyyy @ h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public ArrayList<String> getInterested() {
        return interested;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeLong(date.getTime());
        dest.writeString(description);
        dest.writeString(posterName);
        dest.writeString(posterId);
        dest.writeStringList(interested);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return toIntExact (((Social) o).getDate().getTime() - getDate().getTime());
        } else {
            return -1;
        }

    }
}

interface CreateSocialHandler {
    public void completion(String error);
}

