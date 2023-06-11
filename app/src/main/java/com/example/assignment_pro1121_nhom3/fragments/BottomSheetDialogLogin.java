package com.example.assignment_pro1121_nhom3.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class BottomSheetDialogLogin extends BottomSheetDialogFragment {
    private static final String TAG = BottomSheetDialogLogin.class.getSimpleName();

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    CallbackManager callbackManager;
    TextView btnClose;
    RelativeLayout signInButtonGG, signInButtonFB;
    BottomSheetDialog bottomSheetDialog;
    IOnUpdateUiUserFragmentListener iOnUpdateUiUserFragmentListener;
    ProgressBar mProgressBar;
    SharedPreferences userSharePreferences;
    TextView tv;

    public static BottomSheetDialogLogin newInstance(IOnUpdateUiUserFragmentListener iOnUpdateUiUserFragmentListener) {
        return new BottomSheetDialogLogin(iOnUpdateUiUserFragmentListener);
    }

    private BottomSheetDialogLogin(IOnUpdateUiUserFragmentListener iOnUpdateUiUserFragmentListener) {
        this.iOnUpdateUiUserFragmentListener = iOnUpdateUiUserFragmentListener;
    }

    ActivityResultLauncher<Intent> mIntentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInGGResult(task);
                        Log.d(TAG, "onActivityResult: 1");
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Log.d(TAG, "onActivityResult: 2");
                    }
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

        userSharePreferences = requireContext().getSharedPreferences(Constants.KEY_SHARE_PREFERENCES_USER, Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        if (getActivity() != null) {
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        } else {
            Log.e(TAG, "getActivity is null");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_bottom_sheet_fragment, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setOnShowListener(dialogInterface -> {
            FrameLayout bottomSheet = bottomSheetDialog
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet);

            assert bottomSheet != null;
            BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setSkipCollapsed(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        init(view);
        tv.setText(Html.fromHtml("Bằng cách tiếp tục, bạn đồng ý với <b>Thỏa thuận sử dụng</b> và <b>Chính sách bảo mật</b> của chúng tôi"));
        handleBottomSheet(bottomSheetDialog);
        return bottomSheetDialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void init(View view) {
        btnClose = view.findViewById(R.id.btnClose);
        signInButtonGG = view.findViewById(R.id.layoutLoginGoogle);
        signInButtonFB = view.findViewById(R.id.layoutLoginFacebook);
        mProgressBar = view.findViewById(R.id.progressBar);
        tv = view.findViewById(R.id.text);
    }

    public void handleBottomSheet(BottomSheetDialog bottomSheetDialog) {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        signInButtonGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGG();
            }
        });

        signInButtonFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInFB();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void signInFB() {
        callbackManager = CallbackManager.Factory.create();
//        mProgressBar.setVisibility(View.VISIBLE);
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        LoginManager.getInstance().logInWithReadPermissions(BottomSheetDialogLogin.this, callbackManager,
                Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "signInFB onSuccess");
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "signInFB onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.d(TAG, "signInFB onError" + e.getMessage());
                if (e instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    private void getUserFacebookImageProfile(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    FirebaseUser user = mAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(image))
                            .build();

                    if (user != null) {
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                        }
                                    }
                                });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture.width(200)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
//                                Toast.makeText(requireContext(), user.getDisplayName()+"", Toast.LENGTH_SHORT).show();
                                getUserFacebookImageProfile(token);
                                iOnUpdateUiUserFragmentListener.onUpdateUiCallback();

                                SharedPreferences.Editor editor = userSharePreferences.edit();
                                editor.putString(Constants.KEY_CURRENT_USER_NAME, user.getDisplayName());
                                editor.putString(Constants.KEY_CURRENT_USER_ID, user.getUid());
                                editor.putBoolean(Constants.kEY_IS_LOGIN, true);
                                editor.apply();
                                //addAccountToFirestore();
                            }
                            bottomSheetDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInGG() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mProgressBar.setVisibility(View.VISIBLE);
        mIntentActivityResultLauncher.launch(signInIntent);
    }

    private void handleSignInGGResult(Task<GoogleSignInAccount> completedTask) {
        try {
            Log.d(TAG, "handleSignInGGResult: 3");
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogleAccount(account);
            // Signed in successfully, show authenticated UI.
            // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more
            // information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: ");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Log.d(TAG, "signInWithCredential:success  " + user.getDisplayName());
                                iOnUpdateUiUserFragmentListener.onUpdateUiCallback();

                                // Lưu vào sharePerference
                                SharedPreferences.Editor editor = userSharePreferences.edit();
                                editor.putString(Constants.KEY_CURRENT_USER_NAME, user.getDisplayName());
                                editor.putString(Constants.KEY_CURRENT_USER_ID, user.getUid());
                                editor.putBoolean(Constants.kEY_IS_LOGIN, true);
                                editor.apply();

                            } else {
                                Log.d(TAG, "signInWithCredential:success but cant get account ");
                            }
                            bottomSheetDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public interface IOnUpdateUiUserFragmentListener {
        void onUpdateUiCallback();
    }
}
