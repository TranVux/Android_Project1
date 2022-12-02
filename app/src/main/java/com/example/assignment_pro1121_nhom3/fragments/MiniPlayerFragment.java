package com.example.assignment_pro1121_nhom3.fragments;

import static com.example.assignment_pro1121_nhom3.utils.Constants.*;
import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.*;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.example.assignment_pro1121_nhom3.views.MainActivity;
import com.example.assignment_pro1121_nhom3.views.SplashScreen;
import com.google.android.flexbox.FlexboxLayout;


public class MiniPlayerFragment extends Fragment implements View.OnClickListener {

    public static String TAG = MiniPlayerFragment.class.getSimpleName();
    private Music currentMusic;
    private ImageView musicThumbnail, btnPlay, btnAddToPlaylist;
    private TextView tvMusicName, tvSingerName;
    private int stateBtnPlay = 1; // 1: play | 0: stop
    private FlexboxLayout playerLayout;
    private SharedPreferences sharedPreferences;
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;


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
                stateBtnPlay = 0;
                btnPlay.setImageResource(R.drawable.ic_small_play);
                editor.putBoolean(KEY_STATE_IS_IDLE, true);
                editor.putBoolean(KEY_STATE_IS_PLAYING, false);
                editor.putBoolean(KEY_STATE_IS_START, true);
                break;
            }
            case MUSIC_PLAYER_STATE_PLAYING: {
                stateBtnPlay = 1;
                btnPlay.setImageResource(R.drawable.ic_small_pause);
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

    private void handleIntent(Intent intent) {
        int action = intent.getIntExtra("action", -1);
        switch (action) {
            case MUSIC_PLAYER_ACTION_PAUSE: {
                try {
                    musicPlayer.setStateMusicPlayer(MUSIC_PLAYER_STATE_IDLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case MUSIC_PLAYER_ACTION_RESUME:
            case MUSIC_PLAYER_ACTION_START: {
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
        handleStateMusicPlayer(musicPlayer);
    }
}