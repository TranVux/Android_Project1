package com.example.assignment_pro1121_nhom3.dao;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistDAO {
    private static final String TAG = PlaylistDAO.class.getName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getAllDataPlaylist(IOnProgressBarStatusListener iOnProgressBarStatusListener, ReadAllDataPlaylistListener readAllDataPlaylistListener) {
        iOnProgressBarStatusListener.beforeGetData();
        ArrayList<Playlist> list = new ArrayList<>();
        db.collection("playlists")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                           // Log.d(TAG, document.getId() + " => " + document.getData());
                            Map<String, Object> map = document.getData();
                            String id = document.getId();
                            String name = (String) map.get("name");
                            ArrayList<String> musicsID = (ArrayList<String>) map.getOrDefault("musics", null);
                            Long modifyDate = (Long) map.getOrDefault("modifyDate", 0L);
                            Long creationDate = (Long) map.get("creationDate");
                            String urlThumbnail = (String) map.get("urlThumbnail");
                            String creatorName = (String) map.get("creatorName");
                            Playlist playlist = new Playlist(id, name, musicsID, modifyDate, creationDate, urlThumbnail, creatorName);
                            list.add(playlist);
                        }
                     //   Log.d("finish getting documents", list.size() + "");
                        readAllDataPlaylistListener.onReadAllDataPlaylistCallback(list);
                        iOnProgressBarStatusListener.afterGetData();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void getRecentPublishPlaylist(IOnProgressBarStatusListener iOnProgressBarStatusListener, GetRecentPublishPlaylist getRecentPublishPlaylist) {
        iOnProgressBarStatusListener.beforeGetData();
        ArrayList<Playlist> list = new ArrayList<>();
        db.collection("playlists")
                .whereEqualTo("isPublic", true)
                .limit(30)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               // Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> map = document.getData();
                                String id = document.getId();
                                String name = (String) map.get("name");
                                ArrayList<String> emptyList = new ArrayList<>();
                                ArrayList<String> musicsID = (ArrayList<String>) map.getOrDefault("musics", emptyList);
                                Long modifyDate = (Long) map.getOrDefault("modifyDate", 0L);
                                Long creationDate = (Long) map.get("creationDate");
                                String urlThumbnail = (String) map.get("urlThumbnail");
                                String creatorName = (String) map.get("creatorName");
                                Playlist playlist = new Playlist(id, name, musicsID, modifyDate, creationDate, urlThumbnail, creatorName);
                                list.add(playlist);
                            }
                          //  Log.d("finish getting documents", list.size() + "");
                            getRecentPublishPlaylist.onGetRecentPublishPlaylistSuccess(list);
                            iOnProgressBarStatusListener.afterGetData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            iOnProgressBarStatusListener.afterGetData();
                        }
                    }
                });
    }

    public void getInitDataList(IOnProgressBarStatusListener iOnProgressBarStatusListener, GetInitMusicPlayList getInitMusicPlayList, int limit) {
        ArrayList<Playlist> list = new ArrayList<>();
        iOnProgressBarStatusListener.beforeGetData();
        db.collection("playlists")
                .whereEqualTo("isPublic", true)
                .limit(limit)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               // Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> map = document.getData();
                                String id = document.getId();
                                String name = (String) map.get("name");
                                ArrayList<String> emptyList = new ArrayList<>();
                                ArrayList<String> musicsID = (ArrayList<String>) map.getOrDefault("musics", emptyList);
                                Long modifyDate = (Long) map.getOrDefault("modifyDate", 0L);
                                Long creationDate = (Long) map.get("creationDate");
                                String urlThumbnail = (String) map.get("urlThumbnail");
                                String creatorName = (String) map.get("creatorName");
                                Playlist playlist = new Playlist(id, name, musicsID, modifyDate, creationDate, urlThumbnail, creatorName);
                                list.add(playlist);
                            }
                           // Log.d("finish getting documents", list.size() + "");
                            getInitMusicPlayList.onSuccessGetInitPlayList(list);
                            iOnProgressBarStatusListener.afterGetData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            iOnProgressBarStatusListener.afterGetData();
                        }
                    }
                });
    }

    public void getAllDataPlaylist(String userId, IOnProgressBarStatusListener iOnProgressBarStatusListener, ReadAllDataPlaylistListener readAllDataPlaylistListener) {
        iOnProgressBarStatusListener.beforeGetData();
        ArrayList<Playlist> playlistArrayList = new ArrayList<>();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    ArrayList<String> playlistsID = (ArrayList<String>) snapshot.get("playlistsID");
                    if (playlistsID != null) {
                        if (playlistsID.size()>0) {
                            final int[] countEvents = {0};
                            for (String playlistId : playlistsID) {
                                //id có chứa khoảng trắng??
                                playlistId = playlistId.replaceAll(" ", "");
                               // Log.d(TAG, "playlistId:" + playlistId);
                                DocumentReference docRef = db.collection("playlists").document(playlistId);
                                String finalPlaylistId = playlistId;
                                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            Log.w(TAG, "Listen failed.", error);
                                            return;
                                        }
                                        if (value != null && value.exists()) {
                                      //  Log.d(TAG, "Current data: " + value.getData());
                                            Map<String, Object> map = value.getData();
                                            if (map != null) {
                                                String name = (String) map.get("name");
                                                ArrayList<String> musicsID = (ArrayList<String>) map.getOrDefault("musics", null);
                                                Long modifyDate = (Long) map.getOrDefault("modifyDate", 0L);
                                                Long creationDate = (Long) map.getOrDefault("creationDate", 0L);
                                                String urlThumbnail = (String) map.get("urlThumbnail");
                                                String creatorName = (String) map.get("creatorName");
//                                                Log.d(TAG, "modifyDate: " + modifyDate + "");
//                                                Log.d(TAG, "creationDate: " + creationDate + "");
//                                                Log.d(TAG, "urlThumbnail: " + urlThumbnail + "");
//                                                Log.d(TAG, "mocreatorNamedifyDate: " + creatorName + "");
//                                                Log.d(TAG, "musicsID: " + musicsID.size() + "");
                                                Playlist playlist
                                                         = new Playlist();
                                                playlist.setId(finalPlaylistId);
                                                playlist.setCreationDate(creationDate);
                                                playlist.setModifyDate(modifyDate);
                                                playlist.setName(name);
                                                playlist.setUrlThumbnail(urlThumbnail);
                                                playlist.setMusics(musicsID);
                                                playlist.setCreatorName(creatorName);
                                                playlistArrayList.add(playlist);
                                                countEvents[0]++;
                                                if(countEvents[0] == playlistsID.size()){
                                                    iOnProgressBarStatusListener.afterGetData();
                                                    readAllDataPlaylistListener.onReadAllDataPlaylistCallback(playlistArrayList);
                                                }
                                              //  Log.d(TAG, "playlistArrayList before size: " + playlistArrayList.size());
                                            } else {
                                                Log.d(TAG, "Current data:null");
                                            }
                                        }
                                    }
                                });
                            }
                        }else {
                            iOnProgressBarStatusListener.afterGetData();
                            readAllDataPlaylistListener.onReadAllDataPlaylistCallback(playlistArrayList);
                        }
                    }
                }
            }
        });

    }

    public void getPlaylist(IOnProgressBarStatusListener iOnProgressBarStatusListener, String id, ReadItemPlaylist readItemPlaylist) {
        iOnProgressBarStatusListener.beforeGetData();
        Playlist playlist = new Playlist();
        DocumentReference docRef = db.collection("playlists").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                    //    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> map = document.getData();
                        if (map != null) {
                            String name = (String) map.get("name");
                            ArrayList<String> musicsID = (ArrayList<String>) map.getOrDefault("musics", null);
                            Long modifyDate = (Long) map.getOrDefault("modifyDate", 0L);
                            Long creationDate = (Long) map.getOrDefault("creationDate", 0L);
                            String urlThumbnail = (String) map.get("urlThumbnail");
                            String creatorName = (String) map.get("creatorName");
                            playlist.setId(id);
                            playlist.setCreationDate(creationDate);
                            playlist.setModifyDate(modifyDate);
                            playlist.setName(name);
                            playlist.setUrlThumbnail(urlThumbnail);
                            playlist.setMusics(musicsID);
                            playlist.setCreatorName(creatorName);
                            iOnProgressBarStatusListener.afterGetData();
                            readItemPlaylist.onReadItemPlaylistCallback(playlist);
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

    public void addPlaylist(IOnProgressBarStatusListener iOnProgressBarStatusListener, Playlist playlist, AddPlaylistListener addPlaylistListener) {
        if (playlist != null) {
            iOnProgressBarStatusListener.beforeGetData();
            Map<String, Object> data = new HashMap<>();
            data.put("name", playlist.getName());
            data.put("creationDate", playlist.getCreationDate());
            data.put("modifyDate", playlist.getModifyDate());
            data.put("musics", playlist.getMusics());
            data.put("urlThumbnail", playlist.getUrlThumbnail());
            data.put("creatorName", playlist.getCreatorName());
            data.put("isPublic", false);

            db.collection("playlists")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            addPlaylistListener.onAddPlaylistSuccessCallback(documentReference);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            addPlaylistListener.onAddPlaylistFailureCallback(e);
                        }
                    });
            iOnProgressBarStatusListener.afterGetData();
        }
    }

    public void deletePlaylist(IOnProgressBarStatusListener iOnProgressBarStatusListener, String id, DeletePlaylistListener deletePlaylistListener) {
        iOnProgressBarStatusListener.beforeGetData();
        db.collection("playlists").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        deletePlaylistListener.onDeletePlaylistSuccessCallback();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        deletePlaylistListener.onDeletePlaylistFailureCallback(e);
                    }
                });
        iOnProgressBarStatusListener.afterGetData();
    }

    public void addItemMusicInPlaylist(IOnProgressBarStatusListener iOnProgressBarStatusListener, String idPlaylist, String idMusicToAdd, AddItemMusicInPlaylistListener addItemMusicInPlaylistListener) {
        iOnProgressBarStatusListener.beforeGetData();
        DocumentReference documentReference = db.collection("playlists").document(idPlaylist);
        documentReference.update("musics", FieldValue.arrayUnion(idMusicToAdd))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        addItemMusicInPlaylistListener.onAddItemMusicInPlaylistSuccessCallback();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        addItemMusicInPlaylistListener.onAddItemMusicInPlaylistFailureCallback(e);
                    }
                });
        iOnProgressBarStatusListener.afterGetData();
    }

    public void deleteItemMusicInPlaylist(IOnProgressBarStatusListener iOnProgressBarStatusListener, String idPlaylist, String idMusicToRemove, DeleteItemMusicInPlaylistListener deleteItemMusicInPlaylistListener) {
        iOnProgressBarStatusListener.beforeGetData();
        DocumentReference documentReference = db.collection("playlists").document(idPlaylist);
        documentReference.update("musics", FieldValue.arrayRemove(idMusicToRemove))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        deleteItemMusicInPlaylistListener.onDeleteItemMusicInPlaylistSuccessCallback();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteItemMusicInPlaylistListener.onDeleteItemMusicInPlaylistFailureCallback(e);
                    }
                });
        iOnProgressBarStatusListener.afterGetData();
    }

    public void getMusicInPlaylist(String idPlaylist, IOnProgressBarStatusListener iOnProgressBarStatusListener, ReadMusicInPlaylist readMusicInPlaylist) {
        iOnProgressBarStatusListener.beforeGetData();
        MusicDAO musicDAO = new MusicDAO();
        DocumentReference docRef = db.collection("playlists").document(idPlaylist);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> map = document.getData();
                        if (map != null) {
                            ArrayList<String> musicsID = (ArrayList<String>) map.getOrDefault("musics", null);
                            if (musicsID != null) {
                                musicDAO.getListMusic(musicsID, new MusicDAO.GetListMusic() {
                                    @Override
                                    public void onGetListMusicSuccess(ArrayList<Music> list) {
                                        readMusicInPlaylist.onReadSuccess(list);
                                        iOnProgressBarStatusListener.afterGetData();
                                    }

                                    @Override
                                    public void onGetListMusicFailure() {
                                        iOnProgressBarStatusListener.afterGetData();
                                    }
                                });
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

    public void renamePlaylist(String id, String newName, RenamePlaylistListener renamePlaylistListener) {
        DocumentReference documentReference = db.collection("playlists").document(id);
        documentReference
                .update("name", newName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        renamePlaylistListener.onRenamePlaylistSuccessCallback();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        renamePlaylistListener.onRenamePlaylistFailureCallback(e);
                    }
                });
    }

    public interface GetRecentPublishPlaylist {
        void onGetRecentPublishPlaylistSuccess(ArrayList<Playlist> result);

        void onGetRecentPublishPlaylistFailure();
    }

    public interface AddItemMusicInPlaylistListener {
        void onAddItemMusicInPlaylistSuccessCallback();

        void onAddItemMusicInPlaylistFailureCallback(Exception e);
    }

    public interface GetInitMusicPlayList {
        void onSuccessGetInitPlayList(ArrayList<Playlist> list);

        void onFailureGetInitPlayList(Exception e);
    }

    public interface DeleteItemMusicInPlaylistListener {
        void onDeleteItemMusicInPlaylistSuccessCallback();

        void onDeleteItemMusicInPlaylistFailureCallback(Exception e);
    }

    public interface RenamePlaylistListener {
        void onRenamePlaylistSuccessCallback();

        void onRenamePlaylistFailureCallback(Exception e);

    }

    public interface DeletePlaylistListener {
        void onDeletePlaylistSuccessCallback();

        void onDeletePlaylistFailureCallback(Exception e);
    }

    public interface AddPlaylistListener {
        void onAddPlaylistSuccessCallback(DocumentReference documentReference);

        void onAddPlaylistFailureCallback(Exception e);
    }

    public interface ReadAllDataPlaylistListener {
        void onReadAllDataPlaylistCallback(ArrayList<Playlist> list);
    }

    public interface ReadItemPlaylist {
        void onReadItemPlaylistCallback(Playlist playlist);
    }

    public interface ReadMusicInPlaylist {
        void onReadSuccess(ArrayList<Music> listMusic);

        void onReadFailure(Exception e);
    }
}
