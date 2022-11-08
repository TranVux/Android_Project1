package com.example.assignment_pro1121_nhom3.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.fragments.HomeFragment;
import com.example.assignment_pro1121_nhom3.fragments.PlayerFragment;
import com.example.assignment_pro1121_nhom3.fragments.UserFragment;
import com.example.assignment_pro1121_nhom3.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    int state = 1;
    HomeFragment homeFragment;
    UserFragment userFragment;
    PlayerFragment playerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // đổi màu của status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        //
        // khai báo các fragment
        homeFragment = new HomeFragment();
        userFragment = new UserFragment();
        playerFragment = new PlayerFragment();

        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setItemIconTintList(null);
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(1);
        View customRadio = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_item_bottom_navigation_player, bottomNavigationMenuView, false);
        View customButtonPlay = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_item_bottom_navigation_button_play, bottomNavigationMenuView, false);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, playerFragment, "PlayerFragment").commit();
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
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, new HomeFragment(), "FragmentHome").commit();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.home: {
                        customRadio.setVisibility(View.VISIBLE);
                        customButtonPlay.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, homeFragment, "FragmentHome").commit();
                        break;
                    }
                    case R.id.player:{
                        player.performClick();
                        break;
                    }
                    case R.id.user: {
                        customRadio.setVisibility(View.VISIBLE);
                        customButtonPlay.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, userFragment, "FragmentHome").commit();
                        break;
                    }
                }
                return false;
            }
        });
    }

//    public void fixBottomNavigation(){
//        if (Build.VERSION.SDK_INT >= 30) {
//            // Root ViewGroup of my activity
//            val root = findViewById<ConstraintLayout>(R.id.root)
//
//                    ViewCompat.setOnApplyWindowInsetsListener(root) { view, windowInsets ->
//
//                    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
//
//                // Apply the insets as a margin to the view. Here the system is setting
//                // only the bottom, left, and right dimensions, but apply whichever insets are
//                // appropriate to your layout. You can also update the view padding
//                // if that's more appropriate.
//
//                view.layoutParams =  (view.layoutParams as FrameLayout.LayoutParams).apply {
//                    leftMargin = insets.left
//                    bottomMargin = insets.bottom
//                    rightMargin = insets.right
//                }
//
//                // Return CONSUMED if you don't want want the window insets to keep being
//                // passed down to descendant views.
//                WindowInsetsCompat.CONSUMED
//            }
//
//        }
//    }
}