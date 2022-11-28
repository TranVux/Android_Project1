package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingerDAO {
    private static final String TAG = SingerDAO.class.getName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getAllDataSinger(IOnProgressBarStatusListener iOnProgressBarStatusListener, ReadAllDataSinger readAllDataSinger) {
        ArrayList<Singer> list = new ArrayList<>();
        iOnProgressBarStatusListener.beforeGetData();
        db.collection("singers")
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
                                String avtUrl = (String) map.get("avtUrl");
                                String desc = (String) map.get("desc");
                                Singer singer = new Singer(id, name, avtUrl, desc);
                                list.add(singer);
                            }
                            Log.d("finish getting documents", list.size() + "");
                            readAllDataSinger.onReadAllDataSingerCallback(list);
                            iOnProgressBarStatusListener.afterGetData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getCountDocumentSingers(GetCountDocument getCountDocument) {
        CollectionReference collection = db.collection("singers");
        AggregateQuery countQuery = collection.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                getCountDocument.onGetCountSuccess(snapshot.getCount());
            } else {
                Log.d(TAG, "Count failed: ", task.getException());
            }
        });
    }

    public void getPaginateSinger(Query nextQuery, GetDataPagination getDataPagination, IOnProgressBarStatusListener iOnProgressBarStatusListener) {
        iOnProgressBarStatusListener.beforeGetData();
        Query query;
        ArrayList<Singer> list = new ArrayList<>();
        if (nextQuery == null) {
            query = db.collection("singers").limit(10);
        } else {
            query = nextQuery;
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                DocumentSnapshot lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                Query next = db.collection("singers")
                        .startAfter(lastVisible)
                        .limit(10);

                getDataPagination.getNextQuery(next);
                for (QueryDocumentSnapshot document : documentSnapshots) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    Map<String, Object> map = document.getData();
                    String id = document.getId();
                    String name = (String) map.get("name");
                    String avtUrl = (String) map.get("avtUrl");
                    String desc = (String) map.get("desc");
                    Singer singer = new Singer(id, name, avtUrl, desc);
                    list.add(singer);
                }

                Log.d("finish getting documents", list.size() + "");
                getDataPagination.onGetSuccess(list);
                iOnProgressBarStatusListener.afterGetData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getSinger(IOnProgressBarStatusListener iOnProgressBarStatusListener, String id, ReadItemSinger readItemSinger) {
        iOnProgressBarStatusListener.beforeGetData();
        Singer singer = new Singer();
        DocumentReference docRef = db.collection("singers").document(id);
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
                            String avtUrl = (String) map.get("avtUrl");
                            String desc = (String) map.get("desc");
                            singer.setId(id);
                            singer.setAvtUrl(avtUrl);
                            singer.setName(name);
                            singer.setDesc(desc);
                            iOnProgressBarStatusListener.afterGetData();
                            if (singer.getId() != null) {
                                readItemSinger.onReadItemSingerCallback(singer);
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

    public synchronized void getTopSinger(GetTopSinger getTopSinger, IOnProgressBarStatusListener iOnProgressBarStatusListener) {
        // viết tạm @@
        ArrayList<Singer> listSinger = new ArrayList<>();
        MusicDAO musicDAO = new MusicDAO();
        // danh sách ID đã lấy dữ liệu
        ArrayList<String> hadFetch = new ArrayList<>();
        Map<Long, Singer> mapSinger = new HashMap<>();
        iOnProgressBarStatusListener.beforeGetData();
        musicDAO.getMusicDecreaseByView(new MusicDAO.GetMusicDecreaseByView() {
            @Override
            public void onGetSuccess(ArrayList<Music> list) {
                for (int i = 0; i < list.size(); i++) {
                    String singerID = list.get(i).getSingerId();
                    if (!hadFetch.contains(singerID)) {
                        Log.d(TAG, "onGetSuccess: " + list.get(i).getViews() + " SingerName: " + list.get(i).getSingerName());
                        hadFetch.add(singerID);
                        int finalI = i;
                        getSinger(new IOnProgressBarStatusListener() {
                            @Override
                            public void beforeGetData() {

                            }

                            @Override
                            public void afterGetData() {

                            }
                        }, list.get(i).getSingerId(), new ReadItemSinger() {
                            @Override
                            public void onReadItemSingerCallback(Singer singer) {
                                mapSinger.put(list.get(finalI).getViews(), singer);
                                listSinger.add(singer);
                                if (listSinger.size() == 10) {
                                    Log.d(TAG, "onReadItemSingerCallback: " + mapSinger.size() + " " + mapSinger.values());
                                    getTopSinger.onGetTopSingersSuccess(mapSinger);
                                    iOnProgressBarStatusListener.afterGetData();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public interface GetTopSinger {
        void onGetTopSingersSuccess(Map<Long, Singer> mapSinger);
    }

    public interface ReadAllDataSinger {
        void onReadAllDataSingerCallback(ArrayList<Singer> list);
    }

    public interface ReadItemSinger {
        void onReadItemSingerCallback(Singer singer);
    }

    public interface GetCountDocument {
        void onGetCountSuccess(long count);

        void onGetCountFailure(Exception e);
    }

    public interface GetDataPagination {
        void onGetSuccess(ArrayList<Singer> list);

        void getNextQuery(Query query);
    }
}
