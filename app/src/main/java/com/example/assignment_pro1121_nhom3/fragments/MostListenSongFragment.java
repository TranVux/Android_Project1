package com.example.assignment_pro1121_nhom3.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MusicInPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.adapters.SingerAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.views.DetailSingerActivity;
import com.example.assignment_pro1121_nhom3.views.MoreMusicActivity;

import java.util.ArrayList;
import java.util.List;

public class MostListenSongFragment extends Fragment {
    private static final String TAG = MostListenSongFragment.class.getName();
    private ArrayList<Music> musicArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    public MusicInPlaylistAdapter musicInPlaylistAdapter;
    private MusicDAO musicDAO;
    ProgressBar progressBar;
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
        musicInPlaylistAdapter = new MusicInPlaylistAdapter(musicArrayList, requireContext(), new ItemEvent.MusicItemInPlayListEvent() {
            @Override
            public void onItemClick(Music music) {
                Toast.makeText(requireContext(), music.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoreClick(Music music) {
                Toast.makeText(requireContext(), "btnMore" + music.getName() , Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(musicInPlaylistAdapter);
        musicDAO = new MusicDAO();
        if(MoreMusicActivity.valueChip.equalsIgnoreCase("Tất cả")){
            musicDAO.getTopMusicListen(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 20, new MusicDAO.GetTopMusicListener() {
                @Override
                public void onGetTopMusicCallback(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                }
            });
        }else if(MoreMusicActivity.valueChip.equalsIgnoreCase("RAP")){
            musicDAO.getTopMusicBuyGenre(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 20,true, "6E5s9Ng3le1zlmSddGKB", new MusicDAO.GetTopMusicBuyGenreListener() {
                @Override
                public void onGetTopMusicBuyGenreCallback(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                }
            });

        }else if(MoreMusicActivity.valueChip.equalsIgnoreCase("Lofi")){
            musicDAO.getTopMusicBuyGenre(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 20,true, "BXz0IZNAAfk4m56GPfP8", new MusicDAO.GetTopMusicBuyGenreListener() {
                @Override
                public void onGetTopMusicBuyGenreCallback(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                }
            });
        }else if(MoreMusicActivity.valueChip.equalsIgnoreCase("Nhạc Nhật")){
            musicDAO.getTopMusicBuyGenre(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 20,true, "p9bpS7Br6rS5BoPsh23e", new MusicDAO.GetTopMusicBuyGenreListener() {
                @Override
                public void onGetTopMusicBuyGenreCallback(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                }
            });
        }else if(MoreMusicActivity.valueChip.equalsIgnoreCase("US - UK")){
            musicDAO.getTopMusicBuyGenre(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 20,true, "qm9Usui9Nm2znA38objj", new MusicDAO.GetTopMusicBuyGenreListener() {
                @Override
                public void onGetTopMusicBuyGenreCallback(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                }
            });
        }else if(MoreMusicActivity.valueChip.equalsIgnoreCase("Nhạc Trẻ")){
            musicDAO.getTopMusicBuyGenre(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 20,true, "SSrKhRc2FHzGIyLxjU5w", new MusicDAO.GetTopMusicBuyGenreListener() {
                @Override
                public void onGetTopMusicBuyGenreCallback(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                }
            });
        }if(MoreMusicActivity.valueChip.equalsIgnoreCase("EDM")){
            musicDAO.getTopMusicBuyGenre(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 20,true, "XY5TrJIEKdrsEvNHS5QB", new MusicDAO.GetTopMusicBuyGenreListener() {
                @Override
                public void onGetTopMusicBuyGenreCallback(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                }
            });
        }if(MoreMusicActivity.valueChip.equalsIgnoreCase("Nhạc Hoa")){
            musicDAO.getTopMusicBuyGenre(new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {

                }

                @Override
                public void afterGetData() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 20,true, "f1gSSB0oC4u88hP4mgES", new MusicDAO.GetTopMusicBuyGenreListener() {
                @Override
                public void onGetTopMusicBuyGenreCallback(ArrayList<Music> list) {
                    musicInPlaylistAdapter.setListMusic(list);
                }
            });
        }
//        musicArrayList = FakeData();
    }

    private ArrayList<Music> FakeData() {

        ArrayList<Music> list = new ArrayList<>();

        list.add(new Music("2OfAAhxDFTf3TqlKu4AM",
                "Nơi này có Anh",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://photo-resize-zmp3.zmdcdn.me/w600_r1x1_webp/covers/c/b/cb61528885ea3cdcd9bdb9dfbab067b1_1504988884.jpg",
                0L,
                0L,
                "Sơn Tùng",
                "",
                1200,""));
        list.add(new Music("2OfAAhxDFTf3TqlKu4AM",
                "Big City Boi",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://data.chiasenhac.com/data/cover/124/123404.jpg",
                0L,
                0L,
                "Binz",
                "",
                2000,""));
        list.add(new Music("2OfAAhxDFTf3TqlKu4AM",
                "Sài gòn đau lòng quá",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://data.chiasenhac.com/data/cover/124/123404.jpg",
                0L,
                0L,
                "Hứa Kim Tuyền",
                "",
                2000,""));
        return list;
    }


}