package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addUserToFirestore(User user) {
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", user.getName());
            data.put("creationDate", user.getCreationDate());
            data.put("email", user.getEmail());
            data.put("isDelete", user.isDelete());
            data.put("token", user.getToken());

            db.collection("users").document(user.getId())
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }
    }
    //
    // public void getUser(IOnProgressBarStatusListener
    // iOnProgressBarStatusListener, String id, ReadItemUser readItemUser){
    // iOnProgressBarStatusListener.beforeGetData();
    // User user = new User();
    // DocumentReference docRef = db.collection("users").document(id);
    // docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
    // {
    // @Override
    // public void onComplete(@NonNull Task<DocumentSnapshot> task) {
    // if (task.isSuccessful()) {
    // DocumentSnapshot document = task.getResult();
    // if (document.exists()) {
    // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
    // Map<String, Object> map = document.getData();
    // if (map != null) {
    // String name = (String) map.get("name");
    // String email = (String) map.get("email");
    // String token = (String) map.get("token");
    // Long creationDate = (Long) map.get("creationDate");
    // ArrayList<String> playlistsID = map.
    // boolean isDelete;
    // String bio;
    // iOnProgressBarStatusListener.afterGetData();
    // if(genres.getId()!=null){
    // readItemGenre.onReadItemGenreCallback(genres);
    // }
    // }
    // } else {
    // Log.d(TAG, "No such document");
    // }
    // } else {
    // Log.d(TAG, "get failed with ", task.getException());
    // }
    // }
    //
    // });
    // }
    // public interface ReadItemUser{
    // void onReadItemUserCallback(User user);
    // }
}
