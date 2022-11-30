package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private static final String TAG = UserDAO.class.getName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void checkUserAlreadyHaveOnFirebase(String uid, IsAlreadyLoginOnFirebase isAlreadyLoginOnFirebase) {
        db.collection("users").document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        isAlreadyLoginOnFirebase.onAlreadyLoginResult(documentSnapshot.exists());
                    }
                });
    }

    public void addUserToFirestore(User user) {
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", user.getName());
            data.put("creationDate", user.getCreationDate());
            data.put("email", user.getEmail());
            data.put("isDelete", user.isDelete());
            data.put("token", user.getToken());
            data.put("playlistsID", user.getPlaylistsID());
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

    public void getPlaylist(String userId, IOnProgressBarStatusListener iOnProgressBarStatusListener, GetPlayList getPlayList) {
        iOnProgressBarStatusListener.beforeGetData();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    iOnProgressBarStatusListener.afterGetData();
                    getPlayList.onGetPlaylistSuccess((ArrayList<String>) snapshot.get("playlistsID"));
                } else {
                    Log.d(TAG, "onComplete: Không có dữ liệu");
                    iOnProgressBarStatusListener.afterGetData();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iOnProgressBarStatusListener.afterGetData();
                e.printStackTrace();
            }
        });
    }

    public void addPlaylistForUser(String userID, String playlistID, IOnProgressBarStatusListener iOnProgressBarStatusListener, AddPlaylist playlist) {
        iOnProgressBarStatusListener.beforeGetData();
        db.collection("users").document(userID).update("playlistsID", FieldValue.arrayUnion(playlistID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        iOnProgressBarStatusListener.afterGetData();
                        playlist.onAddPlaylistSuccess();
                        Log.d(TAG, "onComplete: thêm playlist thành công");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iOnProgressBarStatusListener.afterGetData();
                        playlist.onAddPlaylistFailure();
                    }
                });
    }

    public interface AddPlaylist {
        void onAddPlaylistSuccess();

        void onAddPlaylistFailure();
    }

    public interface GetPlayList {
        void onGetPlaylistSuccess(ArrayList<String> result);

        void onGetPlaylistFailure();
    }

    public interface IsAlreadyLoginOnFirebase {
        void onAlreadyLoginResult(boolean result);
    }
}
