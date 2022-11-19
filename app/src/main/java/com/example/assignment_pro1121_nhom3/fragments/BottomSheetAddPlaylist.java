package com.example.assignment_pro1121_nhom3.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.AdapterAddMusicToPlaylist;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;

import java.time.Instant;
import java.util.ArrayList;

public class BottomSheetAddPlaylist extends BottomSheetDialogFragment {

    public static final String TAG = BottomSheetAddPlaylist.class.getSimpleName();

    PlaylistDAO playlistDAO;
    Music currentMusic;
    TextView btnClose, btnCreatePlaylist;
    RecyclerView listPlaylist;
    AdapterAddMusicToPlaylist adapterAddMusicToPlaylist;
    ProgressBar progressBar;
    ArrayList<Playlist> listItemPlaylist;

    public static BottomSheetAddPlaylist newInstace(Music music) {
        BottomSheetAddPlaylist bottomSheetAddPlaylist = new BottomSheetAddPlaylist();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_MUSIC, music);
        bottomSheetAddPlaylist.setArguments(bundle);
        return bottomSheetAddPlaylist;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        playlistDAO = new PlaylistDAO();
        if (bundle != null) {
            currentMusic = (Music) bundle.getSerializable(Constants.KEY_MUSIC);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottomsheet_add_playlist, null);
        bottomSheetDialog.setContentView(view);
        init(view);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        eventClick();
        handleBottomSheetPlaylist();
        return bottomSheetDialog;
    }

    public void eventClick() {
        btnCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
                View layoutAddPlaylist = LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_playlist, null);
                dialogBuilder.setView(layoutAddPlaylist);


                // ánh xạ view
                TextView btnSave, btnClose;
                EditText edtName = layoutAddPlaylist.findViewById(R.id.edtName);
                btnClose = layoutAddPlaylist.findViewById(R.id.btnClose);
                btnSave = layoutAddPlaylist.findViewById(R.id.btnSave);
                //

                // set builder cho dialog
                Dialog dialog = dialogBuilder.create();

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> emptyList = new ArrayList<>();
                        Instant instant = Instant.now();
                        //tên của người tạo là lấy tên của tài khoản hiện tại
                        // nếu chưa đăng nhập thì ko đc tạo playlist
                        Playlist playlistTemp = new Playlist(null, edtName.getText().toString(), emptyList, instant.getEpochSecond(), instant.getEpochSecond(), "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Images%2Fgenres%2Ffallback_img.png?alt=media&token=80e07c67-870a-43ae-aa45-86a62d75d13b", "Vũ");

                        if (edtName.getText().toString().isEmpty()) {
                            Toast.makeText(requireContext(), "Không để trống tên playlist!", Toast.LENGTH_SHORT).show();
                        } else {
                            playlistDAO.addPlaylist(playlistTemp, new PlaylistDAO.AddPlaylistListener() {
                                @Override
                                public void onAddPlaylistSuccessCallback(DocumentReference documentReference) {
                                    Toast.makeText(requireContext(), "Tạo thành công playlist " + edtName.getText().toString(), Toast.LENGTH_SHORT).show();
                                    playlistTemp.setId(documentReference.getId());
                                    listItemPlaylist.add(playlistTemp);
                                    adapterAddMusicToPlaylist.setList(listItemPlaylist);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onAddPlaylistFailureCallback(Exception e) {
                                    Log.d(TAG, "onAddPlaylistFailureCallback: có lõi nè ba");
                                }
                            });
                        }
                    }
                });

                // show dialog
                dialog.show();
            }
        });
    }

    public void handleBottomSheetPlaylist() {
        listItemPlaylist = new ArrayList<>();
        adapterAddMusicToPlaylist = new AdapterAddMusicToPlaylist(listItemPlaylist, requireContext(), new AdapterAddMusicToPlaylist.ItemPlaylistEvent() {
            @Override
            public void onItemClick(Playlist playlist) {

                if (playlist.getMusics().contains(currentMusic.getId())) {
                    Log.d(TAG, "onItemClick: có rồi");
                    Toast.makeText(requireContext(), "Bài hát đã có sẵn trong playlist", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onItemClick: chưa có");
                    playlistDAO.addItemMusicInPlaylist(playlist.getId(), currentMusic.getId(), new PlaylistDAO.AddItemMusicInPlaylistListener() {
                        @Override
                        public void onAddItemMusicInPlaylistSuccessCallback() {
                            Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                            ArrayList<String> lisMusicsIdTemp = playlist.getMusics();
                            lisMusicsIdTemp.add(currentMusic.getId());
                            playlist.setMusics(lisMusicsIdTemp);
                            adapterAddMusicToPlaylist.setList(listItemPlaylist);
                        }

                        @Override
                        public void onAddItemMusicInPlaylistFailureCallback(Exception e) {
                            Log.d(TAG, "onAddItemMusicInPlaylistFailureCallback: lỗi rồi");
                        }
                    });
                }

            }
        });

        listPlaylist.setLayoutManager(new LinearLayoutManager(requireContext()));
        listPlaylist.setAdapter(adapterAddMusicToPlaylist);

        playlistDAO.getAllDataPlaylist(new PlaylistDAO.ReadAllDataPlaylistListener() {
            @Override
            public void onReadAllDataPlaylistCallback(ArrayList<Playlist> list) {
                listItemPlaylist = list;
                adapterAddMusicToPlaylist.setList(listItemPlaylist);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void init(View view) {
        listPlaylist = view.findViewById(R.id.listPlayList);
        btnClose = view.findViewById(R.id.btnClose);
        btnCreatePlaylist = view.findViewById(R.id.btnCreatePlaylist);
        listPlaylist = view.findViewById(R.id.listPlaylist);
        progressBar = view.findViewById(R.id.progressBar);
    }
}
