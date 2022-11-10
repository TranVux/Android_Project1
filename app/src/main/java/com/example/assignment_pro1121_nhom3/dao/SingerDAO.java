package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class SingerDAO {
    private static final String TAG = SingerDAO.class.getName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ArrayList<Singer> getAllDataSinger(ReadAllDataSinger readAllDataSinger){
        ArrayList<Singer> list = new ArrayList<>();
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
                                Singer singer = new Singer(id,name,avtUrl,desc);
                                list.add(singer);
                            }
                            Log.d("finish getting documents",list.size()+"");
                            readAllDataSinger.onReadAllDataSingerCallback(list);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return list;
    }

    public void getSinger(IOnProgressBarStatusListener iOnProgressBarStatusListener, String id, ReadItemSinger readItemSinger){
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
                            if(singer.getId() != null){
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


    public interface ReadAllDataSinger{
        void onReadAllDataSingerCallback(ArrayList<Singer> list);
    }
    public interface ReadItemSinger{
        void onReadItemSingerCallback(Singer singer);
    }
}
