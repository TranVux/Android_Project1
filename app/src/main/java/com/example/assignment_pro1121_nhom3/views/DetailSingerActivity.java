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
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_TOP_10;
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_SINGER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MusicInPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.dao.SingerDAO;
import com.example.assignment_pro1121_nhom3.fragments.BottomSheet;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class DetailSingerActivity extends AppCompatActivity {

    public static final String TAG = DetailSingerActivity.class.getSimpleName();

    private TextView singerNameTopBar, singerNameMain, info, btnLoadMore, amountOfSong;
    private ProgressBar progressBar;
    private LinearLayout playAll;
    private ImageView singerBg;
    private RecyclerView rclMusicSuggest;
    private MusicInPlaylistAdapter musicInPlaylistAdapter;
    private ArrayList<Music> listMusicOfSinger;
    private MusicDAO musicDAO;
    private SingerDAO singerDAO;
    private Query nextQuery = null;
    private int amountOfQuery = 0;
    private long maxOfData = 0;
    private int currentQuery = 0;
    private int limitOfQuery = 20;
    private boolean isLoading = false;
    Singer receiverSinger;
    public MusicPlayer musicPlayer = SplashScreen.musicPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_singer);
        singerNameTopBar = findViewById(R.id.singerNameTopBar);
        singerNameMain = findViewById(R.id.singerNameMain);
        singerBg = findViewById(R.id.singerThumbnail);
        playAll = findViewById(R.id.buttonPlayAll);
        info = findViewById(R.id.info);
        rclMusicSuggest = findViewById(R.id.listMusicOfSinger);
        btnLoadMore = findViewById(R.id.btnLoadMore);
        amountOfSong = findViewById(R.id.amountOfSong);
        progressBar = findViewById(R.id.progressBar);

        //
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        receiverSinger = (Singer) getIntent().getSerializableExtra("singer");

        if (receiverSinger == null) {
            String receiverSingerID = getIntent().getStringExtra("singerID");
            singerDAO = new SingerDAO();
            singerDAO.getSinger(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {

                }
            }, receiverSingerID, new SingerDAO.ReadItemSinger() {
                @Override
                public void onReadItemSingerCallback(Singer singer) {
                    receiverSinger = singer;
                    handleLayoutSingerDetail();
                }
            });
        } else {
            handleLayoutSingerDetail();
        }


    }

    public void handleLayoutSingerDetail() {
        singerNameMain.setText(CapitalizeWord.CapitalizeWords(receiverSinger.getName()));
        singerNameTopBar.setText(CapitalizeWord.CapitalizeWords(receiverSinger.getName()));
        Glide.with(getApplicationContext())
                .load(receiverSinger.getAvtUrl())
                .centerCrop()
                .into(singerBg);
        String tempDesc = receiverSinger.getDesc();
        if (Objects.equals(tempDesc, "null")) {
            tempDesc = "";
        } else {

        }
        info.setText(tempDesc);

        // lấy dữ liệu danh sách bài hát của ca sĩ hiện tại
        musicDAO = new MusicDAO();
        musicDAO.getCountDocumentMusic(new MusicDAO.GetCountDocument() {
            @Override
            public void onGetCountSuccess(long count) {
                maxOfData = count;
                Log.d(TAG, "onGetCountSuccess: " + count);
                if (maxOfData % limitOfQuery != 0) {
                    amountOfQuery = (int) Math.round(Math.ceil(maxOfData / limitOfQuery)) + 1;
                } else {
                    amountOfQuery = (int) (maxOfData / limitOfQuery);
                }
                Log.d(TAG, "onGetCountSuccess: " + " count: " + count + " limitQuery: " + limitOfQuery + " amount of query: " + amountOfQuery);
                getData(receiverSinger.getId());
            }
        }, receiverSinger.getId());


        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToPlayer(receiverSinger, 0);
            }
        });

        String finalTempDesc = tempDesc;
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogLayout = LayoutInflater.from(DetailSingerActivity.this).inflate(R.layout.layout_detail_desc_singer, null);
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(DetailSingerActivity.this);
                dialogBuilder.setView(dialogLayout);
                ImageView imageBlur, imageAvtSinger;
                TextView singerName, contentDesc;
                BlurView blurView;

                blurView = dialogLayout.findViewById(R.id.blurView);
                imageBlur = dialogLayout.findViewById(R.id.blurImage);
                imageAvtSinger = dialogLayout.findViewById(R.id.imageSinger);
                singerName = dialogLayout.findViewById(R.id.singerName);
                contentDesc = dialogLayout.findViewById(R.id.contentDesc);

                blurBackgroundFragment(blurView);
                Glide.with(DetailSingerActivity.this).load(receiverSinger.getAvtUrl()).placeholder(R.drawable.placeholder_img).error(R.drawable.fallback_img)
                        .into(imageBlur);
                Glide.with(DetailSingerActivity.this).load(receiverSinger.getAvtUrl()).placeholder(R.drawable.placeholder_img).error(R.drawable.fallback_img)
                        .into(imageAvtSinger);
                singerName.setText(receiverSinger.getName());
                contentDesc.setText(finalTempDesc);
                Dialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        // xử lý danh sách nhạc của ca sĩ
        musicInPlaylistAdapter = new MusicInPlaylistAdapter(listMusicOfSinger, DetailSingerActivity.this, new ItemEvent.MusicItemInPlayListEvent() {
            @Override
            public void onItemClick(int index) {
                navigateToPlayer(receiverSinger, index);
            }

            @Override
            public void onMoreClick(Music music) {
                BottomSheet bottomSheet = BottomSheet.newInstance(music);
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(DetailSingerActivity.this);
        rclMusicSuggest.setLayoutManager(manager);
        rclMusicSuggest.setAdapter(musicInPlaylistAdapter);

        // xử lý load more cho recyclerview
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoading) {
                    getData(receiverSinger.getId());
                    isLoading = true;
                }
            }
        });
    }

    public void navigateToPlayer(Singer receiverSinger, int position) {
        if (musicInPlaylistAdapter.getItemCount() > 0) {
            musicPlayer.pauseSong(musicPlayer.getCurrentPositionSong());
            musicPlayer.clearPlaylist();
            musicPlayer.setPlayList(musicInPlaylistAdapter.getListMusic());
            musicPlayer.setMusicAtPosition(position);
            try {
                musicPlayer.setStateMusicPlayer(MusicPlayer.MUSIC_PLAYER_STATE_PLAYING);
            } catch (Exception e) {
                e.printStackTrace();
            }
            musicPlayer.setCurrentMode(MusicPlayer.MUSIC_PLAYER_MODE_ONLINE);
            saveCurrentMusic(musicPlayer, receiverSinger.getId(), PLAYLIST_TYPE_SINGER);
            Log.d(TAG, "onClick: " + musicPlayer.getStateMusicPlayer());
            startActivity(new Intent(DetailSingerActivity.this, MainActivity.class));
            startServiceMusic(musicPlayer.getCurrentSong(), MusicPlayer.MUSIC_PLAYER_ACTION_RESET_SONG, musicPlayer.getCurrentMode());
        }
    }

    public void getData(String singerID) {
        musicDAO.getMusicBySingerId(nextQuery, singerID, new MusicDAO.GetSingerByID() {
            @Override
            public void onGetSuccess(ArrayList<Music> list) {
                Log.d(TAG, "onGetSuccess: " + list.size());
                musicInPlaylistAdapter.setListMusic(list);
                amountOfSong.setText(list.size() + " bài");
                currentQuery++;
            }

            @Override
            public void getNextQuery(Query query) {
                nextQuery = query;
            }
        }, new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {
                progressBar.setVisibility(View.VISIBLE);
                if (currentQuery == 0) {
                    btnLoadMore.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterGetData() {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "afterGetData: " + "currentQuery: " + currentQuery + " amountOfQuery: " + amountOfQuery);
                if (currentQuery >= amountOfQuery) {
                    btnLoadMore.setVisibility(View.GONE);
                } else {
                    btnLoadMore.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void blurBackgroundFragment(BlurView blurView) {
        // làm mở ảnh
        float radius = 15f;
        View decorView = DetailSingerActivity.this.getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView, new RenderScriptBlur(DetailSingerActivity.this)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);
        //
    }


    public void startServiceMusic(Music music, int action, String mode) {
        Intent serviceMusic = new Intent(DetailSingerActivity.this, MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        serviceMusic.putExtra(KEY_MODE_MUSIC_PLAYER, mode);
        startService(serviceMusic);
    }

    public boolean checkUniqueSong(String songName) {
        ArrayList<Music> list = (ArrayList<Music>) SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().checkSong(songName);
        return list.size() <= 0;
    }


    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist, String typePlayList) {
        if (checkUniqueSong(musicPlayer.getCurrentSong().getName())) {
            SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().insertSong(musicPlayer.getCurrentSong());
        }
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
        editor.putString(KEY_PLAYLIST_TYPE, typePlayList);
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        Log.d(TAG, "saveCurrentMusic: " + idPlaylist);
        Log.d(TAG, "saveCurrentMusic: " + typePlayList);
        editor.apply();
    }
}