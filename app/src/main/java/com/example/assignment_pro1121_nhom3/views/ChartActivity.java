package com.example.assignment_pro1121_nhom3.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.ChartPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.fragments.BottomSheet;
import com.example.assignment_pro1121_nhom3.models.Music;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChartPlaylistAdapter adapter;
    public final static int ITEM_HEIGHT = 40;
    public final static int MARGIN_ITEM = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_charts);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);

        //text view has color gradient
        TextView textView = findViewById(R.id.labelBxh);
        textView.setText("#BXH".toUpperCase());

        TextPaint paint = textView.getPaint();
        float width = paint.measureText("#BXH");

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#13DCFE"),
                        Color.parseColor("#716BFE"),
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
        //

        adapter = new ChartPlaylistAdapter(this, new ChartPlaylistAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music) {
                Toast.makeText(ChartActivity.this, "Tới activity player", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoreButtonClick(Music music) {
                BottomSheet bottomSheet = new BottomSheet();
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setData(getListMusic());
        recyclerView.setAdapter(adapter);
    }

    private List<Music> getListMusic() {
        List<Music> list = new ArrayList<>();
        list.add(new Music("1", "Có chắc", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("2", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("3", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("4", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("5", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("6", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("7", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("8", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("9", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        list.add(new Music("10", "CCYLD", null, "https://upload.wikimedia.org/wikipedia/vi/thumb/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg/220px-S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg", 1668311586, 1668311586, "Sơn Tùng M-TP", "23133131313131", 2131L, "564645646464644654"));
        return list;
    }
}