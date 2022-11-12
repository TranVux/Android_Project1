package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Genres;
import com.example.assignment_pro1121_nhom3.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private static final String TAG = UserDAO.class.getName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addUserToFirebase(IOnProgressBarStatusListener iOnProgressBarStatusListener,User user, AddUserToFirebaseListener addUserToFirebaseListener){
        if (user != null) {
            iOnProgressBarStatusListener.beforeGetData();
            Map<String, Object> data = new HashMap<>();
            data.put("name", user.getName());
            data.put("creationDate", user.getCreationDate());
            data.put("bio", user.getBio());
            data.put("email", user.getEmail());
            data.put("playlistsID", user.getPlaylistsID());
            data.put("token", user.getToken());

            db.collection("users").document(user.getId())
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            addUserToFirebaseListener.onAddUserToFirebaseSuccessCallback();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            addUserToFirebaseListener.onAddUserToFirebaseFailureCallback(e);
                        }
                    });
            iOnProgressBarStatusListener.afterGetData();
        }
    }

    public void getUser(IOnProgressBarStatusListener iOnProgressBarStatusListener, String id, ReadItemUserListener readItemUser){
        iOnProgressBarStatusListener.beforeGetData();
        User user = new User();
        Source source = Source.DEFAULT;
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> map = document.getData();
                        if (map != null) {
                            String name = (String) map.get("name");
                            String email = (String) map.getOrDefault("email",null);
                            String token = (String) map.getOrDefault("token",null);
                            Long creationDate = (Long) map.get("creationDate");
                            ArrayList<String> playlistsID = (ArrayList<String>) map.getOrDefault("playlistsID",null);
                            String bio = (String) map.getOrDefault("bio",null);
                            user.setId(id);
                            user.setCreationDate(creationDate);
                            user.setEmail(email);
                            user.setToken(token);
                            user.setName(name);
                            user.setPlaylistsID(playlistsID);
                            user.setBio(bio);
                            iOnProgressBarStatusListener.afterGetData();
                            readItemUser.onReadItemUserCallback(user);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        });
    }
    public interface ReadItemUserListener{
        void onReadItemUserCallback(User user);
    }

    public interface AddUserToFirebaseListener{
        void onAddUserToFirebaseSuccessCallback();
        void onAddUserToFirebaseFailureCallback(Exception e);
    }
}
