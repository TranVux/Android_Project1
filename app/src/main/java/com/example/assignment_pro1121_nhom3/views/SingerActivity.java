package com.example.assignment_pro1121_nhom3.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.ViewPagerForSingerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.transition.MaterialElevationScale;

public class SingerActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerForSingerAdapter viewPagerForSingerAdapter;
    public static final String TAG = SingerActivity.class.getSimpleName();
    int currentFontSizeTabLayout1 = 25;
    int currentFontSizeTabLayout2 = 15;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer);
        tabLayout = findViewById(R.id.tabBar);
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);

        //tạo adapter cho viewPager
        viewPagerForSingerAdapter = new ViewPagerForSingerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerForSingerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSmoothScrollingEnabled(true);
        //custom layout cho tabLayout
        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int offset = (int) (Math.round(Math.ceil(positionOffset * 15)));
                assert tab2 != null;
                assert tab1 != null;
                if (offset <= 0 || offset > 10) {
                    //xử lý phần chữ của tablayout
                    setTabTextSize(tab1, (currentFontSizeTabLayout1 - 10));
                    setTabTextSize(tab2, (currentFontSizeTabLayout2 + 10));
                }
                if (position == 0) {
                    if ((currentFontSizeTabLayout1 - offset) >= 15) {
//                        Log.d(TAG, "onPageScrolled: " + (currentFontSizeTabLayout1 - offset) + " " + (currentFontSizeTabLayout2 + offset));
                        setTabTextSize(tab1, (currentFontSizeTabLayout1 - offset));
                        setTabTextSize(tab2, (currentFontSizeTabLayout2 + offset));
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        // set item cho tablayout
        assert tab1 != null;
        tab1.setCustomView(createCustomTabView("Top Lượt Nghe", currentFontSizeTabLayout1));
        assert tab2 != null;
        tab2.setCustomView(createCustomTabView("Tất cả", currentFontSizeTabLayout2));

        //xử lý nút back cho activity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setTabTextSize(TabLayout.Tab tab, int tabSizeSp) {
        View tabCustomView = tab.getCustomView();
        assert tabCustomView != null;
        TextView tabTextView = tabCustomView.findViewById(R.id.tabTV);
        tabTextView.setTextSize(tabSizeSp);
    }

    private View createCustomTabView(String tabText, int tabSizeSp) {
        View tabCustomView = getLayoutInflater().inflate(R.layout.layout_tab, null);
        TextView tabTextView = tabCustomView.findViewById(R.id.tabTV);
        tabTextView.setText(tabText);
        tabTextView.setTextSize(tabSizeSp);
        return tabCustomView;
    }
}