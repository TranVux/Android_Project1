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
import android.widget.LinearLayout;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.SingerAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.dao.SingerDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.interfaces.PaginationScrollListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.views.DetailSingerActivity;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class FragmentTopView extends Fragment {

    public static String TAG = AllSingerFragment.class.getSimpleName();
    private ArrayList<Singer> listSinger = new ArrayList<>();
    private RecyclerView rclSinger;
    private SingerAdapter singerAdapter;
    private SingerDAO singerDAO;
    private LinearLayout layoutLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void init(View view) {
        layoutLoading = view.findViewById(R.id.progressBarLayout);
        rclSinger = view.findViewById(R.id.listSinger);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        // xử lý adapter
        singerAdapter = new SingerAdapter(requireContext(), new ItemEvent.SingerItemEvent() {
            @Override
            public void onItemClick(Singer singer) {
                Intent singerDetailIntent = new Intent(requireContext(), DetailSingerActivity.class);
                singerDetailIntent.putExtra("singer", singer);
                startActivity(singerDetailIntent);
            }
        });

        rclSinger.setLayoutManager(manager);
        rclSinger.setAdapter(singerAdapter);

        singerDAO = new SingerDAO();
        // lấy dữ liệu từ firebase

        getData();
    }

    public void getData() {
        singerDAO.getTopSinger(new SingerDAO.GetTopSinger() {
            @Override
            public void onGetTopSingersSuccess(ArrayList<Singer> list) {
                listSinger = list;
                for (Singer singer : list) {
                    Log.d(TAG, "onGetTopSingersSuccess: " + singer.getName());
                }
                singerAdapter.setListSinger(listSinger);
            }
        }, new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {
                layoutLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterGetData() {
                layoutLoading.setVisibility(View.GONE);
            }
        });
    }
}