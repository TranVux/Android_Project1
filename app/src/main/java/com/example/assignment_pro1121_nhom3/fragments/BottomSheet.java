package com.example.assignment_pro1121_nhom3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment_pro1121_nhom3.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet extends BottomSheetDialogFragment {

    LinearLayout addplaylistchart;

    public BottomSheet(){
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_chart,container,false);

        addplaylistchart = view.findViewById(R.id.Addplaylistchart);
        addplaylistchart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetAddplaylist bottomSheetAddplaylist = new BottomSheetAddplaylist();
                bottomSheetAddplaylist.show(getFragmentManager(),"TAG");
            }
        });


        return view;
    }
}
