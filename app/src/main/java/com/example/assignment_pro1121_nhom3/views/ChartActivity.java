package com.example.assignment_pro1121_nhom3.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.ChartPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.fragments.BottomSheet;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.utils.RoundedBarChart;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;


import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    public static final String TAG = ChartActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ChartPlaylistAdapter adapter;
    private MusicDAO musicDAO;
    private RoundedBarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        //đổi màu chữ status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        musicDAO = new MusicDAO();

        setContentView(R.layout.activity_charts);
        recyclerView = findViewById(R.id.recyclerview);
        NestedScrollView nestedScrollView = findViewById(R.id.scrollView);
        barChart = findViewById(R.id.barChart);

        //set margin cho nestedScrollview

        //
        //text view has color gradient
        TextView textView = findViewById(R.id.labelBxh);
        textView.setText("#BXH".toUpperCase());

        TextPaint paint = textView.getPaint();
        float width = paint.measureText("#BXH");
        //

        // fix lỗi item của recyclerview bị khuất
        Log.d("TAG>>>>>>>>>", "onCreate: " + getNavigationBarHeight());
        setMargins(nestedScrollView, 0, 0, 0, getNavigationBarHeight() - 5);

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#13DCFE"),
                        Color.parseColor("#716BFE"),
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
        //

        //adapter cho top
        adapter = new ChartPlaylistAdapter(this, new ChartPlaylistAdapter.ItemChartEvent() {
            @Override
            public void onItemClick(Music music) {
                Toast.makeText(ChartActivity.this, "Tới activity player", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoreButtonClick(Music music) {
                BottomSheet bottomSheet = BottomSheet.newInstance(music);
                bottomSheet.show(getSupportFragmentManager(), TAG);
            }
        });

        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        musicDAO.getTopMusic10(new MusicDAO.GetTop10Listener() {
            @Override
            public void onGetTop10Callback(ArrayList<Music> list) {
                adapter.setData(list);
            }
        });

        //chart
        setUpBarChart();
        //

    }

    public void setUpBarChart() {
        ArrayList<BarEntry> barChartEntry = new ArrayList<>();
        barChartEntry.add(new BarEntry(1f, 5, R.drawable.img));
        barChartEntry.add(new BarEntry(2f, 4, R.drawable.img));
        barChartEntry.add(new BarEntry(3f, 6, R.drawable.img));
        barChartEntry.add(new BarEntry(4f, 2, R.drawable.img));
        barChartEntry.add(new BarEntry(5f, 8, R.drawable.img));
        barChartEntry.add(new BarEntry(6f, 9, R.drawable.img));

        BarDataSet barDataSet = new BarDataSet(barChartEntry, "");
        barDataSet.setValueTextSize(13f);
        barDataSet.setStackLabels(new String[]{"Em của ngày hôm qua", "Yêu 1", "Yêu 2", "Yêu 3", "Yêu 4", "Yêu 5"});

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(.3f);
        barData.setDrawValues(false);

        barChart.setData(barData);
        barChart.animateY(1000, Easing.EaseInOutCirc);

        barChart.getXAxis().setDrawAxisLine(false);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDescription(null);
        barChart.setTouchEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setRadius(25);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(13f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setYOffset(5);
        xAxis.setDrawLabels(true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);

        barChart.invalidate();
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public int getNavigationBarHeight() {
        boolean hasMenuKey = ViewConfiguration.get(ChartActivity.this).hasPermanentMenuKey();
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}