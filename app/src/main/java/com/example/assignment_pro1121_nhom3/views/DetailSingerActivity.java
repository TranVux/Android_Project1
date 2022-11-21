package com.example.assignment_pro1121_nhom3.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MusicInPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Singer;
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
    private Query nextQuery = null;
    private int amountOfQuery = 0;
    private long maxOfData = 0;
    private int currentQuery = 0;
    private int limitOfQuery = 5;
    private boolean isLoading = false;


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

        Singer receiverSinger = (Singer) getIntent().getSerializableExtra("singer");
        if (receiverSinger != null) {
            singerNameMain.setText(CapitalizeWord.CapitalizeWords(receiverSinger.getName()));
            singerNameTopBar.setText(CapitalizeWord.CapitalizeWords(receiverSinger.getName()));
            Glide.with(getApplicationContext())
                    .load(receiverSinger.getAvtUrl())
                    .centerCrop()
                    .into(singerBg);
            String tempDesc = receiverSinger.getDesc();
            if (Objects.equals(tempDesc, "null")) {
                tempDesc = "";
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
                    Glide.with(DetailSingerActivity.this).load(receiverSinger.getAvtUrl()).error(R.drawable.fallback_img)
                            .into(imageBlur);
                    Glide.with(DetailSingerActivity.this).load(receiverSinger.getAvtUrl()).error(R.drawable.fallback_img)
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
                    Toast.makeText(DetailSingerActivity.this, "Phát " + index, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onMoreClick(Music music) {

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
}