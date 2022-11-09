package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment_pro1121_nhom3.models.Music;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Map;

public class MusicDAO {
    private static final String TAG = MusicDAO.class.getName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public ArrayList<Music> getAllData(){
        ArrayList<Music> list = new ArrayList<>();
        db.collection("musics")
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
                                String url = (String) map.get("url");
                                String thumbnailUrl = (String) map.get("thumbnailUrl");
                                Long creationDate = (Long) map.get("creationDate");
                                if(creationDate == null){
                                    creationDate = 0L;
                                }
                                Long modifyDate = (Long) map.get("modifyDate");
                                if(modifyDate == null){
                                    modifyDate = 0L;
                                }
                                String singerName = (String) map.get("singerName");
                                String singerId = (String) map.get("singerId");
                                Long views = (Long) map.get("views");
                                if(views == null){
                                    views = 0L;
                                }
                                String genresId = (String) map.get("genresId");
                                Music music = new Music(id,name,url,thumbnailUrl,creationDate,modifyDate,singerName,singerId,views,genresId);
                                list.add(music);
                            }
                            Log.d("finish getting documents",list.size()+"");

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return list;
    }
//    public Music getMusic(String id){
//        final Music[] music = new Music[1];
//        db.collection("musics").document(id)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Map<String,Object> map = document.getData();
//                                String id = document.getId();
//                                String name = (String) map.get("name");
//                                String url = (String) map.get("url");
//                                String thumbnailUrl = (String) map.get("thumbnailUrl");
//                                //creationDate,modifyDate k có trong db
//                                Long creationDate = (Long) map.get("creationDate");
//                                if(creationDate == null){
//                                    creationDate = 0L;
//                                }
//                                Long modifyDate = (Long) map.get("modifyDate");
//                                if(modifyDate == null){
//                                    modifyDate = 0L;
//                                }
//                                String singerName = (String) map.get("singerName");
//                                String singerId = (String) map.get("singerId");
//                                Long views = (Long) map.get("views");
//                                if(views == null){
//                                    views = 0L;
//                                }
//                                String genresId = (String) map.get("genresId");
//                                music[0] = new Music(id,name,url,thumbnailUrl,creationDate,modifyDate,singerName,singerId,views,genresId);
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//        Music music1 = music[0];
//        return music1;
//
//    }

}
