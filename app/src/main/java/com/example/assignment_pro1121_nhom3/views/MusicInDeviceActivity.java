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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.PlaylistInDeviceAdapter;
import com.example.assignment_pro1121_nhom3.fragments.MiniPlayerFragment;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.utils.Constants;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class MusicInDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = MusicInDeviceActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PERMISSION = 30;
    private boolean isGrantedPermission = false;
    RecyclerView rclMusicInDevice;
    LinearLayout buttonPlayAll;
    PlaylistInDeviceAdapter myPlaylistAdapter;
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;
    ImageView btnBack;
    TextView amountOfSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_in_device);
        Log.d(TAG, "onCreate: " + isGrantedPermission);
        init();
        setUpMiniPlayer();
        setClick();
        getPermission();
    }

    public void init() {
        buttonPlayAll = findViewById(R.id.buttonPlayAll);
        rclMusicInDevice = findViewById(R.id.recyclerView);
        btnBack = findViewById(R.id.backBtn);
        amountOfSong = findViewById(R.id.amountOfSong);

        myPlaylistAdapter = new PlaylistInDeviceAdapter(MusicInDeviceActivity.this, new PlaylistInDeviceAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music, int position) {
                navigateToPLayer(position);
            }

            @Override
            public void onMoreButtonClick(Music music) {

            }
        });

        myPlaylistAdapter.setData(new ArrayList<>());
        rclMusicInDevice.setAdapter(myPlaylistAdapter);
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
            musicPlayer.setCurrentMode(MusicPlayer.MUSIC_PLAYER_MODE_LOCAL);
            saveCurrentMusic(musicPlayer, musicPlayer.getCurrentSong().getId(), Constants.PLAYLIST_TYPE_DEVICE);
            Log.d(TAG, "onClick: " + musicPlayer.getStateMusicPlayer());
            startActivity(new Intent(MusicInDeviceActivity.this, MainActivity.class));
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
        Intent serviceMusic = new Intent(MusicInDeviceActivity.this, MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        serviceMusic.putExtra(KEY_MODE_MUSIC_PLAYER, mode);
        startService(serviceMusic);
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
        amountOfSong.setText(list.size() + " bài");
        myPlaylistAdapter.setData(list);
    }

    public void getSongListAPI30() {
        File directory = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("Music")));
        File[] mp3File = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".mp3");
            }
        });
        if (mp3File != null) {
            for (File file : mp3File) {
                Log.d(TAG, "getSongListAPI30: " + file.getAbsolutePath() + " " + file.getName());
            }
        }
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageEmulated();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    private ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageEmulated()) {
                                Toast.makeText(MusicInDeviceActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                                getSongList();
                            } else {
                                Toast.makeText(MusicInDeviceActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

    public void getPermission() {
        isGrantedPermission = checkPermission();
        Log.d(TAG, "getPermission: " + isGrantedPermission);
        if (isGrantedPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getSongListAPI30();
            } else {
                getSongList();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    intentActivityResultLauncher.launch(intent);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intentActivityResultLauncher.launch(intent);
                }
            } else {
                ActivityCompat.requestPermissions(MusicInDeviceActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongList();
            } else {
                Toast.makeText(this, "Chưa được cấp quyền camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPlayAll: {
                navigateToPLayer(0);
                break;
            }
            case R.id.backBtn: {
                onBackPressed();
                break;
            }
            default:
                break;
        }
    }

    public void setUpMiniPlayer() {
        if (musicPlayer.getCurrentSong() != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentMiniPlayer, MiniPlayerFragment.newInstance(musicPlayer.getCurrentSong()))
                    .commit();
        }
    }
}