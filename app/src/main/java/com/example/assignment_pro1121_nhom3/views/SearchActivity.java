package com.example.assignment_pro1121_nhom3.views;

import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_ID_OF_PLAYLIST;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_MODE_MUSIC_PLAYER;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_MUSIC;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_PLAYLIST_TYPE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_RECENT_KEY_WORDS;
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
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_SINGER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MyPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.fragments.BottomSheet;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements MusicDAO.GetDataPagination, MusicDAO.SearchMusic, IOnProgressBarStatusListener {

    public static final String TAG = SearchActivity.class.getSimpleName();

    private MusicDAO musicDAO;
    private EditText edtSearchBar;
    private MyPlaylistAdapter myPlaylistAdapter;
    private RecyclerView recyclerView;
    private Timer timer;
    private TextView keywords;
    private LinearLayout emptyLayout;
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;
    private ArrayList<String> searchHistory = new ArrayList<>();
    private ChipGroup chipGroup;
    private ProgressBar progressBar;
    private Query nextQuery;
    private ArrayList<Music> musicSearchArr;
    private boolean isContinuous = false;
    private boolean isNewQuery = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Transition transition = TransitionInflater.from(getApplicationContext()).inflateTransition(android.R.transition.no_transition);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);
        init();

        edtSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <= 0) {
                    isNewQuery = true;
                    musicSearchArr.clear();
                    nextQuery = null;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                search(edtSearchBar.getText().toString());
                            }
                        });
                    }
                }, 1800);
            }
        });
    }

    private void search(String searchString) {
        if (!searchString.isEmpty()) {
            Log.d(TAG, "search: " + searchString);
            if (!searchHistory.contains(searchString)) {
                searchHistory.add(searchString);
                if (searchHistory.size() >= 7) {
                    searchHistory.remove(0);
                }
                Log.d(TAG, "search: " + searchHistory);
            }
            musicDAO.searchByKeyWord(searchString, nextQuery, this, this, this);
        }
    }

    public void init() {
        recyclerView = findViewById(R.id.searchResultRcl);
        edtSearchBar = findViewById(R.id.searchBar);
        keywords = findViewById(R.id.keyword);
        emptyLayout = findViewById(R.id.emptyLayout);
        chipGroup = findViewById(R.id.chipGroupRecentSearchValue);
        progressBar = findViewById(R.id.progressBar);

        musicSearchArr = new ArrayList<>();

        timer = new Timer();
        musicDAO = new MusicDAO();
        searchHistory = getArrayList(KEY_RECENT_KEY_WORDS);
        addSearchResult();
        Log.d(TAG, "init: " + searchHistory.toString());

        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        myPlaylistAdapter = new MyPlaylistAdapter(SearchActivity.this, new MyPlaylistAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music, int position) {
                if (myPlaylistAdapter.getItemCount() > 0) {
                    musicPlayer.pauseSong(musicPlayer.getCurrentPositionSong());
                    musicPlayer.clearPlaylist();
                    musicPlayer.setPlayList(myPlaylistAdapter.getList());
                    musicPlayer.start();
                    try {
                        musicPlayer.setStateMusicPlayer(MusicPlayer.MUSIC_PLAYER_STATE_PLAYING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicPlayer.setCurrentMode(MusicPlayer.MUSIC_PLAYER_MODE_ONLINE);
                    saveCurrentMusic(musicPlayer, music.getSingerId(), PLAYLIST_TYPE_SINGER);
                    Log.d(TAG, "onClick: " + musicPlayer.getStateMusicPlayer());
                    startActivity(new Intent(SearchActivity.this, MainActivity.class));
                    startServiceMusic(musicPlayer.getCurrentSong(), MusicPlayer.MUSIC_PLAYER_ACTION_RESET_SONG, musicPlayer.getCurrentMode());
                }
            }

            @Override
            public void onMoreButtonClick(Music music) {
                BottomSheet bottomSheet = BottomSheet.newInstance(music);
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });
        myPlaylistAdapter.setData(musicSearchArr);
        recyclerView.setAdapter(myPlaylistAdapter);
    }

    public boolean checkUniqueSong(String songName) {
        ArrayList<Music> list = (ArrayList<Music>) SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().checkSong(songName);
        return list.size() <= 0;
    }

    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist, String typePlayList) {
        if (checkUniqueSong(musicPlayer.getCurrentSong().getName())) {
            SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().insertSong(musicPlayer.getCurrentSong());
        }
        SharedPreferences sharedPreferences = getSharedPreferences("music_player", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
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
        editor.putString(KEY_PLAYLIST_TYPE, typePlayList);
        Log.d(TAG, "saveCurrentMusic: " + typePlayList);
        Log.d(TAG, "saveCurrentMusic: " + idPlaylist);
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        editor.apply();
    }

    public void addSearchResult() {
        if (searchHistory == null) {
            searchHistory = new ArrayList<>();
        }
        for (String s : searchHistory) {
            Chip chip = (Chip) LayoutInflater.from(SearchActivity.this).inflate(R.layout.a_chip_search_history, chipGroup, false);
            chip.setText(s);
            chipGroup.addView(chip);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: ");
                    edtSearchBar.setText(chip.getText().toString());
                    isNewQuery = true;
                    musicSearchArr.clear();
                    nextQuery = null;
                    search(chip.getText().toString());
                }
            });
        }

    }

    public void startServiceMusic(Music music, int action, String mode) {
        Intent serviceMusic = new Intent(SearchActivity.this, MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        serviceMusic.putExtra(KEY_MODE_MUSIC_PLAYER, mode);
        startService(serviceMusic);
    }

    public void saveArrayList(ArrayList<String> list, String key) {
        SharedPreferences prefs = getSharedPreferences("keyword_history", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getArrayList(String key) {
        SharedPreferences prefs = getSharedPreferences("keyword_history", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveArrayList(searchHistory, KEY_RECENT_KEY_WORDS);
    }

    @Override
    public void getNextQuery(Query query) {
        if (query != null) {
            nextQuery = query;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSearchSuccess(ArrayList<Music> result) {
        isNewQuery = false;
        musicSearchArr.addAll(result);
        myPlaylistAdapter.setData(musicSearchArr);
        Log.d(TAG, "onSearchSuccess: " + musicSearchArr.size());

        isContinuous = myPlaylistAdapter.getList().size() <= 10;

        if (musicSearchArr.size() == 0) {
            Log.d(TAG, "onSearchSuccess: vào if");
            if (emptyLayout.getVisibility() != View.VISIBLE) {
                Log.d(TAG, "onSearchSuccess: vào if 2");
                myPlaylistAdapter.setData(new ArrayList<>());
                emptyLayout.setVisibility(View.VISIBLE);
                keywords.setText("'" + edtSearchBar.getText().toString().trim() + "'");
            }
        } else {
            emptyLayout.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSearchFailure() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void beforeGetData() {
        if (isNewQuery) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterGetData() {
        progressBar.setVisibility(View.GONE);
        if (isContinuous) {
            musicDAO.searchByKeyWord(edtSearchBar.getText().toString().trim(), nextQuery, this, this, this);
        }
    }
}