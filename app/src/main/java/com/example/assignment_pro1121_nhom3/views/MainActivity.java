package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.fragments.HomeFragment;
import com.example.assignment_pro1121_nhom3.fragments.PlayerFragment;
import com.example.assignment_pro1121_nhom3.fragments.UserFragment;
import com.example.assignment_pro1121_nhom3.interfaces.HandleChangeColorBottomNavigation;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import static com.example.assignment_pro1121_nhom3.utils.Constants.*;
import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class MainActivity extends AppCompatActivity implements HandleChangeColorBottomNavigation {
    public static final String TAG = MainActivity.class.getSimpleName();

    BottomNavigationView bottomNavigation;
    public ImageView imageThumbnailCurrentMusic;

    // handle seekbar
    boolean isSetMax = false;
    boolean updateSeekBar = true;
    public CircularSeekBar circularSeekBar;
    View customButtonPlay, customRadio;

    // Trạng thái của nút play 0 là pause 1 là start/resume
    int stateButtonPlay = 0;

    PlayerFragment playerFragment;
    HomeFragment homeFragment;
    UserFragment userFragment;
    boolean agreeBack = false;
    private boolean doubleBackToExitPressedOnce = false;
    ArrayList<Music> listMusicRecent = new ArrayList<>();
    boolean isPlaying = false, isStart = false, isDestroy = false, isCreated = false;

    ImageView btnPlay;

    // Music player
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;
    //
    // cache
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesMusicList;

    // Receiver
    // MusicPlayerReceiver musicPlayerReceiver;

    public static String recentIdPlaylist;
    PlaylistDAO playlistDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // đổi màu của status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        // Đổi màu status bar nè
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // bỏ hiệu ứng fade
        Transition transition = TransitionInflater.from(MainActivity.this)
                .inflateTransition(android.R.transition.no_transition);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);

        // khai báo các fragment
        homeFragment = new HomeFragment(this);
        userFragment = new UserFragment(this);
        // musicPlayerReceiver = new MusicPlayerReceiver();
        playlistDAO = new PlaylistDAO();
        // playerFragment = new PlayerFragment(this);
        setContentView(R.layout.activity_main);
        init();

        sharedPreferencesMusicList = getSharedPreferences("music_player", MODE_PRIVATE);
        recentIdPlaylist = sharedPreferencesMusicList.getString(KEY_ID_OF_PLAYLIST, KEY_TOP_10);
        Log.d(TAG, "onCreate: " + recentIdPlaylist);

        // Lưu trạng thái có đang chơi nhạc hay không
        sharedPreferences = getSharedPreferences("music_player_state", MODE_PRIVATE);

        isPlaying = sharedPreferences.getBoolean(KEY_STATE_IS_PLAYING, false);
        isStart = sharedPreferences.getBoolean(KEY_STATE_IS_START, false);
        isCreated = sharedPreferences.getBoolean(KEY_STATE_IS_CREATED, true);
        isDestroy = sharedPreferences.getBoolean(KEY_STATE_IS_DESTROYED, false);
        // cài đặt music player
        Log.d(TAG, "onCreate: " + musicPlayer.getSizeOfPlayList());
        musicPlayer.setMusicPlayerCallBack(new MusicPlayerCallback() {
            @Override
            public void onPause() {
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                if (playerFragment == null)
                    return;
                playerFragment.handleRotateImageThumbnail();
            }

            @Override
            public void onResume() {
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                if (playerFragment == null)
                    return;
                playerFragment.handleRotateImageThumbnail();
            }

            @Override
            public void updateSeekBar(int currentPositionSong) {

            }
        });
        Log.d(TAG, "onCreate: " + musicPlayer.getPlayListMusic().size());
        // set state for music player
        musicPlayer.setInitState(isPlaying, isStart, isDestroy, isCreated);
        // get cache data previous music

        // xử lý player state
        handleStateMusicPlayer(musicPlayer);
        playMusicPlayer();

        Log.d(TAG, "onCreate: save state: " + isCreated + " " + isPlaying + " " + isStart + " " + isDestroy);
        Log.d(TAG, "playMusicPlayer: " + musicPlayer.getStateMusicPlayer());
        //
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentLayout, PlayerFragment.newInstance(musicPlayer.getPlayListMusic()), "PlayerFragment")
                .commit();
        if (musicPlayer.getCurrentSong() != null) {
            // handle thumbnail của player
            Glide.with(MainActivity.this)
                    .load(musicPlayer.getCurrentSong().getThumbnailUrl())
                    .error(R.drawable.fallback_img)
                    .apply(new RequestOptions().override(180, 180))
                    .into(imageThumbnailCurrentMusic);
        }

        // set layout mặc định cho fragment là màn hình home
        bottomNavigation.getMenu().getItem(1).setChecked(true);
        // getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout,
        // PlayerFragment.newInstance(musicPlayer.getPlayListMusic()),
        // "PlayerFragment").commit();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.home: {
                        customRadio.setVisibility(View.VISIBLE);
                        customButtonPlay.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentLayout, homeFragment, "FragmentHome").commit();
                        break;
                    }
                    case R.id.player: {
                        imageThumbnailCurrentMusic.performClick();
                        break;
                    }
                    case R.id.user: {
                        customRadio.setVisibility(View.VISIBLE);
                        customButtonPlay.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentLayout, userFragment, "FragmentUser").commit();
                        break;
                    }
                }
                return false;
            }
        });
    }

    public void init() {
        // bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setItemIconTintList(null);
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(1);

        customRadio = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.custom_item_bottom_navigation_player, bottomNavigationMenuView, false);
        imageThumbnailCurrentMusic = customRadio.findViewById(R.id.imageThumbnailCurrentMusic);
        circularSeekBar = customRadio.findViewById(R.id.seekbar);

        customButtonPlay = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.custom_item_bottom_navigation_button_play, bottomNavigationMenuView, false);

        itemView.addView(customRadio);
        itemView.addView(customButtonPlay);
        customButtonPlay.setVisibility(View.VISIBLE);
        customRadio.setVisibility(View.GONE);
        btnPlay = customButtonPlay.findViewById(R.id.btnPlay);

        imageThumbnailCurrentMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customRadio.setVisibility(View.GONE);
                customButtonPlay.setVisibility(View.VISIBLE);
                bottomNavigation.getMenu().getItem(1).setChecked(true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentLayout, PlayerFragment.newInstance(listMusicRecent), "PlayerFragment")
                        .commit();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateButtonPlay == 1) {
                    stopMusicPlayer();
                } else {
                    playMusicPlayer();
                }
            }
        });

    }

    @Override
    public void toTransparent() {
        bottomNavigation
                .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
    }

    @Override
    public void toColor() {
        bottomNavigation
                .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_bottom_navigation));
    }

    private void handleIntent(Intent intent) {
        int action = intent.getIntExtra("action", -1);
        int currentPositionDuration = intent.getIntExtra(KEY_CURRENT_MUSIC_POSITION, 0);

        switch (action) {
            case MUSIC_PLAYER_ACTION_START:
            case MUSIC_PLAYER_ACTION_RESUME: {
                musicPlayer.resumeSong();
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // cập nhật lại nút play
                btnPlay.setImageResource(R.drawable.ic_pause);
                stateButtonPlay = 1;
                break;
            }
            case MUSIC_PLAYER_ACTION_PAUSE: {
                musicPlayer.pauseSong(currentPositionDuration);
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_IDLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // cập nhật lại nút play
                btnPlay.setImageResource(R.drawable.ic_play);
                stateButtonPlay = 0;
                break;
            }
            case MUSIC_PLAYER_ACTION_NEXT:
            case MUSIC_PLAYER_ACTION_COMPLETE: {
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "handleIntent: next");
                musicPlayer.nextSong(musicPlayer.getCurrentIndexSong());
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                if (playerFragment == null)
                    return;
                playerFragment.setContentForNextMusic(musicPlayer.getNextSong());
                playerFragment.setContentInit(musicPlayer.getCurrentSong());
                playerFragment.timeLine.setProgress(0);
                circularSeekBar.setProgress(0);
                handleChangeMusic();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_RESET_SONG, musicPlayer.getCurrentMode());
                    }
                }, 500);
                break;
            }
            case MUSIC_PLAYER_ACTION_PREVIOUS: {
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "handleIntent: previous");
                musicPlayer.previousSong(musicPlayer.getCurrentIndexSong());
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                if (playerFragment == null)
                    return;
                playerFragment.setContentForNextMusic(musicPlayer.getNextSong());
                playerFragment.setContentInit(musicPlayer.getCurrentSong());
                playerFragment.timeLine.setProgress(0);
                circularSeekBar.setProgress(0);
                handleChangeMusic();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_RESET_SONG, musicPlayer.getCurrentMode());
                    }
                }, 500);
                break;
            }
            case MUSIC_PLAYER_ACTION_GO_TO_SONG: {
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int indexSong = intent.getIntExtra(KEY_SONG_INDEX, 0);
                musicPlayer.setMusicAtPosition(indexSong);
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                if (playerFragment == null)
                    return;
                playerFragment.setContentForNextMusic(musicPlayer.getNextSong());
                playerFragment.setContentInit(musicPlayer.getCurrentSong());
                playerFragment.timeLine.setProgress(0);
                circularSeekBar.setProgress(0);
                handleChangeMusic();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_RESET_SONG, musicPlayer.getCurrentMode());
                    }
                }, 500);
                break;
            }
            case MUSIC_PLAYER_ACTION_SEEK_TO_POSITION: {
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                if (playerFragment == null)
                    return;
                playerFragment.handleRotateImageThumbnail();
                break;
            }
            default:
                break;
        }
        handleStateMusicPlayer(musicPlayer);
        saveCurrentMusic(musicPlayer, recentIdPlaylist);
        Log.d(TAG, "playMusicPlayer: " + musicPlayer.getStateMusicPlayer());
    }

    public void playMusicPlayer() {
        if (Objects.equals(musicPlayer.getStateMusicPlayer(), MUSIC_PLAYER_STATE_IDLE)
                && isMyServiceRunning(MusicPlayerService.class)) {
            startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_RESUME, musicPlayer.getCurrentMode());
        } else if (Objects.equals(musicPlayer.getStateMusicPlayer(), MUSIC_PLAYER_STATE_PLAYING)) {
            startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_RESUME, musicPlayer.getCurrentMode());
        } else {
            startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_START, musicPlayer.getCurrentMode());
        }
    }

    public void stopMusicPlayer() {
        if (Objects.equals(musicPlayer.getStateMusicPlayer(), MUSIC_PLAYER_STATE_PLAYING)) {
            startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_PAUSE, musicPlayer.getCurrentMode());
        }
    }

    public void handleStateMusicPlayer(MusicPlayer musicPlayer) {
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        switch (musicPlayer.getStateMusicPlayer()) {
            case MUSIC_PLAYER_STATE_IDLE: {
                stateButtonPlay = 0;
                btnPlay.setImageResource(R.drawable.ic_play);
                editor.putBoolean(KEY_STATE_IS_IDLE, true);
                editor.putBoolean(KEY_STATE_IS_PLAYING, false);
                editor.putBoolean(KEY_STATE_IS_START, true);
                break;
            }
            case MUSIC_PLAYER_STATE_PLAYING: {
                stateButtonPlay = 1;
                btnPlay.setImageResource(R.drawable.ic_pause);
                editor.putBoolean(KEY_STATE_IS_IDLE, false);
                editor.putBoolean(KEY_STATE_IS_PLAYING, true);
                editor.putBoolean(KEY_STATE_IS_START, true);
                break;
            }
            case MUSIC_PLAYER_STATE_DESTROYED: {
                editor.putBoolean(KEY_STATE_IS_CREATED, false);
                editor.putBoolean(KEY_STATE_IS_DESTROYED, true);
                editor.putBoolean(KEY_STATE_IS_START, false);
                break;
            }
            case MUSIC_PLAYER_STATE_CREATED: {
                editor.putBoolean(KEY_STATE_IS_CREATED, true);
                editor.putBoolean(KEY_STATE_IS_DESTROYED, false);
                break;
            }
        }
        editor.apply();
    }

    private BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                updateSeekBar(intent);
                handleIntent(intent);
            }
        }
    };

    public void handleChangeMusic() {
        Glide.with(MainActivity.this)
                .load(musicPlayer.getCurrentSong().getThumbnailUrl())
                .error(R.drawable.fallback_img)
                .apply(new RequestOptions().override(45, 45))
                .into(imageThumbnailCurrentMusic);
    }

    public void startServiceMusic(Music music, int action, String mode) {
        Intent serviceMusic = new Intent(MainActivity.this, MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        serviceMusic.putExtra(KEY_MODE_MUSIC_PLAYER, mode);
        startService(serviceMusic);
    }

    public boolean checkUniqueSong(String songName) {
        ArrayList<Music> list = (ArrayList<Music>) SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().checkSong(songName);
        return list.size() <= 0;
    }

    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist) {
        if (checkUniqueSong(musicPlayer.getCurrentSong().getName())) {
            SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().insertSong(musicPlayer.getCurrentSong());
        }
        SharedPreferences.Editor editor = sharedPreferencesMusicList.edit();
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
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter musicServiceIntentFilter = new IntentFilter(MUSIC_PLAYER_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(musicReceiver, musicServiceIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicReceiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBackPressed() {
        if (agreeBack) {
            super.onBackPressed();
            return;
        }
        agreeBack = true;
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Back thêm lần nữa để thoát", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recentIdPlaylist = sharedPreferencesMusicList.getString(KEY_ID_OF_PLAYLIST, KEY_TOP_10);
        saveCurrentMusic(musicPlayer, recentIdPlaylist);
        Log.d(TAG, "onDestroy: Caching");
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void updateSeekBar(Intent intent) {
        // update duration
        int currentPositionDuration = intent.getIntExtra(KEY_CURRENT_MUSIC_POSITION, 0);
        int duration = intent.getIntExtra(KEY_MUSIC_DURATION, 0);

        if (!isSetMax) {
            if (duration != 0) {
                circularSeekBar.setMax(duration);
                isSetMax = true;
            }
            Log.d(TAG, "onReceive: setMax");
        }

        if (updateSeekBar) {
            Log.d(TAG, "onReceive: " + currentPositionDuration);
            if (currentPositionDuration > 0) {
                circularSeekBar.setProgress(currentPositionDuration);
            }
        }
    }

    // public class MusicPlayerReceiver extends BroadcastReceiver {
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // if (intent != null) {
    // //update duration
    // handleIntent(intent);
    // }
    // }
    // }
}