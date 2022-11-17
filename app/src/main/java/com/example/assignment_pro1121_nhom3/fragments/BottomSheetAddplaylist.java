package com.example.assignment_pro1121_nhom3.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.assignment_pro1121_nhom3.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class BottomSheetAddplaylist extends BottomSheetDialogFragment {

    TextView newplaylist,btnSave,btnCancel;
    EditText tvNameplaylist;

    public BottomSheetAddplaylist(){
    }
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_add_playlist, container, false);
        newplaylist = view.findViewById(R.id.newplaylist);
        newplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(getContext());
            }
        });
        return view;
    }
    @SuppressLint("MissingInflatedId")
    protected void openDialog(final Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.chart_add_dialog_playlist);

        tvNameplaylist = dialog.findViewById(R.id.tvnameplaylist);
        btnSave = dialog.findViewById(R.id.tvLuu);
        btnCancel = dialog.findViewById(R.id.tvHuy);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
            }
        });

        dialog.show();
    }
}
