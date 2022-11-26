package com.example.assignment_pro1121_nhom3.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.assignment_pro1121_nhom3.fragments.AllSingerFragment;
import com.example.assignment_pro1121_nhom3.fragments.FragmentTopView;

public class ViewPagerForSingerAdapter extends FragmentStateAdapter {

    public ViewPagerForSingerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new AllSingerFragment();
        }
        return new FragmentTopView();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
