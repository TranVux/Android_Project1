package com.example.assignment_pro1121_nhom3.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MyPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.fragments.BottomSheet;
import com.example.assignment_pro1121_nhom3.models.Music;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class PlaylistActivity extends AppCompatActivity {

    public static final String TAG = PlaylistActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private MyPlaylistAdapter adapter;
    ImageView imageForeground;
    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_playlist);
        recyclerView = findViewById(R.id.recyclerView);
        imageForeground = findViewById(R.id.imageForeground);
        nestedScrollView = findViewById(R.id.scrollView);

        // gắn ảnh cho playlist
        Blurry.with(PlaylistActivity.this)
                .sampling(8)
                .radius(30)
                .async()
                .from(BitmapFactory.decodeResource(getResources(), R.drawable.img)).into(imageForeground);

        adapter = new MyPlaylistAdapter(this, new MyPlaylistAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music) {
                Toast.makeText(PlaylistActivity.this, "Tới activity player", Toast.LENGTH_SHORT).show();
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
        adapter.setData(getListMusic());
        recyclerView.setAdapter(adapter);

    }

    private List<Music> getListMusic() {
        List<Music> list = new ArrayList<>();
        list.add(new Music("1", "Có chắc", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("2", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("3", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("4", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("5", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("6", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("7", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("8", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("9", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        return list;
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public int getNavigationBarHeight() {
        boolean hasMenuKey = ViewConfiguration.get(PlaylistActivity.this).hasPermanentMenuKey();
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}