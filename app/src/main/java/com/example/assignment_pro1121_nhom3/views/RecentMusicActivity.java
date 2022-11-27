package com.example.assignment_pro1121_nhom3.views;

import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_ID_OF_PLAYLIST;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_MODE_MUSIC_PLAYER;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_MUSIC;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.PlaylistInDeviceAdapter;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.example.assignment_pro1121_nhom3.utils.Constants;

import java.util.ArrayList;

public class RecentMusicActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = RecentMusicActivity.class.getSimpleName();

    RecyclerView rclRecentSong;
    LinearLayout buttonPlayAll;
    PlaylistInDeviceAdapter myPlaylistAdapter;
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;
    ImageView btnBack;
    TextView amountOfSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_music);
        init();
        setClick();
    }

    public void init() {
        rclRecentSong = findViewById(R.id.recyclerView);
        btnBack = findViewById(R.id.backBtn);
        buttonPlayAll = findViewById(R.id.buttonPlayAll);
        amountOfSong = findViewById(R.id.amountOfSong);

        myPlaylistAdapter = new PlaylistInDeviceAdapter(RecentMusicActivity.this, new PlaylistInDeviceAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music, int position) {
                navigateToPLayer(position);
            }

            @Override
            public void onMoreButtonClick(Music music) {

            }
        });
        ArrayList<Music> recentSongList = (ArrayList<Music>) SongRecentDatabase.getInstance(RecentMusicActivity.this).musicRecentDAO().getListSongRecent();
        amountOfSong.setText(recentSongList.size() + " bài");
        myPlaylistAdapter.setData(recentSongList);
        rclRecentSong.setAdapter(myPlaylistAdapter);
    }

    public void setClick() {
        buttonPlayAll.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    public void navigateToPLayer(int position) {
        if (myPlaylistAdapter.getItemCount() > 0) {
            musicPlayer.pauseSong(musicPlayer.getCurrentPositionSong());
            musicPlayer.clearPlaylist();
            musicPlayer.setPlayList(myPlaylistAdapter.getList());
            musicPlayer.setMusicAtPosition(position);
            try {
                musicPlayer.setStateMusicPlayer(MusicPlayer.MUSIC_PLAYER_STATE_PLAYING);
            } catch (Exception e) {
                e.printStackTrace();
            }
            musicPlayer.setCurrentMode(MusicPlayer.MUSIC_PLAYER_MODE_ONLINE);
            saveCurrentMusic(musicPlayer, musicPlayer.getCurrentSong().getId(), Constants.PLAYLIST_TYPE_RECENT_PLAYLIST);
            Log.d(TAG, "onClick: " + musicPlayer.getStateMusicPlayer());
            startActivity(new Intent(RecentMusicActivity.this, MainActivity.class));
            startServiceMusic(musicPlayer.getCurrentSong(), MusicPlayer.MUSIC_PLAYER_ACTION_RESET_SONG, musicPlayer.getCurrentMode());
        }
    }

    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist, String typePlayList) {
        SharedPreferences sharedPreferences = getSharedPreferences("music_player", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SONG_NAME, musicPlayer.getCurrentSong().getName());
        editor.putString(KEY_SONG_URL, musicPlayer.getCurrentSong().getUrl());
        editor.putString(KEY_SONG_THUMBNAIL_URL, musicPlayer.getCurrentSong().getThumbnailUrl());
        editor.putString(KEY_SONG_ID, musicPlayer.getCurrentSong().getId());
        editor.putLong(KEY_SONG_VIEWS, musicPlayer.getCurrentSong().getViews());
        editor.putString(KEY_SONG_SINGER_ID, musicPlayer.getCurrentSong().getSingerId());
        editor.putString(KEY_SONG_SINGER_NAME, musicPlayer.getCurrentSong().getSingerName());
        editor.putString(KEY_SONG_GENRES_ID, musicPlayer.getCurrentSong().getGenresId());
        editor.putLong(KEY_SONG_CREATION_DATE, musicPlayer.getCurrentSong().getCreationDate());
        editor.putLong(KEY_SONG_UPDATE_DATE, musicPlayer.getCurrentSong().getUpdateDate());
        editor.putInt(KEY_SONG_INDEX, musicPlayer.getPlayListMusic().indexOf(musicPlayer.getCurrentSong()));

        Log.d(TAG, "saveCurrentMusic: " + idPlaylist);
        Log.d(TAG, "saveCurrentMusic: " + typePlayList);

        editor.putString(KEY_PLAYLIST_TYPE, typePlayList);
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        editor.apply();
    }

    public void startServiceMusic(Music music, int action, String mode) {
        Intent serviceMusic = new Intent(RecentMusicActivity.this, MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        serviceMusic.putExtra(KEY_MODE_MUSIC_PLAYER, mode);
        startService(serviceMusic);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn: {
                onBackPressed();
                break;
            }
            case R.id.buttonPlayAll: {
                navigateToPLayer(0);
                break;
            }
            default:
                break;
        }
    }
}