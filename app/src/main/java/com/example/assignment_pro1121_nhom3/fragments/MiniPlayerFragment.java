package com.example.assignment_pro1121_nhom3.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.utils.Constants;


public class MiniPlayerFragment extends Fragment implements View.OnClickListener {

    public static String TAG = MiniPlayerFragment.class.getSimpleName();
    private Music currentMusic;
    private ImageView musicThumbnail, btnPlay, btnAddToPlaylist;
    private TextView tvMusicName, tvSingerName;
    private int stateBtnPlay = 1; // 1: play | 0: stop


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
    }

    private void init(View view) {
        musicThumbnail = view.findViewById(R.id.songImg);
        btnPlay = view.findViewById(R.id.btnPause);
        btnAddToPlaylist = view.findViewById(R.id.btnAddToPlaylist);
        tvMusicName = view.findViewById(R.id.musicName);
        tvSingerName = view.findViewById(R.id.singerName);
    }

    public void handleButtonPlay() {
        if (stateBtnPlay == 0) {
            stateBtnPlay = 1;
            btnPlay.setImageResource(R.drawable.ic_small_play);
        } else {
            stateBtnPlay = 0;
            btnPlay.setImageResource(R.drawable.ic_small_pause);
        }
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
                break;
        }
    }
}