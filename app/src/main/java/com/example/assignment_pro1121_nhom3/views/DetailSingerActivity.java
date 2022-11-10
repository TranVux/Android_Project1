package com.example.assignment_pro1121_nhom3.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Singer;

public class DetailSingerActivity extends AppCompatActivity {
    private TextView singerNameTopBar, singerNameMain;
    private ImageView singerBg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_singer);
        singerNameTopBar = findViewById(R.id.singerNameTopBar);
        singerNameMain = findViewById(R.id.singerNameMain);
        singerBg = findViewById(R.id.singerThumbnail);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Singer receiverSinger = (Singer) getIntent().getSerializableExtra("singer");
        if (receiverSinger != null) {
            singerNameMain.setText(receiverSinger.getName());
            singerNameTopBar.setText(receiverSinger.getName());
            Glide.with(getApplicationContext())
                    .load(receiverSinger.getAvtUrl())
                    .centerCrop()
                    .into(singerBg);
        }

    }
}