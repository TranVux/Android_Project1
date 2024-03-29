package com.example.assignment_pro1121_nhom3.views;

import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_ID_OF_PLAYLIST;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_IS_DECREASE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_LIMIT;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_MODE_MUSIC_PLAYER;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_MUSIC;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_PLAYLIST;
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
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_DEFAULT;
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_GENRES;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MyPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.fragments.BottomSheet;
import com.example.assignment_pro1121_nhom3.fragments.MiniPlayerFragment;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Genres;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.storages.MusicPlayerStorage;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

public class DetailPlaylistActivity extends AppCompatActivity {

    public static final String TAG = DetailPlaylistActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private MyPlaylistAdapter adapter;
    ImageView imageForeground, thumbnailPlaylist, btnBack;
    NestedScrollView nestedScrollView;

    Playlist tempPlaylist;
    Genres tempGenres;
    boolean isGenres = false;
    private PlaylistDAO playlistDAO = new PlaylistDAO();
    private MusicDAO musicDAO = new MusicDAO();

    ProgressBar progressBar;
    TextView tvPlaylistName, topBarPlaylistName, tvCreatorName, amountOfSong, keyword;
    CircleImageView avtCreator;
    LinearLayout btnPlayAll, emptyLayout;

    MusicPlayer musicPlayer = SplashScreen.musicPlayer;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        //Đổi màu status bar nè
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tempPlaylist = (Playlist) bundle.getSerializable("playlist");
            if (tempPlaylist == null) {
                tempGenres = (Genres) bundle.getSerializable("genres");
                isGenres = true;
            }
        }

        //setup sharedPreference
        sharedPreferences = getSharedPreferences("music_player", MODE_PRIVATE);

        setContentView(R.layout.activity_detail_playlist);
        init();
        //setup mini player
        setUpMiniPlayer();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        adapter = new MyPlaylistAdapter(this, new MyPlaylistAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music, int position) {
                navigateToPLayer(position);
            }

            @Override
            public void onMoreButtonClick(Music music) {
                BottomSheet bottomSheet = BottomSheet.newInstance(music);
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });

        Log.d(TAG, "onCreate: " + getNavigationBarHeight());
        setMargins(nestedScrollView, 0, 0, 0, getNavigationBarHeight() - 5);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        btnPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToPLayer(0);
            }
        });

        if (tempPlaylist != null) {
            getDataPlaylist();
        } else {
            getDataGenres();
        }
    }

    public void init() {
        recyclerView = findViewById(R.id.recyclerView);
        imageForeground = findViewById(R.id.imageForeground);
        nestedScrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);
        thumbnailPlaylist = findViewById(R.id.imageView);
        tvCreatorName = findViewById(R.id.tvCreatorName);
        tvPlaylistName = findViewById(R.id.tvPlaylistName);
        topBarPlaylistName = findViewById(R.id.tvNamePlaylistTopBar);
        avtCreator = findViewById(R.id.avtCreator);
        amountOfSong = findViewById(R.id.amountOfSong);
        btnBack = findViewById(R.id.backBtn);
        btnPlayAll = findViewById(R.id.buttonPlayAll);
        emptyLayout = findViewById(R.id.emptyLayout);
        keyword = findViewById(R.id.keyword);

        topBarPlaylistName.setSelected(true);
    }

    public void getDataGenres() {
        musicDAO.getTopMusicByGenres(new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {
                progressBar.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
            }

            @Override
            public void afterGetData() {
                progressBar.setVisibility(View.GONE);
            }
        }, 100, false, tempGenres.getId(), new MusicDAO.GetTopMusicByGenres() {
            @Override
            public void onGetTopByGenresCallBack(ArrayList<Music> list) {
                adapter.setData(list);
                amountOfSong.setText(list.size() + " bài");
                if (list.size() <= 0) {
                    emptyLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    keyword.setText(tempGenres.getName());
                }
            }
        });

        // gắn ảnh cho playlist
        Glide.with(DetailPlaylistActivity.this).asBitmap().load(tempGenres.getUrlThumbnail()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Blurry.with(DetailPlaylistActivity.this)
                        .sampling(8)
                        .radius(30)
                        .async()
                        .from(resource).into(imageForeground);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                Blurry.with(DetailPlaylistActivity.this)
                        .sampling(8)
                        .radius(30)
                        .async()
                        .from(BitmapFactory.decodeResource(getResources(), R.drawable.fallback_img)).into(imageForeground);
            }
        });
        //gắn ảnh thumbnail cho playlist
        Glide.with(DetailPlaylistActivity.this)
                .load(tempGenres.getUrlThumbnail())
                .apply(new RequestOptions().override(250, 250))
                .into(thumbnailPlaylist);

        //gán tên playlist, người tạo,...
        tvPlaylistName.setText(CapitalizeWord.CapitalizeWords(tempGenres.getName()));
        topBarPlaylistName.setText(CapitalizeWord.CapitalizeWords(tempGenres.getName()));
        tvCreatorName.setVisibility(View.GONE);
    }

    public void getDataPlaylist() {
        playlistDAO.getMusicInPlaylist(tempPlaylist.getId(), new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterGetData() {
                progressBar.setVisibility(View.GONE);
            }
        }, new PlaylistDAO.ReadMusicInPlaylist() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReadSuccess(ArrayList<Music> listMusic) {
                Log.d(TAG, "onReadSuccess: " + listMusic.size());
                adapter.setData(listMusic);
                amountOfSong.setText(listMusic.size() + " bài");
            }

            @Override
            public void onReadFailure(Exception e) {
                Log.d(TAG, "onReadFailure: lỗi nè ba");
            }
        });

        // gắn ảnh cho playlist
        Glide.with(DetailPlaylistActivity.this).asBitmap().load(tempPlaylist.getUrlThumbnail()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Blurry.with(DetailPlaylistActivity.this)
                        .sampling(8)
                        .radius(30)
                        .async()
                        .from(resource).into(imageForeground);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                Blurry.with(DetailPlaylistActivity.this)
                        .sampling(8)
                        .radius(30)
                        .async()
                        .from(BitmapFactory.decodeResource(getResources(), R.drawable.fallback_img)).into(imageForeground);
            }
        });
        //gắn ảnh thumbnail cho playlist
        Glide.with(DetailPlaylistActivity.this)
                .load(tempPlaylist.getUrlThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().override(250, 250))
                .into(thumbnailPlaylist);

        //gán tên playlist, người tạo,...
        tvPlaylistName.setText(CapitalizeWord.CapitalizeWords(tempPlaylist.getName()));
        tvCreatorName.setText(CapitalizeWord.CapitalizeWords(tempPlaylist.getCreatorName()));
        topBarPlaylistName.setText(CapitalizeWord.CapitalizeWords(tempPlaylist.getName()));
    }

    public void navigateToPLayer(int position) {
        if (adapter.getItemCount() > 0) {
            musicPlayer.pauseSong(musicPlayer.getCurrentPositionSong());
            musicPlayer.clearPlaylist();
            musicPlayer.setPlayList(adapter.getList());
            musicPlayer.setMusicAtPosition(position);
            try {
                musicPlayer.setStateMusicPlayer(MusicPlayer.MUSIC_PLAYER_STATE_PLAYING);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String tempId;
            String playlistType;
            if (isGenres) {
                tempId = tempGenres.getId();
                playlistType = PLAYLIST_TYPE_GENRES;
            } else {
                tempId = tempPlaylist.getId();
                playlistType = PLAYLIST_TYPE_DEFAULT;
            }
            musicPlayer.setCurrentMode(MusicPlayer.MUSIC_PLAYER_MODE_ONLINE);
            saveCurrentMusic(musicPlayer, tempId, playlistType);
            Log.d(TAG, "onClick: " + musicPlayer.getStateMusicPlayer());
            startActivity(new Intent(DetailPlaylistActivity.this, MainActivity.class));
            startServiceMusic(adapter.getList(), MusicPlayer.MUSIC_PLAYER_ACTION_RESET_PLAYLIST, musicPlayer.getCurrentMode());
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public int getNavigationBarHeight() {
        boolean hasMenuKey = ViewConfiguration.get(DetailPlaylistActivity.this).hasPermanentMenuKey();
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public boolean checkUniqueSong(String songName) {
        ArrayList<Music> list = (ArrayList<Music>) SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().checkSong(songName);
        return list.size() <= 0;
    }

    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist, String typePlayList) {
        SharedPreferences.Editor editor = MusicPlayerStorage.getInstance(this).edit();
        editor.putInt(KEY_SONG_INDEX, musicPlayer.getCurrentIndexSong());
        Log.d(TAG, "saveCurrentMusic: " + idPlaylist);
        Log.d(TAG, "saveCurrentMusic: " + typePlayList);
        if (isGenres) {
            editor.putBoolean(KEY_IS_DECREASE, false);
            editor.putInt(KEY_LIMIT, 100);
        }
        editor.putString(KEY_PLAYLIST_TYPE, typePlayList);
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        editor.apply();
    }


    public void startServiceMusic(ArrayList<Music> listMusic, int action, String mode) {
        Intent serviceMusic = new Intent(DetailPlaylistActivity.this, MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_PLAYLIST, listMusic);
        serviceMusic.putExtra(KEY_MODE_MUSIC_PLAYER, mode);
        serviceMusic.putExtra(KEY_SONG_INDEX, MusicPlayerStorage.getInstance(this).getInt(KEY_SONG_INDEX, 0));
        startService(serviceMusic);
    }

    public void setUpMiniPlayer() {
        if (musicPlayer.getCurrentSong() != null) {
            Log.d(TAG, "setUpMiniPlayer: ");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentMiniPlayer, MiniPlayerFragment.newInstance(musicPlayer.getCurrentSong()))
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMiniPlayer();
    }
}