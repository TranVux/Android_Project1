package com.example.assignment_pro1121_nhom3.fragments;

import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_ID_OF_PLAYLIST;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_IS_DECREASE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_LIMIT;
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
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_GENRES;
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_SINGER;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MusicInPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.views.MainActivity;
import com.example.assignment_pro1121_nhom3.views.MoreMusicActivity;
import com.example.assignment_pro1121_nhom3.views.SearchActivity;
import com.example.assignment_pro1121_nhom3.views.SplashScreen;

import java.util.ArrayList;

public class NewMusicFragment extends Fragment {

    private static final String TAG = NewMusicFragment.class.getSimpleName();

    private ArrayList<Music> musicArrayList;
    private RecyclerView mRecyclerView;
    private MusicInPlaylistAdapter musicInPlaylistAdapter;
    MusicDAO musicDAO;
    ProgressBar progressBar;
    LinearLayout emptyLayout;
    TextView keyword;
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;


    public NewMusicFragment() {
        // Required empty public constructor
    }

    public static NewMusicFragment newInstance(String param1, String param2) {
        NewMusicFragment fragment = new NewMusicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        mRecyclerView = view.findViewById(R.id.newMusicFragmentListMusic);
        progressBar = view.findViewById(R.id.progressBar);
        emptyLayout = view.findViewById(R.id.emptyLayout);
        keyword = view.findViewById(R.id.keyword);

        musicInPlaylistAdapter = new MusicInPlaylistAdapter(musicArrayList, requireContext(), new ItemEvent.MusicItemInPlayListEvent() {
            @Override
            public void onItemClick(int index) {
                if (musicInPlaylistAdapter.getItemCount() > 0) {
                    musicPlayer.pauseSong(musicPlayer.getCurrentPositionSong());
                    musicPlayer.clearPlaylist();
                    musicPlayer.setPlayList(musicInPlaylistAdapter.getListMusic());
                    musicPlayer.start();
                    try {
                        musicPlayer.setStateMusicPlayer(MusicPlayer.MUSIC_PLAYER_STATE_PLAYING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    saveCurrentMusic(musicPlayer, musicArrayList.get(index).getGenresId(), PLAYLIST_TYPE_GENRES);
                    Log.d(TAG, "onClick: " + musicPlayer.getStateMusicPlayer());
                    startActivity(new Intent(requireContext(), MainActivity.class));
                    startServiceMusic(musicPlayer.getCurrentSong(), MusicPlayer.MUSIC_PLAYER_ACTION_RESET_SONG);
                }
            }

            @Override
            public void onMoreClick(Music music) {
                BottomSheet bottomSheet = BottomSheet.newInstance(music);
                bottomSheet.show(getParentFragmentManager(), "TAG");
            }
        });
        mRecyclerView.setAdapter(musicInPlaylistAdapter);
        musicDAO = new MusicDAO();

        if (MoreMusicActivity.tempGenres == null) {
            musicDAO.getMusicItemWithLimit(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {
                    progressBar.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 100, new MusicDAO.GetDataMusicWithLimit() {
                @Override
                public void onGetLimitData(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                    musicArrayList = list;
                    if (list.size() <= 0) {
                        keyword.setText(MoreMusicActivity.tempGenres.getName());
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
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
            }, 100, false, MoreMusicActivity.tempGenres.getId(), new MusicDAO.GetTopMusicByGenres() {
                @Override
                public void onGetTopByGenresCallBack(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                    musicArrayList = list;
                    if (list.size() <= 0) {
                        keyword.setText(MoreMusicActivity.tempGenres.getName());
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist, String typePlayList) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("music_player", Context.MODE_PRIVATE);
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
        editor.putBoolean(KEY_IS_DECREASE, false);
        editor.putInt(KEY_LIMIT, 100);
        Log.d(TAG, "saveCurrentMusic: " + typePlayList);
        Log.d(TAG, "saveCurrentMusic: " + idPlaylist);
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        editor.apply();
    }


    public void startServiceMusic(Music music, int action) {
        Intent serviceMusic = new Intent(requireContext(), MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        requireContext().startService(serviceMusic);
    }
}