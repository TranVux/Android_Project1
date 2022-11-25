package com.example.assignment_pro1121_nhom3.views;

import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.TAG;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_ID_OF_PLAYLIST;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_IS_DECREASE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_LIMIT;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_PLAYLIST_TYPE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_CREATION_DATE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_GENRES_ID;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_ID;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_INDEX;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_NAME;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_SINGER_ID;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_SINGER_NAME;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_THUMBNAIL_URL;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_UPDATE_DATE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_URL;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_VIEWS;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_TOP_10;
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_DEFAULT;
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_DEVICE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_RECENT_PUBLISH;
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_SINGER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.fragments.PlayerFragment;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    public static MusicPlayer musicPlayer;
    SharedPreferences sharedPreferences;
    private final PlaylistDAO playlistDAO = new PlaylistDAO();
    private ArrayList<Music> listMusicRecent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        sharedPreferences = getSharedPreferences("music_player", MODE_PRIVATE);
        String recentIdPlaylist = sharedPreferences.getString(KEY_ID_OF_PLAYLIST, KEY_TOP_10);
        String playlistType = sharedPreferences.getString(KEY_PLAYLIST_TYPE, KEY_TOP_10);
        Log.d(TAG, "onCreate: recentKey: " + recentIdPlaylist);
        Log.d(TAG, "onCreate: type: " + playlistType);

        // lấy dữ liệu mẫu
        MusicDAO musicDAO = new MusicDAO();

        if (recentIdPlaylist.isEmpty()) {
            recentIdPlaylist = KEY_TOP_10;
        }

        if (!playlistType.equals(KEY_TOP_10)) {
            if (playlistType.equals(PLAYLIST_TYPE_DEFAULT)) {
                playlistDAO.getMusicInPlaylist(recentIdPlaylist, new IOnProgressBarStatusListener() {
                    @Override
                    public void beforeGetData() {

                    }

                    @Override
                    public void afterGetData() {

                    }
                }, new PlaylistDAO.ReadMusicInPlaylist() {
                    @Override
                    public void onReadSuccess(ArrayList<Music> listMusic) {
                        musicPlayer = MusicPlayer.getInstance(listMusic);
                        Log.d(TAG, "onReadSuccess: " + listMusic.size());
                        musicPlayer.setMusicAtPosition(sharedPreferences.getInt(KEY_SONG_INDEX, 0));
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onReadFailure(Exception e) {
                        Log.d(TAG, "onReadFailure: lỗi nè");
                    }
                });
            } else if (playlistType.equals(PLAYLIST_TYPE_SINGER)) {
                musicDAO.getMusicBySingerId(null, recentIdPlaylist, new MusicDAO.GetSingerByID() {
                    @Override
                    public void onGetSuccess(ArrayList<Music> list) {
                        musicPlayer = MusicPlayer.getInstance(list);
                        Log.d(TAG, "onReadSuccess: " + list.size());
                        musicPlayer.setMusicAtPosition(sharedPreferences.getInt(KEY_SONG_INDEX, 0));
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void getNextQuery(Query query) {

                    }
                }, new IOnProgressBarStatusListener() {
                    @Override
                    public void beforeGetData() {

                    }

                    @Override
                    public void afterGetData() {

                    }
                });
            } else if (playlistType.equals(PLAYLIST_TYPE_RECENT_PUBLISH)) {
                musicDAO.getMusicRecentPublish(new MusicDAO.GetMusicRecentPublish() {
                    @Override
                    public void onGetSuccess(ArrayList<Music> result) {
                        musicPlayer = MusicPlayer.getInstance(result);
                        Log.d(TAG, "onReadSuccess: " + result.size());
                        musicPlayer.setMusicAtPosition(sharedPreferences.getInt(KEY_SONG_INDEX, 0));
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onGetFailure() {

                    }
                }, new IOnProgressBarStatusListener() {
                    @Override
                    public void beforeGetData() {

                    }

                    @Override
                    public void afterGetData() {

                    }
                });
            } else if (playlistType.equals(PLAYLIST_TYPE_DEVICE)) {
                getSongList();
                musicPlayer = MusicPlayer.getInstance(listMusicRecent);
                Log.d(TAG, "onCreate: " + listMusicRecent);
                musicPlayer.setMusicAtPosition(sharedPreferences.getInt(KEY_SONG_INDEX, 0));
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            } else {
                musicDAO.getTopMusicByGenres(new IOnProgressBarStatusListener() {
                    @Override
                    public void beforeGetData() {

                    }

                    @Override
                    public void afterGetData() {

                    }
                }, sharedPreferences.getInt(KEY_LIMIT, 100), sharedPreferences.getBoolean(KEY_IS_DECREASE, false), sharedPreferences.getString(KEY_SONG_GENRES_ID, "BXz0IZNAAfk4m56GPfP8"), new MusicDAO.GetTopMusicByGenres() {
                    @Override
                    public void onGetTopByGenresCallBack(ArrayList<Music> list) {
                        musicPlayer = MusicPlayer.getInstance(list);
                        musicPlayer.setMusicAtPosition(sharedPreferences.getInt(KEY_SONG_INDEX, 0));
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }
                });
            }
        } else {
            musicDAO.getTopMusicListen(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {

                }
            }, 10, new MusicDAO.GetTopMusicListener() {
                @Override
                public void onGetTopMusicCallback(ArrayList<Music> list) {
                    if (list.size() != 0) {
                        musicPlayer = MusicPlayer.getInstance(list);
                    } else {
                        getSongList();
                        musicPlayer = MusicPlayer.getInstance(listMusicRecent);
                    }
                    musicPlayer.setMusicAtPosition(sharedPreferences.getInt(KEY_SONG_INDEX, 0));
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            });
        }
    }

    public void getSongList() {
        // Query external audio resources
        ArrayList<Music> list = new ArrayList<>();
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        @SuppressLint("Recycle") Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        // Iterate over results if valid
        if (musicCursor != null && musicCursor.moveToFirst()) {
            // Get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumArtColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int uriSongColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                Long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thumbnailArt = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), String.valueOf(musicCursor.getLong(albumArtColumn))).toString();
                String uriSong = musicCursor.getString(uriSongColumn);
                list.add(new Music(String.valueOf(thisId), thisTitle, uriSong, thumbnailArt, System.currentTimeMillis(), System.currentTimeMillis(), thisArtist, thisArtist, 1000, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        Log.d(TAG, "getSongList: " + list);
        listMusicRecent.addAll(list);
    }
}