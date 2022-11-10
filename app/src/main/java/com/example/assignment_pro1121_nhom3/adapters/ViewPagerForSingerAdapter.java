package com.example.assignment_pro1121_nhom3.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.assignment_pro1121_nhom3.fragments.AllSingerFragment;
import com.example.assignment_pro1121_nhom3.fragments.FragmentTopView;

public class ViewPagerForSingerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerForSingerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new AllSingerFragment();
        }
        return new FragmentTopView();
    }

    @Override
    public int getCount() {
        return 2;
    }


}
