package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query.Direction;

import androidx.annotation.NonNull;

import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MusicDAO {
    private static final String TAG = MusicDAO.class.getName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //lấy toàn bộ bài hát
    public void getAllDataMusic(IOnProgressBarStatusListener iOnProgressBarStatusListener,ReadAllDataMusic readAllDataMusic) {
        iOnProgressBarStatusListener.beforeGetData();
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
                                if (creationDate == null) {
                                    creationDate = 0L;
                                }
                                Long updateDate = (Long) map.get("modifyDate");
                                if (updateDate == null) {
                                    updateDate = 0L;
                                }
                                String singerName = (String) map.get("singerName");
                                String singerId = (String) map.get("singerID");
                                Long views = (Long) map.get("views");
                                if (views == null) {
                                    views = 0L;
                                }
                                String genresId = (String) map.get("genresID");
                                Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate, singerName, singerId, views, genresId);
                                list.add(music);
                            }
                            Log.d("finish getting documents", list.size() + "");
                            iOnProgressBarStatusListener.afterGetData();
                            readAllDataMusic.onReadAllDataMusicCallback(list);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getInitMusicItem(IOnProgressBarStatusListener iOnProgressBarStatusListener, int limitCount, GetInitDataMusic getInitDataMusic) {
        ArrayList<Music> list = new ArrayList<>();
        iOnProgressBarStatusListener.beforeGetData();
        db.collection("musics")
                .limit(limitCount)
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
                                if (creationDate == null) {
                                    creationDate = 0L;
                                }
                                Long updateDate = (Long) map.get("modifyDate");
                                if (updateDate == null) {
                                    updateDate = 0L;
                                }
                                String singerName = (String) map.get("singerName");
                                String singerId = (String) map.get("singerID");
                                Long views = (Long) map.get("views");
                                if (views == null) {
                                    views = 0L;
                                }
                                String genresId = (String) map.get("genresID");
                                Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate, singerName, singerId, views, genresId);
                                list.add(music);
                            }
                            Log.d("finish getting documents", list.size() + "");
                            getInitDataMusic.onGetInitData(list);
                            iOnProgressBarStatusListener.afterGetData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //lấy  1 bài hát
    public void getMusic(IOnProgressBarStatusListener iOnProgressBarStatusListener, String id, ReadItemMusic readItemMusic) {
        iOnProgressBarStatusListener.beforeGetData();
        Music music = new Music();
        DocumentReference docRef = db.collection("musics").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> map = document.getData();
                        if (map != null) {
                            String id = document.getId();
                            String name = (String) map.get("name");
                            String url = (String) map.get("url");
                            String thumbnailUrl = (String) map.get("thumbnailUrl");
                            Long creationDate = (Long) map.get("creationDate");
                            if (creationDate == null) {
                                creationDate = 0L;
                            }
                            Long updateDate = (Long) map.get("updateDate");
                            if (updateDate == null) {
                                updateDate = 0L;
                            }
                            String singerName = (String) map.get("singerName");
                            String singerId = (String) map.get("singerID");
                            Long views = (Long) map.get("views");
                            if (views == null) {
                                views = 0L;
                            }
                            String genresId = (String) map.get("genresID");
                            music.setId(id);
                            music.setName(name);
                            music.setUrl(url);
                            music.setThumbnailUrl(thumbnailUrl);
                            music.setCreationDate(creationDate);
                            music.setUpdateDate(updateDate);
                            music.setSingerName(singerName);
                            music.setSingerId(singerId);
                            music.setViews(views);
                            music.setGenresId(genresId);
                            iOnProgressBarStatusListener.afterGetData();
                            readItemMusic.onReadItemMusicCallback(music);
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

    public void getTopMusic10(IOnProgressBarStatusListener iOnProgressBarStatusListener, GetTop10Listener getTop10Listener) {
        iOnProgressBarStatusListener.beforeGetData();
        ArrayList<Music> list = new ArrayList<>();
        CollectionReference collectionReference = db.collection("musics");
        collectionReference.orderBy("views", Direction.DESCENDING).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                String id = document.getId();
                                String name = (String) map.get("name");
                                String url = (String) map.get("url");
                                String thumbnailUrl = (String) map.get("thumbnailUrl");
                                Long creationDate = (Long) map.get("creationDate");
                                if (creationDate == null) {
                                    creationDate = 0L;
                                }
                                Long updateDate = (Long) map.get("modifyDate");
                                if (updateDate == null) {
                                    updateDate = 0L;
                                }
                                String singerName = (String) map.get("singerName");
                                String singerId = (String) map.get("singerID");
                                Long views = (Long) map.get("views");
                                if (views == null) {
                                    views = 0L;
                                }
                                String genresId = (String) map.get("genresID");
                                Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate, singerName, singerId, views, genresId);
                                list.add(music);
                            }
                            getTop10Listener.onGetTop10Callback(list);
                            iOnProgressBarStatusListener.afterGetData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public interface GetTop10Listener {
        void onGetTop10Callback(ArrayList<Music> list);
    }

    public interface ReadAllDataMusic {
        void onReadAllDataMusicCallback(ArrayList<Music> list);
    }

    public interface ReadItemMusic {
        void onReadItemMusicCallback(Music music);
    }

    public interface GetInitDataMusic {
        void onGetInitData(ArrayList<Music> list);
    }
}
