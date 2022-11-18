package com.example.assignment_pro1121_nhom3.fragments;

import android.app.Dialog;
import android.content.Intent;
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
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.example.assignment_pro1121_nhom3.views.SplashScreen;
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

        handleViewOfBottomSheet(bottomSheetDialog);
        return bottomSheetDialog;
    }

    public void handleViewOfBottomSheet(BottomSheetDialog bottomSheetDialog) {
        musicInPlaylistAdapter = new MusicInPlaylistAdapter(listMusic, requireContext(), new ItemEvent.MusicItemInPlayListEvent() {
            @Override
            public void onItemClick(int index) {
                Intent musicIntent = new Intent(requireContext(), MusicPlayerService.class);
                musicIntent.putExtra("action", MusicPlayer.MUSIC_PLAYER_ACTION_GO_TO_SONG);
                musicIntent.putExtra(Constants.KEY_SONG_INDEX, index);
                requireContext().startService(musicIntent);
                bottomSheetDialog.dismiss();
            }

            @Override
            public void onMoreClick(Music music) {

            }
        });

        musicListBottomSheet.setAdapter(musicInPlaylistAdapter);

    }
}
