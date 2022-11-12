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
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.Objects;

public class AllSingerFragment extends Fragment {
    public static String TAG = AllSingerFragment.class.getSimpleName();
    private ArrayList<Singer> listSinger = new ArrayList<>();
    private RecyclerView rclSinger;
    private SingerAdapter singerAdapter;
    private SingerDAO singerDAO;
    private LinearLayout layoutLoading;
    long countOfData = 0;
    boolean isLoading = false;
    int amountOfQuery = 0;
    int limitQuery = 10;
    int currentQueryAmount = 0;
    Query nextListQuery = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_singer, container, false);
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

        //xử lý scroll recyclerview
        rclSinger.addOnScrollListener(new PaginationScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                int currentItemInList = manager.findLastCompletelyVisibleItemPosition();
//                int totalItem = manager.getItemCount();
//                if (!isLoading && currentQueryAmount < amountOfQuery && totalItem <= (currentItemInList + limitQuery)) {
//                    getData();
//                    isLoading = true;
//                }
                int visibleItemCount = manager.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstItemVisible = manager.findFirstVisibleItemPosition();

                if (isLoading || currentQueryAmount >= amountOfQuery) return;
                if (firstItemVisible >= 0 && (visibleItemCount + firstItemVisible) >= totalItemCount) {
                    getData();
                    isLoading = true;
                }
            }
        });

        //
        singerDAO = new SingerDAO();
        // lấy dữ liệu từ firebase

        singerDAO.getCountDocumentSingers(new SingerDAO.GetCountDocument() {
            @Override
            public void onGetCountSuccess(long count) {
                countOfData = count;
                if (countOfData % limitQuery != 0) {
                    amountOfQuery = (int) Math.round(Math.ceil(countOfData % limitQuery)) + 1;
                } else {
                    amountOfQuery = (int) Math.round(Math.ceil(countOfData % limitQuery));
                }
                Log.d(TAG, "onGetCountSuccess: " + amountOfQuery + " of: " + count);
                getData();
            }

            @Override
            public void onGetCountFailure(Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void getData() {
        singerDAO.getPaginateSinger(nextListQuery, new SingerDAO.GetDataPagination() {
            @Override
            public void onGetSuccess(ArrayList<Singer> list) {
                Log.d(TAG, "onGetSuccess: " + list.size());
                if (currentQueryAmount > 0) {
                    singerAdapter.removeFooterLoading();
                }
                singerAdapter.setListSinger(list);
                currentQueryAmount++;
                isLoading = false;
                Log.d(TAG, "onGetSuccess: " + "amount query: " + currentQueryAmount);
            }

            @Override
            public void getNextQuery(Query query) {
                nextListQuery = query;
            }
        }, new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {
                if (currentQueryAmount > 0) {
                    singerAdapter.addFooterLoading();
                } else if (currentQueryAmount == 0) {
                    layoutLoading.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterGetData() {
                layoutLoading.setVisibility(View.GONE);
            }
        });
    }
}