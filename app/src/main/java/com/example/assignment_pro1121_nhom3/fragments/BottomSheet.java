package com.example.assignment_pro1121_nhom3.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.dao.SingerDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.example.assignment_pro1121_nhom3.views.DetailSingerActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.checkerframework.common.subtyping.qual.Bottom;

public class BottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    public static final String TAG = BottomSheet.class.getSimpleName();

    LinearLayout btnAddToPlaylist, btnGoToArtist;
    ImageView musicThumbnail;
    TextView musicName, artistName, btnClose;
    Music currentSong;
    SingerDAO singerDAO;

    public static BottomSheet newInstance(Music music) {
        BottomSheet mBottomSheet = new BottomSheet();
        Bundle data = new Bundle();
        data.putSerializable(Constants.KEY_MUSIC, music);
        mBottomSheet.setArguments(data);
        return mBottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        Bundle data = getArguments();
        singerDAO = new SingerDAO();
        if (data != null) {
            currentSong = (Music) data.getSerializable(Constants.KEY_MUSIC);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottomsheet_chart, null);
        bottomSheetDialog.setContentView(view);
        init(view);
        setData(currentSong);
        setClick();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        return bottomSheetDialog;
    }

    public void init(View view) {
        btnAddToPlaylist = view.findViewById(R.id.btnAddToPlaylist);
        btnClose = view.findViewById(R.id.btnClose);
        artistName = view.findViewById(R.id.artistName);
        musicName = view.findViewById(R.id.musicName);
        btnGoToArtist = view.findViewById(R.id.btnGoToArtist);
        musicThumbnail = view.findViewById(R.id.musicThumbnail);
    }

    public void setData(Music music) {
        artistName.setText(CapitalizeWord.CapitalizeWords(music.getSingerName()));
        musicName.setText(CapitalizeWord.CapitalizeWords(music.getName()));
        Glide.with(requireContext()).load(music.getThumbnailUrl()).apply(new RequestOptions().override(63, 63)).into(musicThumbnail);
    }

    public void setClick() {
        btnGoToArtist.setOnClickListener(this);
        btnAddToPlaylist.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddToPlaylist: {
                addToPlaylist();
                break;
            }
            case R.id.btnGoToArtist: {
                goToArtist();
                break;
            }
        }
    }

    private void addToPlaylist() {
        BottomSheetAddPlaylist bottomSheetAddPlaylist = BottomSheetAddPlaylist.newInstace(currentSong);
        bottomSheetAddPlaylist.show(getParentFragmentManager(), TAG);
    }

    public void goToArtist() {
        singerDAO.getSinger(new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {

            }

            @Override
            public void afterGetData() {

            }
        }, currentSong.getSingerId(), new SingerDAO.ReadItemSinger() {
            @Override
            public void onReadItemSingerCallback(Singer singer) {
                Intent singerDetailIntent = new Intent(requireContext(), DetailSingerActivity.class);
                singerDetailIntent.putExtra("singer", singer);
                startActivity(singerDetailIntent);
            }
        });
    }
}
