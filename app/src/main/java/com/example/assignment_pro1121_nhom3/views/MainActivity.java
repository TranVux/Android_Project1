package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.fragments.HomeFragment;
import com.example.assignment_pro1121_nhom3.fragments.PlayerFragment;
import com.example.assignment_pro1121_nhom3.fragments.UserFragment;
import com.example.assignment_pro1121_nhom3.interfaces.HandleChangeColorBottomNavigation;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import static com.example.assignment_pro1121_nhom3.utils.Constants.*;
import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.*;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements HandleChangeColorBottomNavigation {
    public static final String TAG = MainActivity.class.getSimpleName();
    BottomNavigationView bottomNavigation;
    ImageView imageThumbnailCurrentMusic;

    //Trạng thái của nút play 0 là pause 1 là start/resume
    int stateButtonPlay = 0;


    PlayerFragment playerFragment;
    HomeFragment homeFragment;
    UserFragment userFragment;
    boolean agreeBack = false;
    private boolean doubleBackToExitPressedOnce = false;
    ArrayList<Music> listTop10Music = new ArrayList<>();
    boolean isPlaying = false, isStart = false, isDestroy = false, isCreated = false;
    public static boolean isPause = true;

    ImageView btnPlay;

    //Music player
    public static MusicPlayer musicPlayer;
    //

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // đổi màu của status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        //

        // khai báo các fragment
        homeFragment = new HomeFragment(this);
        userFragment = new UserFragment(this);
//        playerFragment = new PlayerFragment(this);
        setContentView(R.layout.activity_main);


        //Lưu trạng thái có đang chơi nhạc hay không
        sharedPreferences = getSharedPreferences("music_player", MODE_PRIVATE);
        isPlaying = sharedPreferences.getBoolean(KEY_STATE_IS_PLAYING, false);
        isStart = sharedPreferences.getBoolean(KEY_STATE_IS_START, false);
        isCreated = sharedPreferences.getBoolean(KEY_STATE_IS_CREATED, true);
        isDestroy = sharedPreferences.getBoolean(KEY_STATE_IS_DESTROYED, false);

        if (getIntent().getExtras() != null) {
            listTop10Music = (ArrayList<Music>) getIntent().getSerializableExtra("fetchData");
        }

        //cài đặt music player
        musicPlayer = MusicPlayer.getInstance(listTop10Music, new MusicPlayerCallback() {
            @Override
            public void onPause() {
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                assert playerFragment != null;
                playerFragment.handleRotateImageThumbnail();
            }

            @Override
            public void onResume() {
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                assert playerFragment != null;
                playerFragment.handleRotateImageThumbnail();
            }

            @Override
            public void updateSeekBar(int currentPositionSong) {

            }
        });

        //

        // bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setItemIconTintList(null);
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(1);

        View customRadio = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.custom_item_bottom_navigation_player, bottomNavigationMenuView, false);
        imageThumbnailCurrentMusic = customRadio.findViewById(R.id.imageThumbnailCurrentMusic);

        View customButtonPlay = LayoutInflater.from(getApplicationContext())
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
                        .replace(R.id.fragmentLayout, PlayerFragment.newInstance(listTop10Music), "PlayerFragment").commit();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateButtonPlay == 1) {
                    btnPlay.setImageResource(R.drawable.ic_play);
                    stopMusicPlayer();
                    stateButtonPlay = 0;
                } else {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    playMusicPlayer();
                    stateButtonPlay = 1;
                }
            }
        });

        // set layout mặc định cho fragment là màn hình home
        bottomNavigation.getMenu().getItem(1).setChecked(true);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, PlayerFragment.newInstance(listTop10Music), "PlayerFragment").commit();

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

        // xử lý player state
        musicPlayer.setInitState(isPlaying, isStart, isDestroy, isCreated);
        musicPlayer.start();
        handleStateMusicPlayer(musicPlayer);
        Log.d(TAG, "onCreate: save state: " + isCreated + " " + isPlaying + " " + isStart + " " + isDestroy);
        Log.d(TAG, "playMusicPlayer: " + musicPlayer.getStateMusicPlayer());
        //

        // handle thumbnail của player
        Glide.with(MainActivity.this)
                .load(musicPlayer.getCurrentSong().getThumbnailUrl())
                .apply(new RequestOptions().override(45, 45))
                .into(imageThumbnailCurrentMusic);
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

    private final BroadcastReceiver mediaPlayerServiceBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                handleIntent(intent);
            }
        }
    };

    private void handleIntent(Intent intent) {
        int action = intent.getIntExtra("action", -1);
        int currentPositionDuration = intent.getIntExtra(KEY_CURRENT_MUSIC_POSITION, 0);
        switch (action) {
            case MUSIC_PLAYER_ACTION_START: {
                try {
                    musicPlayer.resumeSong();
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handleStateMusicPlayer(musicPlayer);
                break;
            }
            case MUSIC_PLAYER_ACTION_PAUSE: {
                musicPlayer.pauseSong(currentPositionDuration);
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_IDLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handleStateMusicPlayer(musicPlayer);
                break;
            }
            case MUSIC_PLAYER_ACTION_RESUME: {
                musicPlayer.resumeSong();
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handleStateMusicPlayer(musicPlayer);
                break;
            }
            case MUSIC_PLAYER_ACTION_NEXT: {
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                musicPlayer.nextSong(musicPlayer.getCurrentIndexSong());
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                assert playerFragment != null;
                playerFragment.setContentForNextMusic(musicPlayer.getNextSong());
                playerFragment.setContentInit(musicPlayer.getCurrentSong());
                handleChangeMusic();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_RESET_SONG);
                    }
                }, 500);
                handleStateMusicPlayer(musicPlayer);
                break;
            }
            case MUSIC_PLAYER_ACTION_PREVIOUS: {
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                musicPlayer.previousSong(musicPlayer.getCurrentIndexSong());
                playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                assert playerFragment != null;
                playerFragment.setContentForNextMusic(musicPlayer.getNextSong());
                playerFragment.setContentInit(musicPlayer.getCurrentSong());
                handleChangeMusic();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_RESET_SONG);
                    }
                }, 500);
                handleStateMusicPlayer(musicPlayer);
                break;
            }
            case MUSIC_PLAYER_ACTION_COMPLETE: {
                musicPlayer.nextSong(musicPlayer.getCurrentIndexSong());
                autoNextMusic();
                break;
            }
            default:
                break;
        }
        Log.d(TAG, "playMusicPlayer: " + musicPlayer.getStateMusicPlayer());
    }

    public void playMusicPlayer() {
        if (Objects.equals(musicPlayer.getStateMusicPlayer(), MUSIC_PLAYER_STATE_IDLE)) {
            startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_RESUME);
        } else {
            startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_START);
        }
    }

    public void stopMusicPlayer() {
        if (Objects.equals(musicPlayer.getStateMusicPlayer(), MUSIC_PLAYER_STATE_PLAYING)) {
            startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_PAUSE);
        }
    }

    public void handleStateMusicPlayer(MusicPlayer musicPlayer) {
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (musicPlayer.getStateMusicPlayer()) {
            case MUSIC_PLAYER_STATE_IDLE: {
                stateButtonPlay = 0;
                btnPlay.setImageResource(R.drawable.ic_play);
                editor.putBoolean(KEY_STATE_IS_IDLE, true);
                editor.putBoolean(KEY_STATE_IS_PLAYING, false);
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

    public void autoNextMusic() {
        Intent nextIntent = new Intent(MainActivity.this, MusicPlayerService.class);
        nextIntent.putExtra("action", MUSIC_PLAYER_ACTION_NEXT);
        nextIntent.putExtra(Constants.KEY_MUSIC, musicPlayer.getCurrentSong());
        Log.d(TAG, "nextMusic: " + musicPlayer.getCurrentSong().getName());
        startService(nextIntent);
    }

    public void handleChangeMusic() {
        Glide.with(MainActivity.this)
                .load(musicPlayer.getCurrentSong().getThumbnailUrl())
                .apply(new RequestOptions().override(45, 45))
                .into(imageThumbnailCurrentMusic);
    }


    public void startServiceMusic(Music music, int action) {
        Intent serviceMusic = new Intent(MainActivity.this, MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        startService(serviceMusic);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter musicServiceIntentFilter = new IntentFilter(MUSIC_PLAYER_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mediaPlayerServiceBroadcast, musicServiceIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mediaPlayerServiceBroadcast);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_STATE_IS_PLAYING, false);
        editor.putBoolean(KEY_STATE_IS_START, false);
        editor.putBoolean(KEY_STATE_IS_DESTROYED, true);
        editor.putBoolean(KEY_STATE_IS_IDLE, false);
        editor.putBoolean(KEY_STATE_IS_CREATED, true);
        editor.apply();
    }
}