package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.PlaylistViewPagerAdapter;
import com.example.assignment_pro1121_nhom3.fragments.MiniPlayerFragment;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MorePlaylistActivity extends AppCompatActivity {
    ViewPager2 viewPager;
    TabLayout tabLayout;
    ImageView btnBack;
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_playlist);
        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar, null));
        //đổi màu chữ status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //

        // ánh xạ view setup view pager và tablayout
        init();

        //setup mini player
        setUpMiniPlayer();

        //xử lý nút back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void init() {
        viewPager = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabBar);
        btnBack = findViewById(R.id.btnBack);

        PlaylistViewPagerAdapter playlistViewPagerAdapter = new PlaylistViewPagerAdapter(this);
        viewPager.setAdapter(playlistViewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Gợi Ý");
                } else if (position == 1) {
                    tab.setText("Của Tôi");
                }
            }
        }).attach();
        tabLayout.setSmoothScrollingEnabled(true);
    }

    public void setUpMiniPlayer() {
        if (musicPlayer.getCurrentSong() != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentMiniPlayer, MiniPlayerFragment.newInstance(musicPlayer.getCurrentSong()))
                    .commit();
        }
    }
}