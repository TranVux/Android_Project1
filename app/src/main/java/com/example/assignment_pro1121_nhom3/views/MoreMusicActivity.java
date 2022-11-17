package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.ViewPager2ForMoreMusicAdapter;
import com.example.assignment_pro1121_nhom3.dao.GenreDAO;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.fragments.MostListenSongFragment;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Genres;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MoreMusicActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    public static final String TAG = MoreMusicActivity.class.getSimpleName();
    int currentFontSizeTabLayout1 = 25;
    int currentFontSizeTabLayout2 = 15;
    ImageView btnBack;
    Chip chipAll;
    ChipGroup chipGroup;
    public static String valueChip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_music);
        reference();
        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar,null));
        //đổi màu chữ status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //
        setDataForChip();
        ViewPager2ForMoreMusicAdapter viewPager2ForMoreMusicAdapter = new ViewPager2ForMoreMusicAdapter(this);
        viewPager2.setAdapter(viewPager2ForMoreMusicAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
              if(position == 0){
                  tab.setCustomView(createCustomTabView("Top Lượt Nghe", currentFontSizeTabLayout1));
              }
              if(position == 1){
                  tab.setCustomView(createCustomTabView("Tất cả", currentFontSizeTabLayout2));
              }
            }
        }).attach();
        tabLayout.setSmoothScrollingEnabled(true);
        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        assert tab1 != null;
        assert tab2 != null;

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                int offset = (int) (Math.round(Math.ceil(positionOffset * 15)));
                if (offset <= 0 || offset > 10) {
                    //xử lý phần chữ của tablayout
                    if (tab1 != null) {
                        setTabTextSize(tab1, (currentFontSizeTabLayout1 - 10));
                    }
                    if (tab2 != null) {
                        setTabTextSize(tab2, (currentFontSizeTabLayout2 + 10));
                    }
                }
                if (position == 0) {
                    if ((currentFontSizeTabLayout1 - offset) >= 15) {
//                        Log.d(TAG, "onPageScrolled: " + (currentFontSizeTabLayout1 - offset) + " " + (currentFontSizeTabLayout2 + offset));
                        if (tab1 != null) {
                            setTabTextSize(tab1, (currentFontSizeTabLayout1 - offset));
                        }
                        if (tab2 != null) {
                            setTabTextSize(tab2, (currentFontSizeTabLayout2 + offset));
                        }
                    }
                }
            }
        });
       // xử lý nút back cho activity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                Chip chip = chipGroup.findViewById(chipGroup.getCheckedChipId());
                ViewPager2ForMoreMusicAdapter viewPager2ForMoreMusicAdapter1;
                if(chip != null){
                    valueChip = chip.getText().toString();
                    viewPager2ForMoreMusicAdapter1 = new ViewPager2ForMoreMusicAdapter(MoreMusicActivity.this);
                    viewPager2.setAdapter(viewPager2ForMoreMusicAdapter1);
                }
            }
        });
    }

    private void setDataForChip() {
        GenreDAO genreDAO = new GenreDAO();
        genreDAO.getAllDataGenre(new GenreDAO.ReadAllDataGenre() {
            @Override
            public void onReadAllDataGenreCallback(ArrayList<Genres> list) {
                for (int i = 0; i < list.size(); i++) {
                    Chip chip =
                            (Chip) getLayoutInflater().inflate(R.layout.single_chip_layout, chipGroup, false);
                    chip.setText(list.get(i).getName());
                    chipGroup.addView(chip);
                }
            }
        });
    }

    private View createCustomTabView(String tabText, int tabSizeSp) {
        View tabCustomView = getLayoutInflater().inflate(R.layout.layout_tab, null);
        TextView tabTextView = tabCustomView.findViewById(R.id.tabTV);
        tabTextView.setText(tabText);
        tabTextView.setTextSize(tabSizeSp);
        return tabCustomView;
    }

    private void setTabTextSize(TabLayout.Tab tab, int tabSizeSp) {
        View tabCustomView = tab.getCustomView();
        if(tabCustomView != null){
            TextView tabTextView = tabCustomView.findViewById(R.id.tabTV);
            tabTextView.setTextSize(tabSizeSp);
        }
    }

    private void reference() {
        tabLayout = findViewById(R.id.tabBar);
        viewPager2 = findViewById(R.id.viewPager2);
        btnBack = findViewById(R.id.btnBack);
        chipAll = findViewById(R.id.chipAll);
        chipGroup = findViewById(R.id.moreMusicChipGroup);
    }

    @Override
    protected void onStart() {
        super.onStart();
        valueChip = "Tất cả";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        valueChip = null;
    }
}