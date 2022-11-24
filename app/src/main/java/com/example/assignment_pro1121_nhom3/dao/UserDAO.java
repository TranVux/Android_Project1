package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignment_pro1121_nhom3.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

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
}
