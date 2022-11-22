package com.example.assignment_pro1121_nhom3.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_ID_OF_PLAYLIST;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_MUSIC;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_PLAYLIST_TYPE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_CREATION_DATE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_GENRES_ID;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_ID;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_INDEX;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_NAME;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_SINGER_ID;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_SINGER_NAME;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_THUMBNAIL_URL;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_UPDATE_DATE;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_URL;
import static com.example.assignment_pro1121_nhom3.utils.Constants.KEY_SONG_VIEWS;
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_RECENT_PUBLISH;
import static com.example.assignment_pro1121_nhom3.utils.Constants.PLAYLIST_TYPE_SINGER;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.adapters.MusicRecentPublishAdapter;
import com.example.assignment_pro1121_nhom3.adapters.PlayListMusicAdapter;
import com.example.assignment_pro1121_nhom3.dao.MusicDAO;
import com.example.assignment_pro1121_nhom3.dao.PlaylistDAO;
import com.example.assignment_pro1121_nhom3.interfaces.HandleChangeColorBottomNavigation;
import com.example.assignment_pro1121_nhom3.interfaces.IOnProgressBarStatusListener;
import com.example.assignment_pro1121_nhom3.interfaces.ItemEvent;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.models.Singer;
import com.example.assignment_pro1121_nhom3.services.MusicPlayerService;
import com.example.assignment_pro1121_nhom3.utils.GridSpacingItemDecoration;
import com.example.assignment_pro1121_nhom3.views.ChartActivity;
import com.example.assignment_pro1121_nhom3.views.DetailPlaylistActivity;
import com.example.assignment_pro1121_nhom3.views.DetailSingerActivity;
import com.example.assignment_pro1121_nhom3.views.MainActivity;
import com.example.assignment_pro1121_nhom3.views.SearchActivity;
import com.example.assignment_pro1121_nhom3.views.SingerActivity;
import com.example.assignment_pro1121_nhom3.views.SplashScreen;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class HomeFragment extends Fragment implements View.OnClickListener {
    public static String TAG = HomeFragment.class.getSimpleName();

    // cái này là BlurView: để làm mờ ảnh
    BlurView blurViewSlide;
    ImageView imageForeground;

    //nestedScrollView chứa phần dưới layout tìm kiếm
    NestedScrollView nestedScrollView;
    // slide ảnh
    ImageSlider imageSlider;
    // list ảnh trong slide
    ArrayList<SlideModel> listPictureSlide;

    //RecyclerView của danh sách phát và mới phát hành
    RecyclerView rclPlaylist, rclRecentPublish;
    ArrayList<Music> listRecentPublish = new ArrayList<>();
    ArrayList<Playlist> listPlaylist = new ArrayList<>();
    MusicRecentPublishAdapter musicRecentPublishAdapter;
    PlayListMusicAdapter playListMusicAdapter;

    // nút xem thêm
    TextView labelMorePlaylist, labelMoreRecentPublishMusic;
    ImageView icMorePlaylist, icMoreRecentPublishMusic;

    //search bar
    LinearLayout searchBar;

    //nút chuyển sang các màn hình khác (bxh, nghệ sĩ,...)
    LinearLayout btnBxh, btnArtis, btnPlaylist, btnCategory;

    //xử lý đổi màu bottom navigation
    HandleChangeColorBottomNavigation handleChangeColorBottomNavigation;

    //DAO lấy dữ liệu music
    MusicDAO musicDAO;

    //DAO lấy dữ liệu của playlist
    PlaylistDAO playlistDAO;

    //xử lý progressbar
    LinearLayout progressBarLayout;
    ProgressBar progressBar;

    //player
    MusicPlayer musicPlayer = SplashScreen.musicPlayer;


    public HomeFragment(HandleChangeColorBottomNavigation handleChangeColorBottomNavigation) {
        this.handleChangeColorBottomNavigation = handleChangeColorBottomNavigation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //bỏ hiểu ứng fade từ search activity
        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.no_transition);
        requireActivity().getWindow().setEnterTransition(transition);
        requireActivity().getWindow().setExitTransition(transition);

        //thiết lập cơ bản, ánh xạ view cho fragment
        musicDAO = new MusicDAO();
        playlistDAO = new PlaylistDAO();
        init(view);

        // làm mờ background fragment
        blurBackgroundFragment();

        //hiện thị slide
        FakeDataSlide();
        imageSlider.setImageList(listPictureSlide, ScaleTypes.CENTER_CROP);
        imageSlider.setItemChangeListener(new ItemChangeListener() {
            @Override
            public void onItemChanged(int i) {
                SlideModel slideModel = listPictureSlide.get(i);
                Glide.with(requireContext()).load(slideModel.getImageUrl()).into(imageForeground);
            }
        });

        // xử lý phần dừng và tiếp tục chuyển của slide
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= 250) {
                    imageSlider.stopSliding();
                } else {
                    imageSlider.startSliding(2500);
                }
            }
        });
    }

    private void blurBackgroundFragment() {
        // làm mở ảnh
        float radius = 15f;
        View decorView = requireActivity().getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        blurViewSlide.setupWith(rootView, new RenderScriptBlur(requireContext())) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);
        //
    }

    // khởi tạo trạng thái đầu tiên, ánh xạ view,...
    @SuppressLint("CutPasteId")
    public void init(View view) {
        blurViewSlide = view.findViewById(R.id.blurView);
        imageSlider = view.findViewById(R.id.slider);
        imageForeground = view.findViewById(R.id.imageForeground);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        nestedScrollView.setSmoothScrollingEnabled(true);
        rclPlaylist = view.findViewById(R.id.listPlayList);
        rclRecentPublish = view.findViewById(R.id.listRecentPublish);
        icMorePlaylist = view.findViewById(R.id.icMorePlayList);
        icMoreRecentPublishMusic = view.findViewById(R.id.icMoreRecentPublish);
        labelMorePlaylist = view.findViewById(R.id.labelMorePlayList);
        labelMoreRecentPublishMusic = view.findViewById(R.id.labelMoreRecentPublish);
        btnBxh = view.findViewById(R.id.btnBxh);
        btnArtis = view.findViewById(R.id.btnArtis);
        btnCategory = view.findViewById(R.id.btnCategory);
        btnPlaylist = view.findViewById(R.id.btnPlaylist);
        progressBar = view.findViewById(R.id.progressBar);
        progressBarLayout = view.findViewById(R.id.progressBarLayout);
        searchBar = view.findViewById(R.id.search_bar);

        //bắt sự kiện onClick cho view
        icMorePlaylist.setOnClickListener(this);
        labelMorePlaylist.setOnClickListener(this);
        icMoreRecentPublishMusic.setOnClickListener(this);
        labelMoreRecentPublishMusic.setOnClickListener(this);
        btnPlaylist.setOnClickListener(this);
        btnArtis.setOnClickListener(this);
        btnCategory.setOnClickListener(this);
        btnBxh.setOnClickListener(this);
        searchBar.setOnClickListener(this);

        // hủy bỏ trạng thái trượt của list
        rclRecentPublish.setNestedScrollingEnabled(false);
        rclPlaylist.setNestedScrollingEnabled(false);
        //

//        FlexboxLayoutManager managerRclRecentPublish = new FlexboxLayoutManager(requireContext());
//        managerRclRecentPublish.setFlexDirection(FlexDirection.ROW);
//        managerRclRecentPublish.setJustifyContent(JustifyContent.FLEX_START);
////        managerRclRecentPublish.setFlexWrap(FlexWrap.WRAP);
//
//        FlexboxLayoutManager managerRclPlayList = new FlexboxLayoutManager(requireContext());
//        managerRclPlayList.setFlexDirection(FlexDirection.ROW);
//        managerRclPlayList.setJustifyContent(JustifyContent.FLEX_START);
////        managerRclPlayList.setFlexWrap(FlexWrap.WRAP);

        LinearLayoutManager rclRecentLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager rclPlaylistLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        rclRecentPublish.setLayoutManager(rclRecentLayoutManager);
        rclPlaylist.setLayoutManager(rclPlaylistLayoutManager);

        musicRecentPublishAdapter = new MusicRecentPublishAdapter(listRecentPublish, requireContext(), new ItemEvent.MusicItemEvent() {
            @Override
            public void onItemClick(Music music, int position) {
                navigateToPlayer(position);
            }

            @Override
            public void onSingerNameClick(String singerID) {
                Toast.makeText(requireContext(), singerID, Toast.LENGTH_SHORT).show();
            }
        });

        playListMusicAdapter = new PlayListMusicAdapter(listPlaylist, requireContext(), new ItemEvent.PlaylistItemEvent() {
            @Override
            public void onItemClick(Playlist playlist) {
                Intent detailPlaylistActivity = new Intent(requireContext(), DetailPlaylistActivity.class);
                detailPlaylistActivity.putExtra("playlist", playlist);
                startActivity(detailPlaylistActivity);
            }
        });

        rclRecentPublish.setAdapter(musicRecentPublishAdapter);
        rclPlaylist.setAdapter(playListMusicAdapter);
//        setDataForList();

        musicDAO.getMusicRecentPublish(new MusicDAO.GetMusicRecentPublish() {
            @Override
            public void onGetSuccess(ArrayList<Music> result) {
                listRecentPublish = result;
                musicRecentPublishAdapter.setList(listRecentPublish);
            }

            @Override
            public void onGetFailure() {

            }
        }, new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {

            }

            @Override
            public void afterGetData() {
                if (listPlaylist.size() != 0 && listRecentPublish.size() != 0) {
                    progressBarLayout.setVisibility(View.GONE);
                }
            }
        });

        playlistDAO.getInitDataList(new IOnProgressBarStatusListener() {
            @Override
            public void beforeGetData() {
            }

            @Override
            public void afterGetData() {
                if (listPlaylist.size() != 0 || listRecentPublish.size() != 0) {
                    progressBarLayout.setVisibility(View.GONE);
                }
            }
        }, new PlaylistDAO.GetInitMusicPlayList() {
            @Override
            public void onSuccessGetInitPlayList(ArrayList<Playlist> list) {
                listPlaylist = list;
                playListMusicAdapter.setList(listPlaylist);
            }

            @Override
            public void onFailureGetInitPlayList(Exception e) {
                e.printStackTrace();
            }
        }, 10);

    }

    public void navigateToPlayer(int position) {
        if (musicRecentPublishAdapter.getItemCount() > 0) {
            musicPlayer.pauseSong(musicPlayer.getCurrentPositionSong());
            musicPlayer.clearPlaylist();
            musicPlayer.setPlayList(musicRecentPublishAdapter.getList());
            musicPlayer.setMusicAtPosition(position);
            try {
                musicPlayer.setStateMusicPlayer(MusicPlayer.MUSIC_PLAYER_STATE_PLAYING);
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveCurrentMusic(musicPlayer, "", PLAYLIST_TYPE_RECENT_PUBLISH);
            Log.d(TAG, "onClick: " + musicPlayer.getStateMusicPlayer());
            ((MainActivity) requireActivity()).imageThumbnailCurrentMusic.performClick();
            startServiceMusic(musicPlayer.getCurrentSong(), MusicPlayer.MUSIC_PLAYER_ACTION_RESET_SONG);
        }
    }

    public void FakeDataSlide() {
        listPictureSlide = new ArrayList<>();
        listPictureSlide.add(new SlideModel("https://photo-resize-zmp3.zmdcdn.me/w600_r1x1_webp/covers/c/b/cb61528885ea3cdcd9bdb9dfbab067b1_1504988884.jpg", ScaleTypes.CENTER_CROP));
        listPictureSlide.add(new SlideModel("https://data.chiasenhac.com/data/cover/124/123404.jpg", ScaleTypes.CENTER_CROP));
        listPictureSlide.add(new SlideModel("https://i.scdn.co/image/ab67616d0000b27329f906fe7a60df7777b02ee1", ScaleTypes.CENTER_CROP));
        listPictureSlide.add(new SlideModel("https://data.chiasenhac.com/data/cover/118/117188.jpg", ScaleTypes.CENTER_CROP));
        listPictureSlide.add(new SlideModel("https://i.scdn.co/image/ab67616d0000b2737f0d153d87dc03712137029b", ScaleTypes.CENTER_CROP));
        listPictureSlide.add(new SlideModel("https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_webp/cover/2/3/0/9/230932fdd1bca925c22c487a06359a5a.jpg", ScaleTypes.CENTER_CROP));
    }

    public void startServiceMusic(Music music, int action) {
        Intent serviceMusic = new Intent(requireContext(), MusicPlayerService.class);
        serviceMusic.putExtra("action", action);
        serviceMusic.putExtra(KEY_MUSIC, music);
        requireContext().startService(serviceMusic);
    }

    public void saveCurrentMusic(MusicPlayer musicPlayer, String idPlaylist, String typePlaylist) {
        SharedPreferences sharedPreferencesMusicList = requireContext().getSharedPreferences("music_player", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesMusicList.edit();
        editor.putString(KEY_SONG_NAME, musicPlayer.getCurrentSong().getName());
        editor.putString(KEY_SONG_URL, musicPlayer.getCurrentSong().getUrl());
        editor.putString(KEY_SONG_THUMBNAIL_URL, musicPlayer.getCurrentSong().getThumbnailUrl());
        editor.putString(KEY_SONG_ID, musicPlayer.getCurrentSong().getId());
        editor.putLong(KEY_SONG_VIEWS, musicPlayer.getCurrentSong().getViews());
        editor.putString(KEY_SONG_SINGER_ID, musicPlayer.getCurrentSong().getSingerId());
        editor.putString(KEY_SONG_SINGER_NAME, musicPlayer.getCurrentSong().getSingerName());
        editor.putString(KEY_SONG_GENRES_ID, musicPlayer.getCurrentSong().getGenresId());
        editor.putLong(KEY_SONG_CREATION_DATE, musicPlayer.getCurrentSong().getCreationDate());
        editor.putLong(KEY_SONG_UPDATE_DATE, musicPlayer.getCurrentSong().getUpdateDate());
        editor.putInt(KEY_SONG_INDEX, musicPlayer.getPlayListMusic().indexOf(musicPlayer.getCurrentSong()));
        Log.d(TAG, "saveCurrentMusic: " + idPlaylist);
        editor.putString(KEY_ID_OF_PLAYLIST, idPlaylist);
        editor.putString(KEY_PLAYLIST_TYPE, typePlaylist);
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        imageSlider.stopSliding();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        imageSlider.startSliding(2500);
        handleChangeColorBottomNavigation.toColor();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        // xử lý xử kiện onclick cho các view
        switch (view.getId()) {
            case R.id.labelMorePlayList:
            case R.id.icMorePlayList: {
                Toast.makeText(requireContext(), "Xem thêm danh sách phát", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.labelMoreRecentPublish:
            case R.id.icMoreRecentPublish: {
                Toast.makeText(requireContext(), "Xem thêm bài hát mới thêm", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btnBxh: {
//                Toast.makeText(requireContext(), "Tới Activity bảng xếp hạng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), ChartActivity.class));
                break;
            }
            case R.id.btnArtis: {
//                Toast.makeText(requireContext(), "Tới Activity nghệ sĩ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), SingerActivity.class));
                break;
            }
            case R.id.btnPlaylist: {
                Toast.makeText(requireContext(), "Tới activity playlist", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btnCategory: {
                Toast.makeText(requireContext(), "Tới activity thể loại", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.search_bar: {
                Intent searchIntent = new Intent(requireContext(), SearchActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(), searchBar,
                        Objects.requireNonNull(ViewCompat.getTransitionName(searchBar))
                );
                startActivity(searchIntent, optionsCompat.toBundle());
                break;
            }
            default:
                break;
        }
    }
}