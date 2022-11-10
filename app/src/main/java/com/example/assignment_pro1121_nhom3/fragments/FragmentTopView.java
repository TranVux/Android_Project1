package com.example.assignment_pro1121_nhom3.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.SingerAdapter;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.views.DetailSingerActivity;

import java.util.ArrayList;


public class FragmentTopView extends Fragment {

    private ArrayList<Singer> listSinger;
    private RecyclerView rclSinger;
    private SingerAdapter singerAdapter;

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
        listSinger = FakeData();
        rclSinger = view.findViewById(R.id.listSinger);
        singerAdapter = new SingerAdapter(listSinger, requireContext(), new ItemEvent.SingerItemEvent() {
            @Override
            public void onItemClick(Singer singer) {
                Intent singerDetailIntent = new Intent(requireContext(), DetailSingerActivity.class);
                singerDetailIntent.putExtra("singer", singer);
                startActivity(singerDetailIntent);
            }
        });
        rclSinger.setAdapter(singerAdapter);
    }

    public ArrayList<Singer> FakeData() {
        ArrayList<Singer> result = new ArrayList<>();
        result.add(new Singer("1", "Sơn tùng M-TP", "https://upload.wikimedia.org/wikipedia/commons/f/fe/Son_Tung_M-TP_1_%282017%29.png", ""));
        result.add(new Singer("2", "Amee", "https://static-images.vnncdn.net/files/publish/2022/5/27/hain7580-102.jpg", ""));
        result.add(new Singer("1", "Binzz", "https://upload.wikimedia.org/wikipedia/commons/2/2c/Binz-bts-chon-3495-159365391024560627410.jpg", ""));
        result.add(new Singer("1", "Emcee L", "https://ss-images.saostar.vn/w800/pc/1609293556815/emceel21-2.jpg", ""));
        result.add(new Singer("1", "Vũ", "https://i.scdn.co/image/ab6761610000e5eb9896fc9a2e28384f2d705c45", ""));
        result.add(new Singer("1", "Phương Ly", "https://anhgaisexy.com/wp-content/uploads/2022/08/hinh-anh-phuong-ly-xinh-dep-nhat.jpg", ""));
        result.add(new Singer("1", "Khói", "https://image.thanhnien.vn/w1024/Uploaded/2022/noktnz/2020_11_10/rapper-khoi_ahhj.jpg", ""));
        result.add(new Singer("1", "chevy", "https://i.scdn.co/image/ab6761610000e5eb99701868c5e1888c70dfcccb", ""));
        return result;
    }
}