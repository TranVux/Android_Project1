package com.example.assignment_pro1121_nhom3.fragments;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.UserPlayListAdapter;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.dao.UserDAO;
import com.example.assignment_pro1121_nhom3.interfaces.HandleChangeColorBottomNavigation;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.models.User;
import com.example.assignment_pro1121_nhom3.views.DetailPlaylistActivity;
import com.example.assignment_pro1121_nhom3.views.MusicInDeviceActivity;
import com.example.assignment_pro1121_nhom3.views.RecentMusicActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.io.File;
import java.io.FileFilter;
import java.time.Instant;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment implements View.OnClickListener {
    public static String TAG = UserFragment.class.getSimpleName();
    FirebaseAuth mAuth;
    LinearLayout layoutNonLogin, layoutLogin;
    CircleImageView layoutLoginUserAvt;
    LinearLayout notifyEmptyList, recentButton, deviceButton;
    ImageView btnSetting, btnAddPlaylist;
    // xử lý đổi màu bottom navigation
    HandleChangeColorBottomNavigation handleChangeColorBottomNavigation;
    TextView btnLogin, amountLocalSong, layoutLoginUserName, textNotifyNonLogin, amountRecentSong;
    UserDAO userDAO;
    PlaylistDAO playlistDAO;
    FirebaseUser currentUser;
    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    UserPlayListAdapter userPlayListAdapter;
    ArrayList<Playlist> tempPlaylist;
    private static final int REQUEST_CODE_PERMISSION = 30;
    private boolean isGrantedPermission = false;

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
        if (checkPermission()) {
            getSongList();
        } else {
            amountLocalSong.setText("");
        }
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
        getPermission();

        if (!checkPermission()) {
            amountLocalSong.setText("");
        } else {
            getSongList();
        }

        setOnClick();
        ArrayList<Music> listRecentSong = (ArrayList<Music>) SongRecentDatabase.getInstance(requireContext())
                .musicRecentDAO().getListSongRecent();
        if (listRecentSong == null) {
            listRecentSong = new ArrayList<>();
        }
        amountRecentSong.setText(listRecentSong.size() + " bài hát");
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
            Log.d(TAG, currentUser.getPhotoUrl() + "");
            if (layoutLogin.getVisibility() == View.VISIBLE) {
                if (getContext() != null) {
                    Log.d(TAG, "checkLogin: " + currentUser.getPhotoUrl());
                    Glide.with(getContext())
                            .load(currentUser.getPhotoUrl())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
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
                        if (!result) {
                            addAccountToFirestore();
                            notifyEmptyList.setVisibility(View.VISIBLE);
                        } else {
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
            playlistDAO.getAllDataPlaylist(currentUser.getUid(), new PlaylistDAO.ReadAllDataPlaylistListener() {
                @Override
                public void onReadAllDataPlaylistCallback(ArrayList<Playlist> list) {
                    Log.d(TAG, "onReadAllDataPlaylistCallback: " + list.size() + "");
                    if (list.size() == 0) {
                        notifyEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        notifyEmptyList.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        tempPlaylist = list;
                        userPlayListAdapter.setPlaylists(tempPlaylist);
                    }
                }
            }, new IOnProgressBarStatusListener() {
                @Override
                public void beforeGetData() {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void afterGetData() {
                    mProgressBar.setVisibility(View.GONE);
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
        amountRecentSong = view.findViewById(R.id.amountRecentSong);
        mRecyclerView = view.findViewById(R.id.playlistMusic);
        mProgressBar = view.findViewById(R.id.progressBar);
        btnAddPlaylist = view.findViewById(R.id.btnAddPlaylist);

        tempPlaylist = new ArrayList<>();
        userPlayListAdapter = new UserPlayListAdapter(tempPlaylist, new UserPlayListAdapter.PlaylistEvent() {
            @Override
            public void onItemClick(Playlist playlist) {
                Intent detailPlaylistActivity = new Intent(requireContext(), DetailPlaylistActivity.class);
                detailPlaylistActivity.putExtra("playlist", playlist);
                startActivity(detailPlaylistActivity);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(userPlayListAdapter);
    }

    public void setOnClick() {
        btnLogin.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        recentButton.setOnClickListener(this);
        deviceButton.setOnClickListener(this);
        btnAddPlaylist.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin: {
                BottomSheetDialogLogin bottomSheetDialogLogin = BottomSheetDialogLogin
                        .newInstance(new BottomSheetDialogLogin.IOnUpdateUiUserFragmentListener() {
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
                startActivity(new Intent(requireContext(), RecentMusicActivity.class));
                break;
            }
            case R.id.btnAddPlaylist: {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    addPLayList();
                } else {
                    BottomSheetDialogLogin bottomSheetDialogLogin = BottomSheetDialogLogin
                            .newInstance(new BottomSheetDialogLogin.IOnUpdateUiUserFragmentListener() {
                                @Override
                                public void onUpdateUiCallback() {
                                    checkLogin();
                                }
                            });
                    bottomSheetDialogLogin.show(getParentFragmentManager(), "BottomSheetLogin");
                    Toast.makeText(requireContext(), "Vui lòng đăng nhập để thêm playlist!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
    }

    public void addPLayList() {
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
                // tên của người tạo là lấy tên của tài khoản hiện tại
                // nếu chưa đăng nhập thì ko đc tạo playlist
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                Playlist playlistTemp = new Playlist(null, edtName.getText().toString(), emptyList,
                        instant.getEpochSecond(), instant.getEpochSecond(),
                        "https://firebasestorage.googleapis.com/v0/b/project1-group3-52e2e.appspot.com/o/Images%2Fgenres%2Ffallback_img.png?alt=media&token=80e07c67-870a-43ae-aa45-86a62d75d13b",
                        user.getDisplayName());

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
                            Toast.makeText(requireContext(), "Tạo thành công playlist " + edtName.getText().toString(),
                                    Toast.LENGTH_SHORT).show();
                            playlistTemp.setId(documentReference.getId());
                            tempPlaylist.add(playlistTemp);
                            userPlayListAdapter.setPlaylists(tempPlaylist);
                            if (notifyEmptyList.getVisibility() == View.VISIBLE) {
                                notifyEmptyList.setVisibility(View.GONE);
                            }
                            dialog.dismiss();
                            userDAO.addPlaylistForUser(currentUser.getUid(), playlistTemp.getId(),
                                    new IOnProgressBarStatusListener() {
                                        @Override
                                        public void beforeGetData() {

                                        }

                                        @Override
                                        public void afterGetData() {

                                        }
                                    }, new UserDAO.AddPlaylist() {
                                        @Override
                                        public void onAddPlaylistSuccess() {
                                            Log.d(TAG, "onAddPlaylistSuccess: tạo thành công");
                                        }

                                        @Override
                                        public void onAddPlaylistFailure() {
                                            Log.d(TAG, "onAddPlaylistFailure: lỗi thêm playlist");
                                        }
                                    });
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
        @SuppressLint("Recycle")
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
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
                String thumbnailArt = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"),
                        String.valueOf(musicCursor.getLong(albumArtColumn))).toString();
                String uriSong = musicCursor.getString(uriSongColumn);
                list.add(new Music(String.valueOf(thisId), thisTitle, uriSong, thumbnailArt, System.currentTimeMillis(),
                        System.currentTimeMillis(), thisArtist, thisArtist, 1000, thisArtist));
            } while (musicCursor.moveToNext());
        }
        Log.d(TAG, "getSongList: " + list);
        amountLocalSong.setText(list.size() + " bài hát");
    }


    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageEmulated();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    private ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageEmulated()) {
                                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                                getSongList();
                            } else {
                                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                                amountLocalSong.setText("");
                            }
                        }
                    }
                }
            });

    public void getSongListAPI30() {
        File directory = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("Music")));
        File[] mp3File = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".mp3");
            }
        });
        if (mp3File != null) {
            for (File file : mp3File) {
                Log.d(TAG, "getSongListAPI30: " + file.getAbsolutePath() + " " + file.getName());
            }
        }
    }

    public void getPermission() {
        isGrantedPermission = checkPermission();
        Log.d(TAG, "getPermission: " + isGrantedPermission);
        if (isGrantedPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getSongListAPI30();
            } else {
                getSongList();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", requireContext().getApplicationContext().getPackageName())));
                    intentActivityResultLauncher.launch(intent);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intentActivityResultLauncher.launch(intent);
                }
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongList();
            } else {
                Toast.makeText(requireContext(), "Chưa được cấp quyền", Toast.LENGTH_SHORT).show();
                amountLocalSong.setText("");
            }
        }
    }
}