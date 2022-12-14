package com.example.assignment_pro1121_nhom3.fragments;

import static com.example.assignment_pro1121_nhom3.utils.Constants.*;
import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.*;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.storages.MusicPlayerStorage;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.example.assignment_pro1121_nhom3.views.MainActivity;
import com.example.assignment_pro1121_nhom3.views.SplashScreen;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;


public class MiniPlayerFragment extends Fragment implements View.OnClickListener {

    public static String TAG = MiniPlayerFragment.class.getSimpleName();
    private Music currentMusic;
    private ImageView musicThumbnail, btnPlay, btnAddToPlaylist;
    private TextView tvMusicName, tvSingerName;
    private int stateBtnPlay = 1; // 1: play | 0: stop
    private FlexboxLayout playerLayout;
    private SharedPreferences sharedPreferences, musicSharePreferences;
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;
    AnimatedVectorDrawable avd1;
    AnimatedVectorDrawableCompat avd;

    public static MiniPlayerFragment newInstance(Music music) {
        MiniPlayerFragment fragment = new MiniPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_MUSIC, music);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentMusic = (Music) getArguments().getSerializable(Constants.KEY_MUSIC);
        }
        sharedPreferences = requireContext().getSharedPreferences("music_player_state", Context.MODE_PRIVATE);
        musicSharePreferences = requireContext().getSharedPreferences("music_player", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mini_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        handlePlayer(currentMusic);
        setClick();
        initStateButton();
    }

    public void setClick() {
        btnPlay.setOnClickListener(this);
        btnAddToPlaylist.setOnClickListener(this);
        playerLayout.setOnClickListener(this);
    }

    private void init(View view) {
        musicThumbnail = view.findViewById(R.id.songImg);
        btnPlay = view.findViewById(R.id.btnPause);
        btnAddToPlaylist = view.findViewById(R.id.btnAddToPlaylist);
        tvMusicName = view.findViewById(R.id.musicName);
        tvSingerName = view.findViewById(R.id.singerName);
        playerLayout = view.findViewById(R.id.playerLayout);

        tvMusicName.setSelected(true);
    }

    public void handleButtonPlay() {
        if (stateBtnPlay == 0) {
            startServiceMusic(currentMusic, MUSIC_PLAYER_ACTION_RESUME, musicPlayer.getCurrentMode());
        } else {
            startServiceMusic(currentMusic, MUSIC_PLAYER_ACTION_PAUSE, musicPlayer.getCurrentMode());
        }
    }

    public void startServiceMusic(Music music, int action, String mode) {
        Intent serviceMusic = new Intent(requireContext(), MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        serviceMusic.putExtra(KEY_MODE_MUSIC_PLAYER, mode);
        requireContext().startService(serviceMusic);
    }

    public void handlePlayer(Music music) {
        Glide.with(this).load(music.getThumbnailUrl()).apply(new RequestOptions().override(150, 150)).into(musicThumbnail);
        tvSingerName.setText(CapitalizeWord.CapitalizeWords(music.getSingerName()));
        tvMusicName.setText(CapitalizeWord.CapitalizeWords(music.getName()));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPause: {
                handleButtonPlay();
                break;
            }
            case R.id.btnAddToPlaylist: {
                BottomSheet bottomSheet = BottomSheet.newInstance(currentMusic);
                bottomSheet.show(getParentFragmentManager(), "BottomSheetFragmentPlayer");
                break;
            }
            default:
                Log.d(TAG, "onClick: invalid view");
                startActivity(new Intent(requireContext(), MainActivity.class));
                break;
        }
    }

    public void handleStateMusicPlayer(MusicPlayer musicPlayer) {
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        switch (musicPlayer.getStateMusicPlayer()) {
            case MUSIC_PLAYER_STATE_IDLE: {
                editor.putBoolean(KEY_STATE_IS_IDLE, true);
                editor.putBoolean(KEY_STATE_IS_PLAYING, false);
                editor.putBoolean(KEY_STATE_IS_START, true);
                break;
            }
            case MUSIC_PLAYER_STATE_PLAYING: {
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

    @SuppressLint("UseCompatLoadingForDrawables")
    public void initStateButton() {
        switch (musicPlayer.getStateMusicPlayer()) {
            case MUSIC_PLAYER_STATE_IDLE: {
                stateBtnPlay = 0;
                btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.avd_stop_to_play));
                Drawable drawable = btnPlay.getDrawable();
                if (drawable instanceof AnimatedVectorDrawableCompat) {
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                } else if (drawable instanceof AnimatedVectorDrawable) {
                    avd1 = (AnimatedVectorDrawable) drawable;
                    avd1.start();
                }
                break;
            }
            case MUSIC_PLAYER_STATE_PLAYING: {
                stateBtnPlay = 1;
                btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.avd_play_to_stop));
                Drawable drawable = btnPlay.getDrawable();
                if (drawable instanceof AnimatedVectorDrawableCompat) {
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                } else if (drawable instanceof AnimatedVectorDrawable) {
                    avd1 = (AnimatedVectorDrawable) drawable;
                    avd1.start();
                }
                break;
            }
            default:
                break;
        }
    }

    private final BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                handleIntent(intent);
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(musicReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(MUSIC_PLAYER_EVENT);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(musicReceiver, intentFilter);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void handleIntent(Intent intent) {
        int action = intent.getIntExtra("action", -1);
        switch (action) {
            case MUSIC_PLAYER_ACTION_PAUSE: {
                stateBtnPlay = 0;
                btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.avd_stop_to_play));
                Drawable drawable = btnPlay.getDrawable();
                if (drawable instanceof AnimatedVectorDrawableCompat) {
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                } else if (drawable instanceof AnimatedVectorDrawable) {
                    avd1 = (AnimatedVectorDrawable) drawable;
                    avd1.start();
                }
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_IDLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case MUSIC_PLAYER_ACTION_RESUME:
            case MUSIC_PLAYER_ACTION_START: {
                stateBtnPlay = 1;
                btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.avd_play_to_stop));
                Drawable drawable = btnPlay.getDrawable();
                if (drawable instanceof AnimatedVectorDrawableCompat) {
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                } else if (drawable instanceof AnimatedVectorDrawable) {
                    avd1 = (AnimatedVectorDrawable) drawable;
                    avd1.start();
                }
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_PLAYING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                handlePlayer(musicPlayer.getCurrentSong());
                break;
            }
            default:
                break;
        }
        handleStateMusicPlayer(musicPlayer);
        saveCurrentMusic(musicPlayer, MusicPlayerStorage.getInstance(requireContext()).getString(KEY_ID_OF_PLAYLIST, KEY_TOP_10));
    }

    public boolean checkUniqueSong(String songName) {
        ArrayList<Music> list = (ArrayList<Music>) SongRecentDatabase.getInstance(requireContext().getApplicationContext()).musicRecentDAO().checkSong(songName);
        return list.size() <= 0;
    }

    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist) {
        SharedPreferences.Editor editor = MusicPlayerStorage.getInstance(requireContext()).edit();
        editor.putInt(KEY_SONG_INDEX, musicPlayer.getCurrentIndexSong());
        Log.d(TAG, "saveCurrentMusic: " + idPlaylist);
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        editor.apply();
    }
}