package com.example.assignment_pro1121_nhom3.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TableLayout;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.PlaylistViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class PlaylistActivity extends AppCompatActivity {

    private TabLayout mTableLayout;
    private PlaylistViewPagerAdapter mPlaylistViewPager;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        mTableLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        mPlaylistViewPager = new PlaylistViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(mPlaylistViewPager);
    }
}