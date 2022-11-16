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

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);


        // lấy dữ liệu mẫu
        MusicDAO musicDAO = new MusicDAO();
        musicDAO.getTopMusic10(new MusicDAO.GetTop10Listener() {
            @Override
            public void onGetTop10Callback(ArrayList<Music> list) {
//                MainActivity.musicPlayer.setPlayList(list);
//                MainActivity.musicPlayer.start();
//                Log.d(TAG, "onGetTop10Callback: has data " + MainActivity.musicPlayer.getCurrentSong().getName());
//                startServiceMusic(musicPlayer.getCurrentSong(), MUSIC_PLAYER_ACTION_START);
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra("fetchData", list);
                startActivity(intent);
                finish();
            }
        });
    }
}