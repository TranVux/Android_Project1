package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
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
import com.example.assignment_pro1121_nhom3.fragments.MiniPlayerFragment;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.transition.MaterialElevationScale;

public class SingerActivity extends AppCompatActivity {
    public static final String TAG = SingerActivity.class.getSimpleName();
    ImageView btnBack;
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer);
        TabLayout tabLayout = findViewById(R.id.tabBar);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);

        //setup music player
        setUpMiniPlayer();

        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar, null));
        //đổi màu chữ status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //
        //tạo adapter cho viewPager
        ViewPagerForSingerAdapter viewPagerForSingerAdapter = new ViewPagerForSingerAdapter(this);
        viewPager.setAdapter(viewPagerForSingerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Top Lượt Nghe");
                } else if (position == 1) {
                    tab.setText("Tất cả");
                }
            }
        }).attach();
        tabLayout.setSmoothScrollingEnabled(true);
//        //custom layout cho tabLayout
//        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
//        TabLayout.Tab tab2 = tabLayout.getTabAt(1);

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.d(TAG, "onPageScrolled: positionOffset" + positionOffset);
//                int offset = (int) (Math.round(Math.ceil(positionOffset * 15)));
//                Log.d(TAG, "onPageScrolled: " + offset);
//                assert tab2 != null;
//                assert tab1 != null;
//                if (offset <= 0 || offset > 10) {
//                    //xử lý phần chữ của tablayout
//                    setTabTextSize(tab1, (currentFontSizeTabLayout1 - 10));
//                    setTabTextSize(tab2, (currentFontSizeTabLayout2 + 10));
//                }
//                if (position == 0) {
//                    if ((currentFontSizeTabLayout1 - offset) >= 15) {
////                        Log.d(TAG, "onPageScrolled: " + (currentFontSizeTabLayout1 - offset) + " " + (currentFontSizeTabLayout2 + offset));
//                        setTabTextSize(tab1, (currentFontSizeTabLayout1 - offset));
//                        setTabTextSize(tab2, (currentFontSizeTabLayout2 + offset));
//                    }
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Log.d(TAG, "onPageScrollStateChanged: " + state);
//            }
//        });

        // set item cho tablayout
//        assert tab1 != null;
//        tab1.setCustomView(createCustomTabView("Top Lượt Nghe", currentFontSizeTabLayout1));
//        assert tab2 != null;
//        tab2.setCustomView(createCustomTabView("Tất cả", currentFontSizeTabLayout2));


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

    public void setUpMiniPlayer() {
        if (musicPlayer.getCurrentSong() != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentMiniPlayer, MiniPlayerFragment.newInstance(musicPlayer.getCurrentSong()))
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMiniPlayer();
    }
}