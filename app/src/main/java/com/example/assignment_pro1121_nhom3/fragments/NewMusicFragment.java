package com.example.assignment_pro1121_nhom3.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MusicInPlaylistAdapter;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;

import java.util.ArrayList;

public class NewMusicFragment extends Fragment {
    private ArrayList<Music> musicArrayList;
    private RecyclerView mRecyclerView;
    private MusicInPlaylistAdapter musicInPlaylistAdapter;

    public NewMusicFragment() {
        // Required empty public constructor
    }

    public static NewMusicFragment newInstance(String param1, String param2) {
        NewMusicFragment fragment = new NewMusicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        musicArrayList = FakeData();
        mRecyclerView = view.findViewById(R.id.newMusicFragmentListMusic);
        musicInPlaylistAdapter = new MusicInPlaylistAdapter(musicArrayList, requireContext(), new ItemEvent.MusicItemInPlayListEvent() {
            @Override
            public void onItemClick(Music music) {
                Toast.makeText(requireContext(), music.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoreClick(Music music) {
                Toast.makeText(requireContext(), "btnMore" + music.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(musicInPlaylistAdapter);
    }

    private ArrayList<Music> FakeData() {
        ArrayList<Music> list = new ArrayList<>();
        list.add(new Music("2OfAAhxDFTf3TqlKu4AM",
                "Nơi này có Anh",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://photo-resize-zmp3.zmdcdn.me/w600_r1x1_webp/covers/c/b/cb61528885ea3cdcd9bdb9dfbab067b1_1504988884.jpg",
                0L,
                0L,
                "Sơn Tùng",
                "",
                1200, ""));
        list.add(new Music("2OfAAhxDFTf3TqlKu4AM",
                "Big City Boi",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://data.chiasenhac.com/data/cover/124/123404.jpg",
                0L,
                0L,
                "Binz",
                "",
                2000, ""));
        list.add(new Music("2OfAAhxDFTf3TqlKu4AM",
                "Sài gòn đau lòng quá",
                "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Musics%2FNoi-Nay-Co-Anh-Masew-Bootleg-Son-Tung-M-TP-Masew.mp3?alt=media&token=aba5a5d8-9133-4568-81d1-18c5fbbb5d37",
                "https://data.chiasenhac.com/data/cover/124/123404.jpg",
                0L,
                0L,
                "Hứa Kim Tuyền",
                "",
                2000, ""));
        return list;
    }
}