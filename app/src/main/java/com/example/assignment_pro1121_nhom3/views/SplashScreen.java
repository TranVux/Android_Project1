package com.example.assignment_pro1121_nhom3.views;

import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    public static MusicPlayer musicPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);


        // lấy dữ liệu mẫu
        MusicDAO musicDAO = new MusicDAO();
        musicDAO.getTopMusic10(new MusicDAO.GetTop10Listener() {
            @Override
            public void onGetTop10Callback(ArrayList<Music> list) {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra("fetchData", list);
                startActivity(intent);

                musicPlayer = MusicPlayer.getInstance(list);

                finish();
            }
        });
    }
}