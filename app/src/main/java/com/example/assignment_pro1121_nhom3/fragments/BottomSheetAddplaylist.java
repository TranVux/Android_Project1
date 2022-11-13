package com.example.assignment_pro1121_nhom3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment_pro1121_nhom3.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetAddplaylist extends BottomSheetDialogFragment {

    public BottomSheetAddplaylist(){
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_add_playlist, container, false);
        return view;
    }
}
