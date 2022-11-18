package com.example.assignment_pro1121_nhom3.views;

import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.TAG;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_ID_OF_PLAYLIST;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
        String musicID = sharedPreferences.getString(KEY_SONG_ID, "");
        String recentIdPlaylist = sharedPreferences.getString(KEY_ID_OF_PLAYLIST, KEY_TOP_10);
        Log.d(TAG, "onCreate: " + recentIdPlaylist);

        // lấy dữ liệu mẫu
        MusicDAO musicDAO = new MusicDAO();

        if (!recentIdPlaylist.equals(KEY_TOP_10)) {
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
        } else {
            musicDAO.getTopMusic10(new MusicDAO.GetTop10Listener() {
                @Override
                public void onGetTop10Callback(ArrayList<Music> list) {
                    musicPlayer = MusicPlayer.getInstance(list);
                    musicPlayer.setMusicAtPosition(sharedPreferences.getInt(KEY_SONG_INDEX, 0));
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            });
        }
    }
}