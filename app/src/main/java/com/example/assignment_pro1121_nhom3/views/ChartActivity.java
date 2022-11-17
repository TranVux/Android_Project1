package com.example.assignment_pro1121_nhom3.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.ChartPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.fragments.BottomSheet;
import com.example.assignment_pro1121_nhom3.models.Music;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChartPlaylistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        recyclerView = findViewById(R.id.recyclerview);
        adapter = new ChartPlaylistAdapter(this, new ChartPlaylistAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music) {
                BottomSheet bottomSheet = new BottomSheet();
                bottomSheet.show(getSupportFragmentManager(),"TAG");
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setData(getListMusic());

        recyclerView.setAdapter(adapter);

    }

    private List<Music> getListMusic() {
        List<Music> list = new ArrayList<>();
        list.add(new Music("1", R.drawable.img, "Có chắc yêu là đây", "Sơn Tùng MT-P", 1200L));
        list.add(new Music("2", R.drawable.img, "CCYLD", "MT-P", 1200L));
        list.add(new Music("3", R.drawable.img, "CCYLD", "MT-P", 1200L));
        list.add(new Music("4", R.drawable.img, "CCYLD", "MT-P", 1200L));
        list.add(new Music("5", R.drawable.img, "CCYLD", "MT-P", 1200L));
        list.add(new Music("6", R.drawable.img, "CCYLD", "MT-P", 1200L));
        list.add(new Music("7", R.drawable.img, "CCYLD", "MT-P", 1200L));
        list.add(new Music("8", R.drawable.img, "CCYLD", "MT-P", 1200L));
        list.add(new Music("9", R.drawable.img, "CCYLD", "MT-P", 1200L));
        list.add(new Music("10", R.drawable.img, "CCYLD", "MT-P", 1200L));
        return list;
    }
}