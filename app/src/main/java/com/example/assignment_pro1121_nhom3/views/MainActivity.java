package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.fragments.HomeFragment;
import com.example.assignment_pro1121_nhom3.fragments.PlayerFragment;
import com.example.assignment_pro1121_nhom3.fragments.UserFragment;
import com.example.assignment_pro1121_nhom3.interfaces.HandleChangeColorBottomNavigation;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements HandleChangeColorBottomNavigation {
    BottomNavigationView bottomNavigation;
    int state = 1;
    HomeFragment homeFragment;
    UserFragment userFragment;
    PlayerFragment playerFragment;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // đổi màu của status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        //
        // khai báo các fragment
        homeFragment = new HomeFragment(this);
        userFragment = new UserFragment(this);
        playerFragment = new PlayerFragment(this);
        setContentView(R.layout.activity_main);

        // bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setItemIconTintList(null);
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(1);
        View customRadio = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.custom_item_bottom_navigation_player, bottomNavigationMenuView, false);
        View customButtonPlay = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.custom_item_bottom_navigation_button_play, bottomNavigationMenuView, false);
        itemView.addView(customRadio);
        itemView.addView(customButtonPlay);
        customButtonPlay.setVisibility(View.GONE);
        ImageView player = customRadio.findViewById(R.id.player);
        ImageView btnPlay = customButtonPlay.findViewById(R.id.btnPlay);

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customRadio.setVisibility(View.GONE);
                customButtonPlay.setVisibility(View.VISIBLE);
                bottomNavigation.getMenu().getItem(1).setChecked(true);
                getSupportFragmentManager().beginTransaction()
                        // .setCustomAnimations(
                        // R.anim.slide_enter_right_to_left, // enter
                        // R.anim.slide_exit_right_to_left, // exit
                        // R.anim.slide_enter_left_to_right, // popEnter
                        // R.anim.slide_exit_left_to_right // popExit
                        // )
                        .replace(R.id.fragmentLayout, playerFragment, "PlayerFragment").commit();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 1) {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    state = 0;
                } else {
                    btnPlay.setImageResource(R.drawable.ic_play);
                    state = 1;
                }
            }
        });

        // set layout mặc định cho fragment là màn hình home
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, homeFragment, "FragmentHome").commit();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.home: {
                        customRadio.setVisibility(View.VISIBLE);
                        customButtonPlay.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction()
                                // .setCustomAnimations(
                                // R.anim.slide_enter_right_to_left, // enter
                                // R.anim.slide_exit_right_to_left, // exit
                                // R.anim.slide_enter_left_to_right, // popEnter
                                // R.anim.slide_exit_left_to_right // popExit
                                // )
                                .replace(R.id.fragmentLayout, homeFragment, "FragmentHome").commit();
                        break;
                    }
                    case R.id.player: {
                        player.performClick();
                        break;
                    }
                    case R.id.user: {
                        customRadio.setVisibility(View.VISIBLE);
                        customButtonPlay.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction()
                                // .setCustomAnimations(
                                // R.anim.slide_enter_right_to_left, // enter
                                // R.anim.slide_exit_right_to_left, // exit
                                // R.anim.slide_enter_left_to_right, // popEnter
                                // R.anim.slide_exit_left_to_right // popExit
                                // )
                                .replace(R.id.fragmentLayout, userFragment, "FragmentUser").commit();
                        break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void toTransparent() {
        bottomNavigation
                .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
    }

    @Override
    public void toColor() {
        bottomNavigation
                .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_bottom_navigation));
    }
}