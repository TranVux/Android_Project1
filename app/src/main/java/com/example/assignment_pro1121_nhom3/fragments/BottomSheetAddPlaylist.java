package com.example.assignment_pro1121_nhom3.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.AdapterAddMusicToPlaylist;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.dao.UserDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.time.Instant;
import java.util.ArrayList;

public class BottomSheetAddPlaylist extends BottomSheetDialogFragment {

    public static final String TAG = BottomSheetAddPlaylist.class.getSimpleName();

    PlaylistDAO playlistDAO;
    MusicDAO musicDAO;
    UserDAO userDAO;
    Music currentMusic;
    TextView btnClose, btnCreatePlaylist;
    RecyclerView listPlaylist;
    AdapterAddMusicToPlaylist adapterAddMusicToPlaylist;
    ProgressBar progressBar;
    ArrayList<Playlist> listItemPlaylist;
    LinearLayout notifyEmptyPlaylist;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
        userDAO = new UserDAO();
        musicDAO = new MusicDAO();
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
                if (user != null) {
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
                            Playlist playlistTemp = new Playlist(user.getUid(), edtName.getText().toString(), emptyList, instant.getEpochSecond(), instant.getEpochSecond(), "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Images%2Fgenres%2Ffallback_img.png?alt=media&token=80e07c67-870a-43ae-aa45-86a62d75d13b", user.getDisplayName());

                            if (edtName.getText().toString().isEmpty()) {
                                Toast.makeText(requireContext(), "Không để trống tên playlist!", Toast.LENGTH_SHORT).show();
                            } else {
                                playlistDAO.addPlaylist(new IOnProgressBarStatusListener() {
                                    @Override
                                    public void beforeGetData() {

                                    }

                                    @Override
                                    public void afterGetData() {

                                    }
                                }, playlistTemp, new PlaylistDAO.AddPlaylistListener() {
                                    @Override
                                    public void onAddPlaylistSuccessCallback(DocumentReference documentReference) {
                                        Toast.makeText(requireContext(), "Tạo thành công playlist " + edtName.getText().toString(), Toast.LENGTH_SHORT).show();
                                        playlistTemp.setId(documentReference.getId());
                                        listItemPlaylist.add(playlistTemp);
                                        adapterAddMusicToPlaylist.setList(listItemPlaylist);
                                        notifyEmptyPlaylist.setVisibility(View.GONE);
                                        userDAO.addPlaylistForUser(user.getUid(), documentReference.getId(), new IOnProgressBarStatusListener() {
                                            @Override
                                            public void beforeGetData() {

                                            }

                                            @Override
                                            public void afterGetData() {

                                            }
                                        }, new UserDAO.AddPlaylist() {
                                            @Override
                                            public void onAddPlaylistSuccess() {
                                                Log.d(TAG, "onAddPlaylistSuccess: thêm thành công");
                                            }

                                            @Override
                                            public void onAddPlaylistFailure() {
                                                Log.d(TAG, "onAddPlaylistSuccess: thêm thất bại");
                                            }
                                        });
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
                } else {
                    BottomSheetDialogLogin bottomSheetDialogLogin = BottomSheetDialogLogin
                            .newInstance(new BottomSheetDialogLogin.IOnUpdateUiUserFragmentListener() {
                                @Override
                                public void onUpdateUiCallback() {

                                }
                            });
                    bottomSheetDialogLogin.show(getParentFragmentManager(), "BottomSheetLogin");
                    Toast.makeText(requireContext(), "Đăng nhập để tạo playlist", Toast.LENGTH_SHORT).show();
                }
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
                    playlistDAO.addItemMusicInPlaylist(new IOnProgressBarStatusListener() {
                        @Override
                        public void beforeGetData() {

                        }

                        @Override
                        public void afterGetData() {
                        }
                    }, playlist.getId(), currentMusic.getId(), new PlaylistDAO.AddItemMusicInPlaylistListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onAddItemMusicInPlaylistSuccessCallback() {
                            Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
//                            listItemPlaylist.get(listItemPlaylist.indexOf(playlist)).getMusics().add(currentMusic.getId());

                            if (playlist.getMusics().size() >= 1) {
                                musicDAO.getMusic(new IOnProgressBarStatusListener() {
                                    @Override
                                    public void beforeGetData() {

                                    }

                                    @Override
                                    public void afterGetData() {

                                    }
                                }, playlist.getMusics().get(0), new MusicDAO.ReadItemMusic() {
                                    @Override
                                    public void onReadItemMusicCallback(Music music) {
                                        playlistDAO.setThumbnailPlaylist(playlist.getId(), music.getThumbnailUrl());
                                    }
                                });
                            }
                            playlistDAO.getAllDataPlaylist(user.getUid(), new PlaylistDAO.ReadAllDataPlaylistListener() {
                                @Override
                                public void onReadAllDataPlaylistCallback(ArrayList<Playlist> list) {
                                    adapterAddMusicToPlaylist.setList(list);
                                }
                            }, new IOnProgressBarStatusListener() {
                                @Override
                                public void beforeGetData() {

                                }

                                @Override
                                public void afterGetData() {

                                }
                            });
//                            playlist.setMusics(lisMusicsIdTemp);
//                            adapterAddMusicToPlaylist.setList(listItemPlaylist);
//                            adapterAddMusicToPlaylist.notifyDataSetChanged();
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

        playlistDAO.getAllDataPlaylist(user.getUid(), new PlaylistDAO.ReadAllDataPlaylistListener() {
            @Override
            public void onReadAllDataPlaylistCallback(ArrayList<Playlist> list) {
                listItemPlaylist = list;
                adapterAddMusicToPlaylist.setList(listItemPlaylist);
                progressBar.setVisibility(View.GONE);
                if (list.size() == 0) {
                    notifyEmptyPlaylist.setVisibility(View.VISIBLE);
                } else {
                    notifyEmptyPlaylist.setVisibility(View.GONE);
                }

            }
        }, new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterGetData() {
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
        notifyEmptyPlaylist = view.findViewById(R.id.notify_empty_list);
    }
}
