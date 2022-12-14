package com.example.assignment_pro1121_nhom3.dao;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.utils.ListFilterUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class MusicDAO {
    private static final String TAG = MusicDAO.class.getName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // lấy toàn bộ bài hát
    public void getAllDataMusic(IOnProgressBarStatusListener iOnProgressBarStatusListener,
                                ReadAllDataMusic readAllDataMusic) {
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
                                Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate,
                                        singerName, singerId, views, genresId);
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

    public void getMusicItemWithLimit(IOnProgressBarStatusListener iOnProgressBarStatusListener, int limitCount,
                                      GetDataMusicWithLimit getDataMusicWithLimit) {
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
                                Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate,
                                        singerName, singerId, views, genresId);
                                list.add(music);
                            }
                            Log.d("finish getting documents", list.size() + "");
                            getDataMusicWithLimit.onGetLimitData(list);
                            iOnProgressBarStatusListener.afterGetData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getMusicRecentPublish(GetMusicRecentPublish getMusicRecentPublish,
                                      IOnProgressBarStatusListener iOnProgressBarStatusListener) {
        iOnProgressBarStatusListener.beforeGetData();
        ArrayList<Music> list = new ArrayList<>();
        db.collection("musics").orderBy("creationDate", Direction.DESCENDING).limit(10)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate,
                                        singerName, singerId, views, genresId);
                                list.add(music);
                            }
                            Log.d("finish getting documents", list.size() + "");
                            getMusicRecentPublish.onGetSuccess(list);
                            iOnProgressBarStatusListener.afterGetData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iOnProgressBarStatusListener.afterGetData();
                        getMusicRecentPublish.onGetFailure();
                    }
                });
    }

    // lấy 1 bài hát
    public void getMusic(IOnProgressBarStatusListener iOnProgressBarStatusListener, String id,
                         ReadItemMusic readItemMusic) {
        iOnProgressBarStatusListener.beforeGetData();
//        Music music = new Music();
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
                            iOnProgressBarStatusListener.afterGetData();
                            readItemMusic.onReadItemMusicCallback(new Music(id, name, url, thumbnailUrl, creationDate, updateDate, singerName, singerId, views, genresId));
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

    public void getMusicDecreaseByView(GetMusicDecreaseByView getMusicDecreaseByView) {
        getMusicItemWithLimit(new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {

            }

            @Override
            public void afterGetData() {

            }
        }, 150, new GetDataMusicWithLimit() {
            @Override
            public void onGetLimitData(ArrayList<Music> list) {
                list.sort(Comparator.comparing(Music::getViews).reversed());
                getMusicDecreaseByView.onGetSuccess(list);
            }
        });
    }

    public void getCountDocumentMusic(GetCountDocument getCountDocument, String singerID) {
        db.collection("musics").whereEqualTo("singerID", singerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        getCountDocument.onGetCountSuccess(task.getResult().size());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void getMusicBySingerId(Query query, String singerID, GetSingerByID getSingerByID,
                                   IOnProgressBarStatusListener iOnProgressBarStatusListener) {
        ArrayList<Music> list = new ArrayList<>();
        Query dataQuery;
        if (query == null) {
            dataQuery = db.collection("musics").whereEqualTo("singerID", singerID).limit(20);
        } else {
            dataQuery = query;
        }

        dataQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().size() > 0) {
                    iOnProgressBarStatusListener.beforeGetData();
                    DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    Query next = db.collection("musics")
                            .whereEqualTo("singerID", singerID)
                            .startAfter(lastVisible)
                            .limit(20);
                    getSingerByID.getNextQuery(next);

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
                        Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate, singerName,
                                singerId, views, genresId);
                        list.add(music);
                    }
                    Log.d("finish getting documents", list.size() + "");
                    // gửi list kết quả ra bên ngoài
                    getSingerByID.onGetSuccess(list);
                    // xử lý progress bar
                    iOnProgressBarStatusListener.afterGetData();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    iOnProgressBarStatusListener.afterGetData();
                }
            }
        });
    }

    public void getListMusic(ArrayList<String> listID, GetListMusic getListMusic) {
        ArrayList<Music> list = new ArrayList<>();
        if (!listID.isEmpty()) {
            db.collection("musics").whereIn(FieldPath.documentId(), listID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
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
                                        Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate,
                                                singerName, singerId, views, genresId);
                                        list.add(music);
                                    }
                                }
                                getListMusic.onGetListMusicSuccess(list);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            Log.d(TAG, "getListMusic: playlist rỗng");
            getListMusic.onGetListMusicFailure();
        }
    }

    public void getPlaylistItem(ArrayList<String> listID, GetListMusic listMusic) {
        ArrayList<Music> finalListMusic = new ArrayList<>();
        if (ListFilterUtil.checkMaxSizeListFilter(listID)) {
            int chunk = 10; // chunk size to divide
            for (int i = 0; i < listID.size(); i += chunk) {
                Log.d(TAG, "getPlaylistItem: " + Arrays.toString(Arrays.copyOfRange(new ArrayList[]{listID}, i, Math.min(listID.size(), i + chunk))));
//                ArrayList<String> tempArrId = new ArrayList<>();
//                Collections.addAll(tempArrId, )
//                getListMusic(temp);
            }
        } else {
            getListMusic(listID, listMusic);
        }
    }

    public void searchMusic(String query, SearchMusic searchMusic,
                            IOnProgressBarStatusListener iOnProgressBarStatusListener) {
        String end = query.trim() + "\uf8ff";
        ArrayList<Music> listResult = new ArrayList<>();
        iOnProgressBarStatusListener.beforeGetData();
        db.collection("musics").whereGreaterThanOrEqualTo("name", query).whereLessThanOrEqualTo("name", end)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
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
                                    Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate,
                                            singerName, singerId, views, genresId);
                                    listResult.add(music);
                                }
                            }
                            searchMusic.onSearchSuccess(listResult);
                            iOnProgressBarStatusListener.afterGetData();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        searchMusic.onSearchFailure();
                        iOnProgressBarStatusListener.afterGetData();
                    }
                });
    }

    public void searchByKeyWord(String keyWord, Query nextQuery, GetDataPagination getDataPagination, SearchMusic searchMusic, IOnProgressBarStatusListener iOnProgressBarStatusListener) {
        ArrayList<Music> listResult = new ArrayList<>();
        iOnProgressBarStatusListener.beforeGetData();

        String finalKeyWord = keyWord.trim().toLowerCase();
        Query query;

        if (nextQuery == null) {
            query = db.collection("musics").limit(50);
        } else {
            query = nextQuery;
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                    //get next query
                    DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                    Query next = db.collection("musics")
                            .startAfter(lastVisible)
                            .limit(50);
                    getDataPagination.getNextQuery(next);

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
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
                            Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate,
                                    singerName, singerId, views, genresId);
                            if (checkKeyWord(finalKeyWord, music)) {
                                listResult.add(music);
                            }
                        }
                    }
                    searchMusic.onSearchSuccess(listResult);
                    iOnProgressBarStatusListener.afterGetData();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iOnProgressBarStatusListener.afterGetData();
            }
        });
    }

    public boolean checkKeyWord(String keyWord, Music music) {
        return music.getName().toLowerCase().contains(keyWord);
    }

    // public void getTopMusic10(GetTop10Listener getTop10Listener) {

    public void getTopMusicByGenres(IOnProgressBarStatusListener iOnProgressBarStatusListener, int limit,
                                    boolean isDescending, String genreId, GetTopMusicByGenres getTopMusicBuyGenreListener) {
        iOnProgressBarStatusListener.beforeGetData();
        ArrayList<Music> list = new ArrayList<>();
        CollectionReference collectionReference = db.collection("musics");
        Query query;
        if (isDescending) {
            query = collectionReference.whereEqualTo("genresID", genreId).orderBy("views", Direction.DESCENDING)
                    .limit(limit);
        } else {
            query = collectionReference.whereEqualTo("genresID", genreId).limit(limit);
        }
        query.get()
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
                                Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate,
                                        singerName, singerId, views, genresId);
                                list.add(music);
                            }
                            getTopMusicBuyGenreListener.onGetTopByGenresCallBack(list);
                            iOnProgressBarStatusListener.afterGetData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getTopMusicListen(IOnProgressBarStatusListener iOnProgressBarStatusListener, int limit,
                                  GetTopMusicListener getTopMusicListener) {
        iOnProgressBarStatusListener.beforeGetData();
        ArrayList<Music> list = new ArrayList<>();
        CollectionReference collectionReference = db.collection("musics");
        collectionReference.orderBy("views", Direction.DESCENDING).limit(limit).get()
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
                                Music music = new Music(id, name, url, thumbnailUrl, creationDate, updateDate,
                                        singerName, singerId, views, genresId);
                                list.add(music);
                            }
                            getTopMusicListener.onGetTopMusicCallback(list);
                            iOnProgressBarStatusListener.afterGetData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public interface GetDataPagination {

        void getNextQuery(Query query);
    }

    public interface GetCountDocument {
        void onGetCountSuccess(long count);
    }

    public interface GetSingerByID {
        void onGetSuccess(ArrayList<Music> list);

        void getNextQuery(Query query);
    }

    public interface GetMusicDecreaseByView {
        void onGetSuccess(ArrayList<Music> list);
    }

    public interface GetTopMusicListener {
        void onGetTopMusicCallback(ArrayList<Music> list);
    }

    public interface ReadAllDataMusic {
        void onReadAllDataMusicCallback(ArrayList<Music> list);
    }

    public interface ReadItemMusic {
        void onReadItemMusicCallback(Music music);
    }

    public interface GetDataMusicWithLimit {
        void onGetLimitData(ArrayList<Music> list);
    }

    public interface GetListMusic {
        void onGetListMusicSuccess(ArrayList<Music> list);

        void onGetListMusicFailure();
    }

    public interface SearchMusic {
        void onSearchSuccess(ArrayList<Music> result);

        void onSearchFailure();
    }

    public interface GetMusicRecentPublish {
        void onGetSuccess(ArrayList<Music> result);

        void onGetFailure();
    }

    public interface GetTopMusicByGenres {
        void onGetTopByGenresCallBack(ArrayList<Music> list);
    }
}
