package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Genres;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class GenreDAO {
    private static final String TAG = GenreDAO.class.getName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ArrayList<Genres> getAllDataGenre(ReadAllDataGenre readAllDataGenre){
        ArrayList<Genres> list = new ArrayList<>();
        db.collection("genres")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> map = document.getData();
                                String id = document.getId();
                                String name = (String) map.get("name");
                                Long modifyDate = (Long) map.get("modifyDate");
                                Long creationDate = (Long) map.get("creationDate");
                                String urlThumbnail = (String) map.get("urlThumbnail");
                                Genres genres = new Genres(id,name,modifyDate,creationDate,urlThumbnail);
                                list.add(genres);
                            }
                            Log.d("finish getting documents",list.size()+"");
                            readAllDataGenre.onReadAllDataGenreCallback(list);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return list;
    }

    public void getGenre(IOnProgressBarStatusListener iOnProgressBarStatusListener, String id, ReadItemGenre readItemGenre){
        iOnProgressBarStatusListener.beforeGetData();
        Genres genres = new Genres();
        DocumentReference docRef = db.collection("genres").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> map = document.getData();
                        if (map != null) {
                            String name = (String) map.get("name");
                            Long modifyDate = (Long) map.get("modifyDate");
                            Long creationDate = (Long) map.get("creationDate");
                            String urlThumbnail = (String) map.get("urlThumbnail");
                            genres.setId(id);
                            genres.setName(name);
                            genres.setModifyDate(modifyDate);
                            genres.setUrlThumbnail(urlThumbnail);
                            genres.setCreationDate(creationDate);
                            iOnProgressBarStatusListener.afterGetData();
                            if(genres.getId()!=null){
                                readItemGenre.onReadItemGenreCallback(genres);
                            }
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

    public interface ReadAllDataGenre{
        void onReadAllDataGenreCallback(ArrayList<Genres> list);
    }
    public interface ReadItemGenre{
        void onReadItemGenreCallback(Genres genres);
    }
}
