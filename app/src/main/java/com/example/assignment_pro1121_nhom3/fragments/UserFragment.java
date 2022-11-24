package com.example.assignment_pro1121_nhom3.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.interfaces.HandleChangeColorBottomNavigation;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment implements View.OnClickListener {
    public static String TAG = UserFragment.class.getSimpleName();
    FirebaseAuth mAuth;
    LinearLayout layoutNonLogin, layoutLogin;
    CircleImageView layoutLoginUserAvt;
    TextView layoutLoginUserName;
    ImageView btnSetting;
    // xử lý đổi màu bottom navigation
    HandleChangeColorBottomNavigation handleChangeColorBottomNavigation;
    TextView btnLogin;

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
        init(view);
        setOnClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkLogin();
    }

     public void checkLogin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
//            Toast.makeText(getContext(), "FirebaseUser Đã đăng nhập " + currentUser.getDisplayName()+" - " +currentUser.getUid(), Toast.LENGTH_SHORT).show();
            if(layoutNonLogin.getVisibility() == View.VISIBLE){
                layoutNonLogin.setVisibility(View.GONE);
                layoutLogin.setVisibility(View.VISIBLE);
            }
            if (layoutLogin.getVisibility() == View.VISIBLE) {
                if (getContext() != null) {
                    Glide.with(getContext())
                            .load(currentUser.getPhotoUrl().toString())
                            .centerCrop()
                            .error(R.drawable.default_avt)
                            .into(layoutLoginUserAvt);
                } else {
                    Log.e(TAG, "onStart getContext is null");
                }
                layoutLoginUserName.setText(currentUser.getDisplayName());
            }
        }else if(layoutNonLogin.getVisibility() == View.GONE){
            layoutNonLogin.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
        }
    }

    public void init(View view) {
        btnLogin = view.findViewById(R.id.btnLogin);
        layoutLogin = view.findViewById(R.id.layoutLogin);
        layoutNonLogin = view.findViewById(R.id.layoutNonLogin);
        layoutLoginUserAvt = view.findViewById(R.id.layoutLoginUserAvt);
        layoutLoginUserName = view.findViewById(R.id.layoutLoginUserName);
        btnSetting = view.findViewById(R.id.btnSetting);
    }

    public void setOnClick() {
        btnLogin.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
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
                if(item.getItemId()==R.id.menuSettingLogout){
                    logoutUser();
                }
                return false;
            }
        });
        popup.setGravity(Gravity.END);
        Menu menu = popup.getMenu();
        if(mAuth.getCurrentUser()==null){
            menu.setGroupVisible(R.id.groupAccount,false);
        }
        popup.show();
    }

    private void logoutUser() {
        signOutUserGoogle();
        signOutUserFacebook();
        signOutUserFirebase();
        checkLogin();
    }

    private void signOutUserFirebase() {
        FirebaseAuth.getInstance().signOut();
    }

    private void signOutUserFacebook() {
        LoginManager.getInstance().logOut();
    }

    private void signOutUserGoogle() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(requireContext());
        if(acct != null){
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG,"signOutUserGoogle onComplete");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"signOutUserGoogle onFailure " + e.getMessage());
                        }
                    });
            mGoogleSignInClient.revokeAccess();
        }
    }
}