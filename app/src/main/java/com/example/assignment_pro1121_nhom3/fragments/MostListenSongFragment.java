package com.example.assignment_pro1121_nhom3.fragments;

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
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_GENRES;

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

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MusicInPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.storages.MusicPlayerStorage;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.example.assignment_pro1121_nhom3.views.MainActivity;
import com.example.assignment_pro1121_nhom3.views.MoreMusicActivity;
import com.example.assignment_pro1121_nhom3.views.SplashScreen;

import java.util.ArrayList;

public class MostListenSongFragment extends Fragment {
    private static final String TAG = MostListenSongFragment.class.getSimpleName();
    private ArrayList<Music> musicArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    public MusicInPlaylistAdapter musicInPlaylistAdapter;
    private MusicDAO musicDAO;
    ProgressBar progressBar;
    LinearLayout emptyLayout;
    TextView keyWords;
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;

    public MostListenSongFragment() {

    }

    public static MostListenSongFragment newInstance(String param1, String param2) {
        MostListenSongFragment fragment = new MostListenSongFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_most_listen_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        // loadData();
    }


    private void init(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.mostListenSongFragmentListMusic);
        emptyLayout = view.findViewById(R.id.emptyLayout);
        keyWords = view.findViewById(R.id.keyword);

        musicInPlaylistAdapter = new MusicInPlaylistAdapter(musicArrayList, requireContext(), new ItemEvent.MusicItemInPlayListEvent() {
            @Override
            public void onItemClick(int index) {
                if (musicInPlaylistAdapter.getItemCount() > 0) {
                    musicPlayer.pauseSong(musicPlayer.getCurrentPositionSong());
                    musicPlayer.clearPlaylist();
                    musicPlayer.setPlayList(musicInPlaylistAdapter.getListMusic());
                    musicPlayer.setMusicAtPosition(index);
                    try {
                        musicPlayer.setStateMusicPlayer(MusicPlayer.MUSIC_PLAYER_STATE_PLAYING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicPlayer.setCurrentMode(MusicPlayer.MUSIC_PLAYER_MODE_ONLINE);
                    saveCurrentMusic(musicPlayer, musicArrayList.get(index).getGenresId(), PLAYLIST_TYPE_GENRES);
                    Log.d(TAG, "onClick: " + musicPlayer.getStateMusicPlayer());
                    startActivity(new Intent(requireContext(), MainActivity.class));
                    startServiceMusic(musicPlayer.getPlayListMusic(), MusicPlayer.MUSIC_PLAYER_ACTION_RESET_PLAYLIST, musicPlayer.getCurrentMode());
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
        if (MoreMusicActivity.tempGenres != null) {
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
            }, 20, true, MoreMusicActivity.tempGenres.getId(), new MusicDAO.GetTopMusicByGenres() {
                @Override
                public void onGetTopByGenresCallBack(ArrayList<Music> list) {
                    Log.d(TAG, "onGetTopByGenresCallBack: " + list.size());
                    musicInPlaylistAdapter.setListMusic(list);
                    musicArrayList = list;
                    if (list.size() <= 0) {
                        keyWords.setText(MoreMusicActivity.tempGenres.getName());
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            musicDAO.getTopMusicListen(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {
                    progressBar.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 20, new MusicDAO.GetTopMusicListener() {
                @Override
                public void onGetTopMusicCallback(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                    musicArrayList = list;
                    if (list.size() <= 0) {
                        keyWords.setText(MoreMusicActivity.tempGenres.getName());
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public boolean checkUniqueSong(String songName) {
        ArrayList<Music> list = (ArrayList<Music>) SongRecentDatabase.getInstance(requireContext()).musicRecentDAO().checkSong(songName);
        return list.size() <= 0;
    }

    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist, String typePlayList) {
        SharedPreferences.Editor editor = MusicPlayerStorage.getInstance(requireContext()).edit();
        editor.putInt(KEY_SONG_INDEX, musicPlayer.getCurrentIndexSong());
        editor.putBoolean(KEY_IS_DECREASE, true);
        editor.putInt(KEY_LIMIT, 20);
        editor.putString(KEY_PLAYLIST_TYPE, typePlayList);
        Log.d(TAG, "saveCurrentMusic: " + typePlayList);
        Log.d(TAG, "saveCurrentMusic: " + idPlaylist);
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        editor.apply();
    }


    public void startServiceMusic(ArrayList<Music> listMusic, int action, String mode) {
        Intent serviceMusic = new Intent(requireContext(), MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_PLAYLIST, listMusic);
        serviceMusic.putExtra(KEY_MODE_MUSIC_PLAYER, mode);
        serviceMusic.putExtra(KEY_SONG_INDEX, MusicPlayerStorage.getInstance(requireContext()).getInt(KEY_SONG_INDEX, 0));
        requireContext().startService(serviceMusic);
    }
}