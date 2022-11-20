package com.example.assignment_pro1121_nhom3.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionInflater;

import com.example.assignment_pro1121_nhom3.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Transition transition = TransitionInflater.from(getApplicationContext()).inflateTransition(android.R.transition.no_transition);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);
    }

}