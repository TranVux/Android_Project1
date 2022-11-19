package com.example.assignment_pro1121_nhom3.fragments;

import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.*;
import static com.example.assignment_pro1121_nhom3.utils.Constants.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.example.assignment_pro1121_nhom3.views.MainActivity;
import com.example.assignment_pro1121_nhom3.views.SplashScreen;

import java.util.ArrayList;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class PlayerFragment extends Fragment implements View.OnClickListener {

    public static String TAG = PlayerFragment.class.getSimpleName();
    // BlurView sẽ làm mở những đối tượng ở ngoài phạm vi của nó
    BlurView backgroundFragment;

    //layout chính
    CoordinatorLayout parentLayout;

    //Nút thêm dùng để mở lên danh sách bài hát tiếp theo
    TextView labelMoreListMusic, musicName, singerName, singerNameNext, musicNameNext, labelViewNextMusic, labelViewCurrentMusic;
    ImageView icMoreListMusic, imageMusicThumbnail, backgroundImage, btnNext, btnPrev, btnAddToPlayList, imageThumbnailNextMusic;
    SeekBar timeLine;

    //playlist hiện tại
    ArrayList<Music> playListMusic;

    // music player hiện tại
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;

    //lấy dữ liệu cần thiết

    public static PlayerFragment newInstance(ArrayList<Music> playListMusic) {
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("playlist", playListMusic);

        playerFragment.setArguments(bundle);
        return playerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // thiết lập thuộc tính ban đầu, ánh xạ view
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("music_player", Context.MODE_PRIVATE);
        init(view);
        //
        musicPlayer.setMusicAtPosition(sharedPreferences.getInt(KEY_SONG_INDEX, 0));
        handleRotateImageThumbnail();
        //Player hiện tại

        Log.d(TAG, "onViewCreated: " + musicPlayer.getCurrentSong().getName());
        Log.d(TAG, "onViewCreated: " + musicPlayer.getSizeOfPlayList());
        setContentInit(musicPlayer.getCurrentSong());
        setContentForNextMusic(musicPlayer.getNextSong());

        // bắt sự kiện click cho view
        setEventClick();
        //

        //tạo background blur cho fragment
        blurBackgroundFragment();
    }

    public void handleRotateImageThumbnail() {
        if (Objects.equals(musicPlayer.getStateMusicPlayer(), MUSIC_PLAYER_STATE_PLAYING)) {
            Log.d(TAG, "handleRotateImageThumbnail: run");
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    imageMusicThumbnail.animate().rotationBy(360).withEndAction(this).setDuration(10000).setInterpolator(new LinearInterpolator()).start();
                }
            };
            imageMusicThumbnail.animate().rotationBy(360).withEndAction(runnable).setDuration(10000).setInterpolator(new LinearInterpolator()).start();
        } else {
            Log.d(TAG, "handleRotateImageThumbnail: cancel");
            imageMusicThumbnail.animate().cancel();
        }
    }

    public void setContentInit(Music music) {
        TransitionManager.beginDelayedTransition(parentLayout, new AutoTransition());
        Glide.with(requireContext()).load(music.getThumbnailUrl()).placeholder(backgroundImage.getDrawable()).into(backgroundImage);
        Glide.with(requireContext()).load(music.getThumbnailUrl()).placeholder(imageMusicThumbnail.getDrawable()).into(imageMusicThumbnail);
        musicName.setText(CapitalizeWord.CapitalizeWords(music.getName()));
        singerName.setText(CapitalizeWord.CapitalizeWords(music.getSingerName()));
        labelViewCurrentMusic.setText(String.valueOf(music.getViews()));
    }

    public void setContentForNextMusic(Music nextMusic) {
        TransitionManager.beginDelayedTransition(parentLayout, new AutoTransition());
        Glide.with(requireContext()).load(nextMusic.getThumbnailUrl()).placeholder(imageThumbnailNextMusic.getDrawable()).into(imageThumbnailNextMusic);
        musicNameNext.setText(CapitalizeWord.CapitalizeWords(nextMusic.getName()));
        singerNameNext.setText(CapitalizeWord.CapitalizeWords(nextMusic.getSingerName()));
        labelViewNextMusic.setText(String.valueOf(nextMusic.getViews()));
    }

    private void init(View view) {
        parentLayout = view.findViewById(R.id.playerLayout);
        backgroundImage = view.findViewById(R.id.backgroundImage);
        backgroundFragment = view.findViewById(R.id.blurView);
        labelMoreListMusic = view.findViewById(R.id.moreLabel);
        musicName = view.findViewById(R.id.musicName);
        singerName = view.findViewById(R.id.singerName);
        imageMusicThumbnail = view.findViewById(R.id.imageMusicThumbnail);
        timeLine = view.findViewById(R.id.timeLine);
        icMoreListMusic = view.findViewById(R.id.icMoreMusic);
        btnAddToPlayList = view.findViewById(R.id.btnAddToPlaylist);
        btnNext = view.findViewById(R.id.btnSkipToNext);
        btnPrev = view.findViewById(R.id.btnSkipToPrev);
        singerNameNext = view.findViewById(R.id.nextSingerName);
        musicNameNext = view.findViewById(R.id.nextMusicName);
        labelViewNextMusic = view.findViewById(R.id.labelNextMusicView);
        imageThumbnailNextMusic = view.findViewById(R.id.imageThumbnailNextMusic);
        labelViewCurrentMusic = view.findViewById(R.id.view);
    }

    public void setEventClick() {
        labelMoreListMusic.setOnClickListener(this);
        icMoreListMusic.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnAddToPlayList.setOnClickListener(this);
    }

    private void blurBackgroundFragment() {
        // làm mở ảnh
        float radius = 15f;
        View decorView = requireActivity().getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        backgroundFragment.setupWith(rootView, new RenderScriptBlur(requireContext())) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);
        //
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        ((MainActivity) requireActivity()).toTransparent();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    public void handleShowBottomSheet() {
        //thêm dữ liệu cho listPlaylist

        MyBottomSheetDialog bottomSheetDialog = MyBottomSheetDialog.newInstance(musicPlayer.getPlayListMusic());
        bottomSheetDialog.show(getParentFragmentManager(), "MyBottomSheet");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.moreLabel:
            case R.id.icMoreMusic: {
                handleShowBottomSheet();
                break;
            }
            case R.id.btnAddToPlaylist: {
                BottomSheet bottomSheet = BottomSheet.newInstance(musicPlayer.getCurrentSong());
                bottomSheet.show(getParentFragmentManager(), TAG);
                break;
            }
            case R.id.btnSkipToNext: {
                Log.d(TAG, "onClick: Next");
                nextMusic();
                break;
            }
            case R.id.btnSkipToPrev: {
                Log.d(TAG, "onClick: Prev");
                previousMusic();
                break;
            }
            default: {
                break;
            }
        }
    }

    public void nextMusic() {
        Intent nextIntent = new Intent(requireContext(), MusicPlayerService.class);
        nextIntent.putExtra("action", MUSIC_PLAYER_ACTION_NEXT);
        Log.d(TAG, "nextMusic: " + musicPlayer.getCurrentSong().getName());
        requireContext().startService(nextIntent);
    }

    public void previousMusic() {
        Intent previousIntent = new Intent(requireContext(), MusicPlayerService.class);
        previousIntent.putExtra("action", MUSIC_PLAYER_ACTION_PREVIOUS);
        Log.d(TAG, "preMusic: " + musicPlayer.getCurrentSong().getName());
        requireContext().startService(previousIntent);
    }
}