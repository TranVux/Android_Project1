package com.example.assignment_pro1121_nhom3.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.PlayListMusicAdapter;
import com.example.assignment_pro1121_nhom3.adapters.UserPlayListAdapter;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.dao.UserDAO;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.models.User;
import com.example.assignment_pro1121_nhom3.views.DetailPlaylistActivity;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MyPlaylistFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MyPlaylistFragment.class.getSimpleName();
    RecyclerView rclMyPlaylist;
    ArrayList<Playlist> listPlaylist;
    PlayListMusicAdapter playListMusicAdapter;
    PlaylistDAO playlistDAO;
    UserDAO userDAO;
    ProgressBar progressBar;
    LinearLayout layoutNonLogin;
    LinearLayout notifyEmptyList;
    TextView btnLogin;
    boolean isLogin = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setClick();
        getData();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkLogin();
    }

    private void init(@NonNull View view) {
        rclMyPlaylist = view.findViewById(R.id.rclMyPlaylist);
        progressBar = view.findViewById(R.id.progressBar);
        layoutNonLogin = view.findViewById(R.id.layoutNonLogin);
        btnLogin = view.findViewById(R.id.btnLogin);
        notifyEmptyList = view.findViewById(R.id.notify_empty_list);

        userDAO = new UserDAO();
        listPlaylist = new ArrayList<>();
        playlistDAO = new PlaylistDAO();

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(requireContext());
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);

        // xét layout manager cho recycler view
        rclMyPlaylist.setLayoutManager(flexboxLayoutManager);

        playListMusicAdapter = new PlayListMusicAdapter(listPlaylist, requireContext(), new ItemEvent.PlaylistItemEvent() {
            @Override
            public void onItemClick(Playlist playlist) {
                Intent detailPlaylistActivity = new Intent(requireContext(), DetailPlaylistActivity.class);
                detailPlaylistActivity.putExtra("playlist", playlist);
                startActivity(detailPlaylistActivity);
            }
        });
        rclMyPlaylist.setAdapter(playListMusicAdapter);
    }

    public void setClick() {
        btnLogin.setOnClickListener(this);
    }

    public void getData() {
        if (isLogin) {
            // lấy playlist của người dùng hiện tại
        } else {
            layoutNonLogin.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin: {
                BottomSheetDialogLogin bottomSheetDialogLogin = BottomSheetDialogLogin.newInstance(new BottomSheetDialogLogin.IOnUpdateUiUserFragmentListener() {
                    @Override
                    public void onUpdateUiCallback() {
                        checkLogin();
                    }
                });
                bottomSheetDialogLogin.show(getParentFragmentManager(), "BottomSheetLogin");
                break;
            }
            default:
                break;
        }
    }

    private void checkLogin() {
            updateUI();
    }

    private void updateUI() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            layoutNonLogin.setVisibility(View.GONE);
            checkFirstLogin(currentUser);
        }
    }

    private void checkFirstLogin(FirebaseUser currentUser) {
        userDAO.checkUserAlreadyHaveOnFirebase(currentUser.getUid(), new UserDAO.IsAlreadyLoginOnFirebase() {
            @Override
            public void onAlreadyLoginResult(boolean result) {
                if(!result){
                    addAccountToFirestore();
                    notifyEmptyList.setVisibility(View.VISIBLE);
                }else {
                    loadPlaylist(currentUser);
                }
            }
        });
    }

    private void loadPlaylist(FirebaseUser currentUser) {
        if (currentUser != null) {
            playlistDAO.getAllDataPlaylist(currentUser.getUid(),new PlaylistDAO.ReadAllDataPlaylistListener() {
                @Override
                public void onReadAllDataPlaylistCallback(ArrayList<Playlist> list) {
                    Log.d(TAG,"onReadAllDataPlaylistCallback: " + list.size()+"");
                    if (list.size() == 0) {
                        notifyEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        playListMusicAdapter.setList(list);
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
    }

    private void addAccountToFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String uid = currentUser.getUid();
            long tsLong = System.currentTimeMillis() / 1000;
            User user = new User(uid, name, email, null, tsLong, new ArrayList<>(), null);
            userDAO = new UserDAO();
            userDAO.addUserToFirestore(user);
        }
    }

}