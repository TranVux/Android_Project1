package com.example.assignment_pro1121_nhom3.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.assignment_pro1121_nhom3.fragments.MostListenSongFragment;
import com.example.assignment_pro1121_nhom3.fragments.NewMusicFragment;

public class ViewPager2ForMoreMusicAdapter extends FragmentStateAdapter {
    public ViewPager2ForMoreMusicAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return NewMusicFragment.newInstance(null,null);
        }
        return MostListenSongFragment.newInstance(null,null);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
