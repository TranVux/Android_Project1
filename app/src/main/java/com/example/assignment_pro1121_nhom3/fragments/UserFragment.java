package com.example.assignment_pro1121_nhom3.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.ChartPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.adapters.SingerAdapter;
import com.example.assignment_pro1121_nhom3.adapters.UserFavoritesListAdapter;
import com.example.assignment_pro1121_nhom3.dao.SingerDAO;
import com.example.assignment_pro1121_nhom3.interfaces.HandleChangeColorBottomNavigation;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.views.ChartActivity;
import com.example.assignment_pro1121_nhom3.views.DetailSingerActivity;
import com.example.assignment_pro1121_nhom3.views.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserFavoritesListAdapter adapter;
    private List<Playlist> list;

    public static String TAG = UserFragment.class.getSimpleName();
    // xử lý đổi màu bottom navigation
    HandleChangeColorBottomNavigation handleChangeColorBottomNavigation;

    public UserFragment(HandleChangeColorBottomNavigation handleChangeColorBottomNavigation) {
        this.handleChangeColorBottomNavigation = handleChangeColorBottomNavigation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        handleChangeColorBottomNavigation.toColor();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        recyclerView = view.findViewById(R.id.playlistMusic);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new UserFavoritesListAdapter(list, getContext(), new UserFavoritesListAdapter.ItemFavoriteslistEvent() {
            @Override
            public void onItemclick(Playlist playlist) {
                Toast.makeText(getContext(), "Tới activity playlist", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(manager);
        adapter.setData(getListMusic());
        recyclerView.setAdapter(adapter);
    }

    public void init(View view) {
        list = new ArrayList<>();
        // xử lý adapter
    }
    private ArrayList<Playlist> getListMusic() {
        ArrayList<Playlist> list = new ArrayList<>();
        list.add(new Playlist("https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", "Ngủ", "20 bài hát"));
        list.add(new Playlist("https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", "Ngủ", "20 bài hát"));
        list.add(new Playlist("https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", "Ngủ", "20 bài hát"));
        list.add(new Playlist("https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", "Ngủ", "20 bài hát"));
        list.add(new Playlist("https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", "Ngủ", "20 bài hát"));
        list.add(new Playlist("https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", "Ngủ", "20 bài hát"));
        list.add(new Playlist("https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", "Ngủ", "20 bài hát"));

        return list;
    }
}