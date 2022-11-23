package com.example.assignment_pro1121_nhom3.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.assignment_pro1121_nhom3.fragments.HomeFragment;
import com.example.assignment_pro1121_nhom3.fragments.MyPlaylistFragment;
import com.example.assignment_pro1121_nhom3.fragments.SuggestPlaylistFragment;

public class PlaylistViewPagerAdapter extends FragmentStatePagerAdapter {
    public PlaylistViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyPlaylistFragment();

            case 1:
                return new SuggestPlaylistFragment();

            default:
                return new MyPlaylistFragment();
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "MyPlaylist";
                break;

            case 1:
                title = "SuggestPlaylist";
        }
        return title;
    }
}
