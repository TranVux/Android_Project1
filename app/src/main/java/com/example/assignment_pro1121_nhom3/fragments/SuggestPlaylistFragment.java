package com.example.assignment_pro1121_nhom3.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.PlayListMusicAdapter;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.views.DetailPlaylistActivity;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

public class SuggestPlaylistFragment extends Fragment {

    public static String TAG = SuggestPlaylistFragment.class.getSimpleName();

    RecyclerView rclSuggestPlaylist;
    ArrayList<Playlist> listPlaylist;
    PlayListMusicAdapter playListMusicAdapter;
    PlaylistDAO playlistDAO;
    ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suggest_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        getData();
    }

    private void init(View view) {
        rclSuggestPlaylist = view.findViewById(R.id.listPlayList);
        progressBar = view.findViewById(R.id.progressBar);

        listPlaylist = new ArrayList<>();
        playlistDAO = new PlaylistDAO();

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(requireContext());
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);

        // xét layout manager cho recycler view
        rclSuggestPlaylist.setLayoutManager(flexboxLayoutManager);

        playListMusicAdapter = new PlayListMusicAdapter(listPlaylist, requireContext(), new ItemEvent.PlaylistItemEvent() {
            @Override
            public void onItemClick(Playlist playlist) {
                Intent detailPlaylistActivity = new Intent(requireContext(), DetailPlaylistActivity.class);
                detailPlaylistActivity.putExtra("playlist", playlist);
                startActivity(detailPlaylistActivity);
            }
        });
        rclSuggestPlaylist.setAdapter(playListMusicAdapter);
    }

    public void getData() {
        playlistDAO.getRecentPublishPlaylist(new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterGetData() {
                progressBar.setVisibility(View.GONE);
            }
        }, new PlaylistDAO.GetRecentPublishPlaylist() {
            @Override
            public void onGetRecentPublishPlaylistSuccess(ArrayList<Playlist> result) {
                playListMusicAdapter.setList(result);
                listPlaylist = result;
            }

            @Override
            public void onGetRecentPublishPlaylistFailure() {
                Log.d(TAG, "onGetRecentPublishPlaylistFailure: ");
            }
        });
    }
}