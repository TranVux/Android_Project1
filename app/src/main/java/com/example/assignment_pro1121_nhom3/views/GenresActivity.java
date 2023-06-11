package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.GenresAdapter;
import com.example.assignment_pro1121_nhom3.dao.GenreDAO;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.fragments.MiniPlayerFragment;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Genres;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.utils.GridSpacingItemDecoration;
import com.example.assignment_pro1121_nhom3.utils.ItemOffsetDecoration;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

public class GenresActivity extends AppCompatActivity {
    ImageView btnBack;
    RecyclerView rclGenresList;
    GenresAdapter genresAdapter;
    ArrayList<Genres> listGenres;
    GenreDAO genreDAO;
    MusicDAO musicDAO;
    ProgressBar progressBar;
    MusicPlayer musicPlayer = MusicPlayer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar, null));
        //đổi màu chữ status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //

        init();
        setUpMiniPlayer();
        getData();
    }

    public void init() {
        btnBack = findViewById(R.id.btnBack);
        rclGenresList = findViewById(R.id.rclGenres);
        progressBar = findViewById(R.id.progressBar);

        listGenres = new ArrayList<>();
        genreDAO = new GenreDAO();
        musicDAO = new MusicDAO();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(GenresActivity.this, 2);
        rclGenresList.setLayoutManager(gridLayoutManager);

        genresAdapter = new GenresAdapter(listGenres, GenresActivity.this, new GenresAdapter.EventItem() {
            @Override
            public void onItemClick(Genres genres) {
                Intent detailIntent = new Intent(GenresActivity.this, DetailPlaylistActivity.class);
                detailIntent.putExtra("genres", genres);
                startActivity(detailIntent);
            }
        });

        rclGenresList.setAdapter(genresAdapter);
    }

    public void getData() {
        genreDAO.getAllDataGenre(new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterGetData() {
                progressBar.setVisibility(View.GONE);
            }
        }, new GenreDAO.ReadAllDataGenre() {
            @Override
            public void onReadAllDataGenreCallback(ArrayList<Genres> list) {
                listGenres = list;
                genresAdapter.setListGenres(listGenres);
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

    @Override
    protected void onResume() {
        super.onResume();
        setUpMiniPlayer();
    }
}