package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MyPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.fragments.BottomSheet;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

public class DetailPlaylistActivity extends AppCompatActivity {

    public static final String TAG = DetailPlaylistActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private MyPlaylistAdapter adapter;
    ImageView imageForeground, thumbnailPlaylist, btnBack;
    NestedScrollView nestedScrollView;
    Playlist tempPlaylist;
    private PlaylistDAO playlistDAO = new PlaylistDAO();
    ProgressBar progressBar;
    TextView tvPlaylistName, topBarPlaylistName, tvCreatorName, amountOfSong;
    CircleImageView avtCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        //Đổi màu status bar nè
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tempPlaylist = (Playlist) bundle.getSerializable("playlist");
        }

        setContentView(R.layout.activity_detail_playlist);
        recyclerView = findViewById(R.id.recyclerView);
        imageForeground = findViewById(R.id.imageForeground);
        nestedScrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);
        thumbnailPlaylist = findViewById(R.id.imageView);
        tvCreatorName = findViewById(R.id.tvCreatorName);
        tvPlaylistName = findViewById(R.id.tvPlaylistName);
        topBarPlaylistName = findViewById(R.id.tvNamePlaylistTopBar);
        avtCreator = findViewById(R.id.avtCreator);
        amountOfSong = findViewById(R.id.amountOfSong);
        btnBack = findViewById(R.id.backBtn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        adapter = new MyPlaylistAdapter(this, new MyPlaylistAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music) {
                Toast.makeText(DetailPlaylistActivity.this, "Tới activity player", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoreButtonClick(Music music) {
                BottomSheet bottomSheet = new BottomSheet();
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });

        Log.d(TAG, "onCreate: " + getNavigationBarHeight());
        setMargins(nestedScrollView, 0, 0, 0, getNavigationBarHeight() - 5);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        if (tempPlaylist != null) {
            playlistDAO.getMusicInPlaylist(tempPlaylist.getId(), new PlaylistDAO.ReadMusicInPlaylist() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onReadSuccess(ArrayList<Music> listMusic) {
                    Log.d(TAG, "onReadSuccess: " + listMusic.toString());
                    adapter.setData(listMusic);
                    amountOfSong.setText(listMusic.size() + " bài");
                }

                @Override
                public void onReadFailure(Exception e) {
                    Log.d(TAG, "onReadFailure: lỗi lấy dự liệu nè ba");
                }
            }, new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            });
            // gắn ảnh cho playlist
            Glide.with(DetailPlaylistActivity.this).asBitmap().load(tempPlaylist.getUrlThumbnail()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Blurry.with(DetailPlaylistActivity.this)
                            .sampling(8)
                            .radius(30)
                            .async()
                            .from(resource).into(imageForeground);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    Blurry.with(DetailPlaylistActivity.this)
                            .sampling(8)
                            .radius(30)
                            .async()
                            .from(BitmapFactory.decodeResource(getResources(), R.drawable.fallback_img)).into(imageForeground);
                }
            });
            //gắn ảnh thumbnail cho playlist
            Glide.with(DetailPlaylistActivity.this)
                    .load(tempPlaylist.getUrlThumbnail())
                    .apply(new RequestOptions().override(140, 140))
                    .into(thumbnailPlaylist);

            //gán tên playlist, người tạo,...
            tvPlaylistName.setText(CapitalizeWord.CapitalizeWords(tempPlaylist.getName()));
            tvCreatorName.setText(CapitalizeWord.CapitalizeWords(tempPlaylist.getCreatorName()));
            topBarPlaylistName.setText(CapitalizeWord.CapitalizeWords(tempPlaylist.getName()));
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public int getNavigationBarHeight() {
        boolean hasMenuKey = ViewConfiguration.get(DetailPlaylistActivity.this).hasPermanentMenuKey();
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}