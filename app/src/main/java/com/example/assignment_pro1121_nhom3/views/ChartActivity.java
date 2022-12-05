package com.example.assignment_pro1121_nhom3.views;

import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_ID_OF_PLAYLIST;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_MODE_MUSIC_PLAYER;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_MUSIC;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_PLAYLIST_TYPE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_CREATION_DATE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_GENRES_ID;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_ID;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_INDEX;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_NAME;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_SINGER_ID;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_SINGER_NAME;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_THUMBNAIL_URL;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_UPDATE_DATE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_URL;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_VIEWS;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_TOP_10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.ChartPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.fragments.BottomSheet;
import com.example.assignment_pro1121_nhom3.fragments.MiniPlayerFragment;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.models.TopMusics;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.example.assignment_pro1121_nhom3.utils.RoundedBarChart;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class ChartActivity extends AppCompatActivity {

    public static final String TAG = ChartActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RelativeLayout topBarLayout;
    private ChartPlaylistAdapter adapter;
    private MusicDAO musicDAO;
    private RoundedBarChart barChart;
    ArrayList<Music> listMusic;
    ArrayList<TopMusics> listTopMusic;
    public MusicPlayer musicPlayer = SplashScreen.musicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        //đổi màu chữ status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //bỏ hiệu ứng fade của transition
        Transition transition = TransitionInflater.from(getApplicationContext()).inflateTransition(android.R.transition.no_transition);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);

        musicDAO = new MusicDAO();
        setContentView(R.layout.activity_charts);

        //set up mini player
        setUpMiniPlayer();

        recyclerView = findViewById(R.id.recyclerview);
        NestedScrollView nestedScrollView = findViewById(R.id.scrollView);
        barChart = findViewById(R.id.barChart);
        topBarLayout = findViewById(R.id.topBarLayout);

        //set click cho top bar layout
        topBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(ChartActivity.this, SearchActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        ChartActivity.this, topBarLayout,
                        Objects.requireNonNull(ViewCompat.getTransitionName(topBarLayout)));
                startActivity(searchIntent, optionsCompat.toBundle());
            }
        });

        //set margin cho nestedScrollview

        //
        //text view has color gradient
        TextView textView = findViewById(R.id.labelBxh);
        textView.setText("#BXH".toUpperCase());

        TextPaint paint = textView.getPaint();
        float width = paint.measureText("#BXH");
        //

        // fix lỗi item của recyclerview bị khuất
        Log.d("TAG>>>>>>>>>", "onCreate: " + getNavigationBarHeight());
        setMargins(nestedScrollView, 0, 0, 0, getNavigationBarHeight() - 5);

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#13DCFE"),
                        Color.parseColor("#716BFE"),
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
        //

        //adapter cho top
        adapter = new ChartPlaylistAdapter(this, new ChartPlaylistAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music, int position) {
                if (adapter.getItemCount() > 0) {
                    musicPlayer.pauseSong(musicPlayer.getCurrentPositionSong());
                    musicPlayer.clearPlaylist();
                    musicPlayer.setPlayList((ArrayList<Music>) adapter.getList());
                    musicPlayer.setMusicAtPosition(position);
                    try {
                        musicPlayer.setStateMusicPlayer(MusicPlayer.MUSIC_PLAYER_STATE_PLAYING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicPlayer.setCurrentMode(MusicPlayer.MUSIC_PLAYER_MODE_ONLINE);
                    saveCurrentMusic(musicPlayer, KEY_TOP_10, KEY_TOP_10);
                    Log.d(TAG, "onClick: " + musicPlayer.getStateMusicPlayer());
                    startActivity(new Intent(ChartActivity.this, MainActivity.class));
                    startServiceMusic(musicPlayer.getCurrentSong(), MusicPlayer.MUSIC_PLAYER_ACTION_RESET_SONG, musicPlayer.getCurrentMode());
                }
            }

            @Override
            public void onMoreButtonClick(Music music) {
                BottomSheet bottomSheet = BottomSheet.newInstance(music);
                bottomSheet.show(getSupportFragmentManager(), TAG);
            }
        });

        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        musicDAO.getTopMusicListen(new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {

            }

            @Override
            public void afterGetData() {

            }
        }, 10, new MusicDAO.GetTopMusicListener() {
            @Override
            public void onGetTopMusicCallback(ArrayList<Music> list) {
                adapter.setData(list);
                listMusic = list;
                setDataBarChart();
            }
        });
        setUpBarChart();
    }

    public void addMusicIntoListTopMusic(ArrayList<Music> list) {
        listTopMusic = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listTopMusic.add(new TopMusics(list.get(i), (i + 1)));
        }
    }

    public void setDataBarChart() {
        ArrayList<BarEntry> barChartEntry = new ArrayList<>();

        addMusicIntoListTopMusic(listMusic);

        Collections.shuffle(listTopMusic);

        for (TopMusics topMusics : listTopMusic) {
            barChartEntry.add(new BarEntry(topMusics.getRank(), topMusics.getMusic().getViews()));
            Log.d(TAG, "setDataBarChart: " + topMusics.getRank());
        }

        BarDataSet barDataSet = new BarDataSet(barChartEntry, "");
        barDataSet.setValueTextSize(13f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(.3f);
        barData.setDrawValues(false);
        barData.setValueTextColor(Color.WHITE);

        barChart.setData(barData);
        barChart.invalidate();
        barChart.animateY(1000, Easing.EaseInOutCirc);
    }

    public void setUpBarChart() {
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDescription(null);
        barChart.setTouchEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setRadius(25);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(13f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setYOffset(5);
        xAxis.setDrawLabels(true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10);
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public int getNavigationBarHeight() {
        boolean hasMenuKey = ViewConfiguration.get(ChartActivity.this).hasPermanentMenuKey();
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public void startServiceMusic(Music music, int action, String mode) {
        Intent serviceMusic = new Intent(ChartActivity.this, MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        serviceMusic.putExtra(KEY_MODE_MUSIC_PLAYER, mode);
        startService(serviceMusic);
    }

    public boolean checkUniqueSong(String songName) {
        ArrayList<Music> list = (ArrayList<Music>) SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().checkSong(songName);
        return list.size() <= 0;
    }

    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist, String typePlaylist) {
        if (checkUniqueSong(musicPlayer.getCurrentSong().getName())) {
            SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().insertSong(musicPlayer.getCurrentSong());
        }
        SharedPreferences sharedPreferencesMusicList = getSharedPreferences("music_player", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesMusicList.edit();
        editor.putString(KEY_SONG_NAME, musicPlayer.getCurrentSong().getName());
        editor.putString(KEY_SONG_URL, musicPlayer.getCurrentSong().getUrl());
        editor.putString(KEY_SONG_THUMBNAIL_URL, musicPlayer.getCurrentSong().getThumbnailUrl());
        editor.putString(KEY_SONG_ID, musicPlayer.getCurrentSong().getId());
        editor.putLong(KEY_SONG_VIEWS, musicPlayer.getCurrentSong().getViews());
        editor.putString(KEY_SONG_SINGER_ID, musicPlayer.getCurrentSong().getSingerId());
        editor.putString(KEY_SONG_SINGER_NAME, musicPlayer.getCurrentSong().getSingerName());
        editor.putString(KEY_SONG_GENRES_ID, musicPlayer.getCurrentSong().getGenresId());
        editor.putLong(KEY_SONG_CREATION_DATE, musicPlayer.getCurrentSong().getCreationDate());
        editor.putLong(KEY_SONG_UPDATE_DATE, musicPlayer.getCurrentSong().getUpdateDate());
        editor.putInt(KEY_SONG_INDEX, musicPlayer.getPlayListMusic().indexOf(musicPlayer.getCurrentSong()));
        Log.d(TAG, "saveCurrentMusic: " + idPlaylist);
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        editor.putString(KEY_PLAYLIST_TYPE, typePlaylist);
        editor.apply();
    }

    public void setUpMiniPlayer() {
        if (musicPlayer.getCurrentSong() != null) {
            Log.d(TAG, "setUpMiniPlayer: ");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentMiniPlayer, MiniPlayerFragment.newInstance(musicPlayer.getCurrentSong()), "MiniPlayer")
                    .commit();
        }
    }
}