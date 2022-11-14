package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.fragments.HomeFragment;
import com.example.assignment_pro1121_nhom3.fragments.PlayerFragment;
import com.example.assignment_pro1121_nhom3.fragments.UserFragment;
import com.example.assignment_pro1121_nhom3.interfaces.HandleChangeColorBottomNavigation;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.EventMusicPlayerService;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import static com.example.assignment_pro1121_nhom3.utils.Constants.*;
import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HandleChangeColorBottomNavigation {
    BottomNavigationView bottomNavigation;
    int state = 0;
    HomeFragment homeFragment;
    UserFragment userFragment;
    boolean agreeBack = false;
    private boolean doubleBackToExitPressedOnce = false;
    ArrayList<Music> listTop10Music = new ArrayList<>();

    //Music player
    public static MusicPlayer musicPlayer = MusicPlayer.getInstance(null);
    //

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

        //
        // bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setItemIconTintList(null);
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(1);
        View customRadio = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.custom_item_bottom_navigation_player, bottomNavigationMenuView, false);
        View customButtonPlay = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.custom_item_bottom_navigation_button_play, bottomNavigationMenuView, false);
        itemView.addView(customRadio);
        itemView.addView(customButtonPlay);
        customButtonPlay.setVisibility(View.GONE);
        ImageView player = customRadio.findViewById(R.id.player);
        ImageView btnPlay = customButtonPlay.findViewById(R.id.btnPlay);

        player.setOnClickListener(new View.OnClickListener() {
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
                if (state == 1) {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    state = 0;
                } else {
                    btnPlay.setImageResource(R.drawable.ic_play);
                    state = 1;
                }
            }
        });

        // set layout mặc định cho fragment là màn hình home
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, homeFragment, "FragmentHome").commit();

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
                        player.performClick();
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


        // lấy dữ liệu mẫu
        MusicDAO musicDAO = new MusicDAO();
        musicDAO.getTopMusic10(new MusicDAO.GetTop10Listener() {
            @Override
            public void onGetTop10Callback(ArrayList<Music> list) {
                listTop10Music = list;
                musicPlayer.setPlayList(listTop10Music);
                musicPlayer.start();
                Log.d(TAG, "onGetTop10Callback: has data " + musicPlayer.getCurrentSong().getName());
//                startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_START);
            }
        });

        //
    }

    public void startServiceMusic(Music music, int action) {
        Intent serviceMusic = new Intent(MainActivity.this, MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        startService(serviceMusic);
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
            case MUSIC_PLAYER_ACTION_PAUSE: {
                musicPlayer.setCurrentPositionSong(currentPositionDuration);
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_IDLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case MUSIC_PLAYER_ACTION_RESUME: {
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            default:
                break;
        }
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
}