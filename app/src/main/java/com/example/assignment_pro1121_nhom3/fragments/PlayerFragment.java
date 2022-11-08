package com.example.assignment_pro1121_nhom3.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.interfaces.EventInterface;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class PlayerFragment extends Fragment implements View.OnClickListener {

    public static String TAG = PlayerFragment.class.getSimpleName();
    // BlurView sẽ làm mở những đối tượng ở ngoài phạm vi của nó
    BlurView backgroundFragment;

    //Nút thêm dùng để mở lên danh sách bài hát tiếp theo
    TextView labelMoreListMusic;
    ImageView icMoreListMusic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // thiết lập thuộc tính ban đầu, ánh xạ view
        init(view);
        //

        // bắt sự kiện click cho view
        setEventClick();
        //

        //tạo background blur cho fragment
        blurBackgroundFragment();
    }

    private void init(View view) {
        backgroundFragment = view.findViewById(R.id.blurView);
        labelMoreListMusic = view.findViewById(R.id.moreLabel);
        icMoreListMusic = view.findViewById(R.id.icMoreMusic);
//        playerBottomSheet = view.findViewById(R.id.player_bottom_sheet);
//
//        bottomSheetBehavior = BottomSheetBehavior.from(playerBottomSheet);
    }

    public void setEventClick() {
        labelMoreListMusic.setOnClickListener(this);
        icMoreListMusic.setOnClickListener(this);
    }

    private void blurBackgroundFragment() {
        // làm mở ảnh
        float radius = 15f;
        View decorView = requireActivity().getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        backgroundFragment.setupWith(rootView, new RenderScriptBlur(requireContext())) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);
        //
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    public void handleShowBottomSheet() {
        //thêm dữ liệu cho listPlaylist

        MyBottomSheetDialog bottomSheetDialog = MyBottomSheetDialog.newInstance(FakeDataBottomSheet());
        bottomSheetDialog.show(getParentFragmentManager(), "MyBottomSheet");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.moreLabel:
            case R.id.icMoreMusic: {
                handleShowBottomSheet();
                break;
            }
            default: {
                break;
            }
        }
    }

    public ArrayList<Music> FakeDataBottomSheet() {
        ArrayList<Music> listRecentPublish = new ArrayList<>();
        //thêm dữ liệu cho listRecentPublish
        listRecentPublish.add(new Music("2OfAAhxDFTf3TqlKu4AM", "Nơi này có anh",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://photo-resize-zmp3.zmdcdn.me/w600_r1x1_webp/covers/c/b/cb61528885ea3cdcd9bdb9dfbab067b1_1504988884.jpg",
                1667278226, 1667278226, "Sơn tùng M-TP", "1CV6SRGg7uxj0W1bsBu8", 1011, "SSrKhRc2FHzGIyLxjU5w"));
        listRecentPublish.add(new Music("2OfAAhxDFTf3TqlKu4AM", "Nơi này có anh",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://data.chiasenhac.com/data/cover/124/123404.jpg",
                1667278226, 1667278226, "Binz", "1CV6SRGg7uxj0W1bsBu8", 10, "SSrKhRc2FHzGIyLxjU5w"));
        listRecentPublish.add(new Music("2OfAAhxDFTf3TqlKu4AM", "Big City Boi",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://data.chiasenhac.com/data/cover/118/117188.jpg",
                1667278226, 1667278226, "MIN", "1CV6SRGg7uxj0W1bsBu8", 101, "SSrKhRc2FHzGIyLxjU5w"));
        listRecentPublish.add(new Music("2OfAAhxDFTf3TqlKu4AM", "Chạy khỏi thế giới này",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://data.chiasenhac.com/data/cover/169/168211.jpg",
                1667278226, 1667278226, "DALAB", "1CV6SRGg7uxj0W1bsBu8", 1011, "SSrKhRc2FHzGIyLxjU5w"));
        return listRecentPublish;
    }
}