package com.example.assignment_pro1121_nhom3.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.assignment_pro1121_nhom3.fragments.MyPlaylistFragment;
import com.example.assignment_pro1121_nhom3.fragments.SuggestPlaylistFragment;

public class PlaylistViewPagerAdapter extends FragmentStateAdapter {

    public PlaylistViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) return new MyPlaylistFragment();
        return new SuggestPlaylistFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
