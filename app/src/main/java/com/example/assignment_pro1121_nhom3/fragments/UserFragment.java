package com.example.assignment_pro1121_nhom3.fragments;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.UserPlayListAdapter;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.dao.UserDAO;
import com.example.assignment_pro1121_nhom3.interfaces.HandleChangeColorBottomNavigation;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.models.User;
import com.example.assignment_pro1121_nhom3.views.MusicInDeviceActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment implements View.OnClickListener {
    public static String TAG = UserFragment.class.getSimpleName();
    FirebaseAuth mAuth;
    LinearLayout layoutNonLogin, layoutLogin;
    CircleImageView layoutLoginUserAvt;
    TextView layoutLoginUserName;
    TextView textNotifyNonLogin;
    LinearLayout notifyEmptyList, recentButton, deviceButton;
    ImageView btnSetting;
    // xử lý đổi màu bottom navigation
    HandleChangeColorBottomNavigation handleChangeColorBottomNavigation;
    TextView btnLogin, amountLocalSong;
    UserDAO userDAO;
    PlaylistDAO playlistDAO;
    FirebaseUser currentUser;
    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;

    public UserFragment(HandleChangeColorBottomNavigation handleChangeColorBottomNavigation) {
        this.handleChangeColorBottomNavigation = handleChangeColorBottomNavigation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
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
        handleChangeColorBottomNavigation.toColor();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userDAO = new UserDAO();
        playlistDAO = new PlaylistDAO();
        init(view);

        if (!checkPermission()) {
            amountLocalSong.setText("");
        } else {
            getSongList();
        }

        setOnClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkLogin();
    }


    public void checkLogin() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (layoutNonLogin.getVisibility() == View.VISIBLE) {
                layoutNonLogin.setVisibility(View.GONE);
                layoutLogin.setVisibility(View.VISIBLE);
                textNotifyNonLogin.setVisibility(View.GONE);
            }
            Log.d(TAG,currentUser.getPhotoUrl()+"");
            if (layoutLogin.getVisibility() == View.VISIBLE) {
                if (getContext() != null) {
                    Log.d(TAG, "checkLogin: " + currentUser.getPhotoUrl());
                    Glide.with(getContext())
                            .load(currentUser.getPhotoUrl())
                            .centerCrop()
                            .error(R.drawable.default_avt)
                            .into(layoutLoginUserAvt);
                } else {
                    Log.e(TAG, "onStart getContext is null");
                }
                layoutLoginUserName.setText(currentUser.getDisplayName());
                userDAO.checkUserAlreadyHaveOnFirebase(currentUser.getUid(), new UserDAO.IsAlreadyLoginOnFirebase() {
                    @Override
                    public void onAlreadyLoginResult(boolean result) {
                        if(!result){
                            addAccountToFirestore();
                            notifyEmptyList.setVisibility(View.VISIBLE);
                        }else {
                            checkPlaylist();
                        }
                    }
                });
            }
        } else if (layoutNonLogin.getVisibility() == View.GONE) {
            layoutNonLogin.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
            textNotifyNonLogin.setVisibility(View.VISIBLE);
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

    public void checkPlaylist() {
        if (currentUser != null) {
            playlistDAO.getAllDataPlaylist(currentUser.getUid(), new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void afterGetData() {
                    mProgressBar.setVisibility(View.GONE);
                }
            }, new PlaylistDAO.ReadAllDataPlaylistListener() {
                @Override
                public void onReadAllDataPlaylistCallback(ArrayList<Playlist> list) {
                    Log.d(TAG,"onReadAllDataPlaylistCallback: " + list.size()+"");
                    if (list.size() == 0) {
                        notifyEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        notifyEmptyList.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        UserPlayListAdapter userPlayListAdapter = new UserPlayListAdapter(list);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        mRecyclerView.setLayoutManager(linearLayoutManager);
                        mRecyclerView.setAdapter(userPlayListAdapter);
                    }
                }
            });
        }
    }

    public void init(View view) {
        btnLogin = view.findViewById(R.id.btnLogin);
        layoutLogin = view.findViewById(R.id.layoutLogin);
        layoutNonLogin = view.findViewById(R.id.layoutNonLogin);
        layoutLoginUserAvt = view.findViewById(R.id.layoutLoginUserAvt);
        layoutLoginUserName = view.findViewById(R.id.layoutLoginUserName);
        btnSetting = view.findViewById(R.id.btnSetting);
        textNotifyNonLogin = view.findViewById(R.id.textNotifyNonLogin);
        notifyEmptyList = view.findViewById(R.id.notify_empty_list);
        recentButton = view.findViewById(R.id.recentButton);
        deviceButton = view.findViewById(R.id.deviceButton);
        amountLocalSong = view.findViewById(R.id.amountLocalSong);
        mRecyclerView = view.findViewById(R.id.playlistMusic);
        mProgressBar = view.findViewById(R.id.progressBar);
    }

    public void setOnClick() {
        btnLogin.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        recentButton.setOnClickListener(this);
        deviceButton.setOnClickListener(this);
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
            case R.id.btnSetting:
                showPopupMenuSetting(btnSetting);
                break;
            case R.id.deviceButton: {
                startActivity(new Intent(requireContext(), MusicInDeviceActivity.class));
                break;
            }
            case R.id.recentButton: {
                break;
            }
            default:
                break;
        }
    }

    private void showPopupMenuSetting(ImageView btnSetting) {
        PopupMenu popup = new PopupMenu(requireContext(), btnSetting);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_setting_fragment_user, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menuSettingLogout) {
                    logoutUser();
                }
                return false;
            }
        });
        popup.setGravity(Gravity.END);
        Menu menu = popup.getMenu();
        if (mAuth.getCurrentUser() == null) {
            menu.setGroupVisible(R.id.groupAccount, false);
        }
        popup.show();
    }

    private void logoutUser() {
        signOutUserGoogle();
        signOutUserFacebook();
        signOutUserFirebase();
        checkLogin();
        updateUiLogout();
    }

    private void updateUiLogout() {
        mRecyclerView.setVisibility(View.GONE);
        notifyEmptyList.setVisibility(View.GONE);
    }

    private void signOutUserFirebase() {
        FirebaseAuth.getInstance().signOut();
    }

    private void signOutUserFacebook() {
        LoginManager.getInstance().logOut();
    }

    private void signOutUserGoogle() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (acct != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "signOutUserGoogle onComplete");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "signOutUserGoogle onFailure " + e.getMessage());
                        }
                    });
            mGoogleSignInClient.revokeAccess();
        }
    }

    public void getSongList() {
        // Query external audio resources
        ArrayList<Music> list = new ArrayList<>();
        ContentResolver musicResolver = requireContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        @SuppressLint("Recycle") Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        // Iterate over results if valid
        if (musicCursor != null && musicCursor.moveToFirst()) {
            // Get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumArtColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int uriSongColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                Long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thumbnailArt = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), String.valueOf(musicCursor.getLong(albumArtColumn))).toString();
                String uriSong = musicCursor.getString(uriSongColumn);
                list.add(new Music(String.valueOf(thisId), thisTitle, uriSong, thumbnailArt, System.currentTimeMillis(), System.currentTimeMillis(), thisArtist, thisArtist, 1000, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        Log.d(TAG, "getSongList: " + list);
        amountLocalSong.setText(list.size() + " bài hát");
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageEmulated();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED;
        }
    }
}