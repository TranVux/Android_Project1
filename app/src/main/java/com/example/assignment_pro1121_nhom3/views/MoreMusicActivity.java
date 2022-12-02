package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.ViewPager2ForMoreMusicAdapter;
import com.example.assignment_pro1121_nhom3.dao.GenreDAO;
import com.example.assignment_pro1121_nhom3.fragments.MiniPlayerFragment;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Genres;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
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
    ImageView btnBack;
    Chip chipAll;
    ChipGroup chipGroup;
    public static Genres tempGenres;
    ArrayList<Genres> listGenres = new ArrayList<>();
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_music);
        reference();
        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar, null));
        //đổi màu chữ status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //

        //setup player mini
        setUpMiniPlayer();

        setDataForChip();
        ViewPager2ForMoreMusicAdapter viewPager2ForMoreMusicAdapter = new ViewPager2ForMoreMusicAdapter(this);
        viewPager2.setAdapter(viewPager2ForMoreMusicAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Top Lượt Nghe");
                }
                if (position == 1) {
                    tab.setText("Tất cả");
                }
            }
        }).attach();
        tabLayout.setSmoothScrollingEnabled(true);

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
                if (chip != null) {
                    String valueChip = chip.getText().toString();
                    setDataForTempGenres(valueChip);
                    viewPager2ForMoreMusicAdapter1 = new ViewPager2ForMoreMusicAdapter(MoreMusicActivity.this);
                    viewPager2.setAdapter(viewPager2ForMoreMusicAdapter1);
                }
            }
        });
    }

    public void setUpMiniPlayer() {
        if (musicPlayer.getCurrentSong() != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentMiniPlayer, MiniPlayerFragment.newInstance(musicPlayer.getCurrentSong()))
                    .commit();
        }
    }

    public void setDataForTempGenres(String nameGenres) {
        for (Genres genres : listGenres) {
            if (genres.getName().equalsIgnoreCase(nameGenres)) {
                tempGenres = genres;
                return;
            }
        }
        tempGenres = null;
    }

    private void setDataForChip() {
        GenreDAO genreDAO = new GenreDAO();
        genreDAO.getAllDataGenre(new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {

            }

            @Override
            public void afterGetData() {

            }
        }, new GenreDAO.ReadAllDataGenre() {
            @Override
            public void onReadAllDataGenreCallback(ArrayList<Genres> list) {
                listGenres = list;
                for (int i = 0; i < list.size(); i++) {
                    Chip chip = (Chip) getLayoutInflater().inflate(R.layout.single_chip_layout, chipGroup, false);
                    chip.setText(list.get(i).getName());
                    chipGroup.addView(chip);
                }
            }
        });
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
        tempGenres = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMiniPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tempGenres = null;
    }
}