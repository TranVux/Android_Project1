package com.example.assignment_pro1121_nhom3.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MusicInPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class MyBottomSheetDialog extends BottomSheetDialogFragment {
    public static final String KEY_DATA_BOTTOM_SHEET_DIALOG = "KEY_DATA_BOTTOM_SHEET_DIALOG";
    public final String TAG = MyBottomSheetDialog.class.getSimpleName();
    private ArrayList<Music> listMusic;
    private MusicInPlaylistAdapter musicInPlaylistAdapter;
    RecyclerView musicListBottomSheet;
    TextView btnClose;

    public static MyBottomSheetDialog newInstance(ArrayList<Music> listMusic) {
        MyBottomSheetDialog bottomSheetDialog = new MyBottomSheetDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DATA_BOTTOM_SHEET_DIALOG, listMusic);

        bottomSheetDialog.setArguments(bundle);
        return bottomSheetDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        if (data != null) {
            listMusic = (ArrayList<Music>) data.get(KEY_DATA_BOTTOM_SHEET_DIALOG);
            Log.d(TAG, "onCreate: " + listMusic.size());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.player_bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(view);
        musicListBottomSheet = view.findViewById(R.id.nextMusicList);
        btnClose = view.findViewById(R.id.btnClose);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        handleViewOfBottomSheet();
        return bottomSheetDialog;
    }

    public void handleViewOfBottomSheet() {
        musicInPlaylistAdapter = new MusicInPlaylistAdapter(listMusic, requireContext(), new ItemEvent.MusicItemInPlayListEvent() {
            @Override
            public void onItemClick(Music music) {
                Toast.makeText(requireContext(), music.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoreClick(Music music) {
                Toast.makeText(requireContext(), "More btn " + music.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        musicListBottomSheet.setAdapter(musicInPlaylistAdapter);

    }
}