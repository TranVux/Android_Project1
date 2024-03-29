package com.example.assignment_pro1121_nhom3.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.storages.MusicPlayerStorage;
import com.example.assignment_pro1121_nhom3.storages.SongRecentDatabase;
import com.example.assignment_pro1121_nhom3.storages.StateMusicPlayerStorage;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.views.MainActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaExtractor;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.*;
import static com.example.assignment_pro1121_nhom3.utils.Constants.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener {

    public static final String TAG = MusicPlayerService.class.getSimpleName();

    private String currentMediaID = "";
    private boolean isLoadPlaylistSuccessfully = false;

    //    private Music currentSong;
    private boolean isPlaying = false;
    MediaSessionCompat mediaSessionCompat;
    MediaControllerCompat mediaControllerCompat;
    private String playMode;
    private ExoPlayer exoPlayer;
    ArrayList<Music> playlistSong = new ArrayList<>();

    //Tracker Progress
    private Handler handler;
    private Runnable runnable;
    //intent update position music
    Intent updatePositionCurrentMusic;

    //caching handle
    Cache cache;
    HttpDataSource.Factory httpDataSourceFactory;
    DatabaseProvider databaseProvider;
    DataSource.Factory dataSourceFactory;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playMode = MusicPlayerStorage.getInstance(this).getString(KEY_MODE, MUSIC_PLAYER_MODE_ONLINE);
        installMediaPlayer();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int action = intent.getIntExtra("action", -1);
            playMode = intent.getStringExtra(KEY_MODE_MUSIC_PLAYER);
            handleActionMusicPlayer(intent, action);
        }
        return START_STICKY;
    }

    public void handleActionMusicPlayer(Intent intent, int action) {
        switch (action) {
            case MUSIC_PLAYER_ACTION_START: {
                if (!isPlaying) {
                    initService(intent);
                }
                break;
            }
            case MUSIC_PLAYER_ACTION_RESET_PLAYLIST: {
                isLoadPlaylistSuccessfully = false;
                resetSong(intent);
                break;
            }
            case MUSIC_PLAYER_ACTION_NEXT: {
                nextSong();
                break;
            }
            case MUSIC_PLAYER_ACTION_PREVIOUS: {
                previousSong();
                break;
            }
            case MUSIC_PLAYER_ACTION_PAUSE: {
                pauseSong();
                break;
            }
            case MUSIC_PLAYER_ACTION_RESUME: {
                resumeSong(intent);
                Log.d(TAG, "handleActionMusicPlayer: " + exoPlayer.getPlaybackState());
                break;
            }
            case MUSIC_PLAYER_ACTION_DESTROY: {
                destroyPlayer();
                break;
            }
            case MUSIC_PLAYER_ACTION_GO_TO_SONG: {
                int index = intent.getIntExtra(KEY_SONG_INDEX, 0);
                goToSong(index);
                break;
            }
            case MUSIC_PLAYER_ACTION_SEEK_TO_POSITION: {
                int musicDurationPosition = intent.getIntExtra(KEY_SEEK_TO_POSITION, 0);
                seekToPosition(musicDurationPosition);
                break;
            }
            default:
                Log.d(TAG, "handleActionMusicPlayer: Invalid action for music player");
                break;
        }
        handleStatePlayer();
    }


    private void resetSong(Intent intent) {
        exoPlayer.release();
        handler.removeCallbacks(runnable);
        installMediaPlayer();
        initService(intent);
    }

    public void seekToPosition(int position) {
        exoPlayer.seekTo(position * 1000L);
        exoPlayer.play();
        sendIntentToActivity(MUSIC_PLAYER_ACTION_RESUME, exoPlayer.getCurrentMediaItemIndex());
    }

    private void destroyPlayer() {
        onDestroy();
        sendIntentToActivity(MUSIC_PLAYER_ACTION_DESTROY, exoPlayer.getCurrentMediaItemIndex());
    }

    private void goToSong(int index) {
        if (exoPlayer.isPlaying()) {
            exoPlayer.pause();
        }
        currentMediaID = getMediaIdAtPosition(index);
        exoPlayer.seekTo(index, 0);
        exoPlayer.play();
        sendIntentToActivity(MUSIC_PLAYER_ACTION_GO_TO_SONG, index);
    }

    private void resumeSong(Intent intent) {
        if (!exoPlayer.isPlaying()) {
            if (exoPlayer.getCurrentMediaItem() != null) {
                exoPlayer.play();
                currentMediaID = getCurrentMediaId();
            } else {
                initService(intent);
            }
            isPlaying = true;
            sendNotification();
            sendIntentToActivity(MUSIC_PLAYER_ACTION_RESUME, exoPlayer.getCurrentMediaItemIndex());
        }
    }

    private void pauseSong() {
        if (exoPlayer.isPlaying()) {
            exoPlayer.pause();
            currentMediaID = getCurrentMediaId();
            isPlaying = false;
            sendNotification();
            sendIntentToActivity(MUSIC_PLAYER_ACTION_PAUSE, exoPlayer.getCurrentMediaItemIndex());
        }
    }

    private void previousSong() {
        if (exoPlayer.hasPreviousMediaItem()) {
//            currentMediaID = getPrevMediaId();
            currentMediaID = playlistSong.get(exoPlayer.getPreviousMediaItemIndex()).getId();
            exoPlayer.seekToPreviousMediaItem();
            exoPlayer.play();
//            sendIntentToActivity(MUSIC_PLAYER_ACTION_UPDATE_PLAYER, exoPlayer.getCurrentMediaItemIndex());
            isPlaying = true;
        }
    }

    private void nextSong() {
        if (exoPlayer.hasNextMediaItem()) {
//            currentMediaID = getNextMediaId();
            currentMediaID = playlistSong.get(exoPlayer.getNextMediaItemIndex()).getId();
            exoPlayer.seekToNextMediaItem();
            exoPlayer.play();
//            sendIntentToActivity(MUSIC_PLAYER_ACTION_UPDATE_PLAYER, exoPlayer.getCurrentMediaItemIndex());
            isPlaying = true;
        }
    }

    public void sendIntentToActivity(int action, int index) {
        Intent outIntent = new Intent(MUSIC_PLAYER_EVENT);
        outIntent.putExtra("action", action);
        outIntent.putExtra(KEY_SONG_INDEX, index);
        LocalBroadcastManager.getInstance(this).sendBroadcast(outIntent);
    }

    private void initService(Intent intent) {
        playlistSong = (ArrayList<Music>) intent.getSerializableExtra(KEY_PLAYLIST);
        int index = intent.getIntExtra(KEY_SONG_INDEX, 0);
        if (playlistSong != null) {
            setUpListMediaSource(getListMediaSource(playlistSong));
            currentMediaID = getCurrentMediaId();
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.prepare();
            exoPlayer.seekTo(index, 0);
            Log.d(TAG, "initService: " + currentMediaID);
            exoPlayer.play();
//            Log.d(TAG, "initService: " + currentSong.getName());
            sendIntentToActivity(MUSIC_PLAYER_ACTION_RESUME, exoPlayer.getCurrentMediaItemIndex());
            isPlaying = true;
            isLoadPlaylistSuccessfully = true;
            changeStateResume(true);
            handleStatePlayer();
        }
    }

    public void setUpTrackerProgress() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
//                Log.d(TAG, "run: " + exoPlayer.getCurrentPosition() / 1000);
                updatePositionCurrentMusic = new Intent(MUSIC_PLAYER_EVENT);
                updatePositionCurrentMusic.putExtra(KEY_CURRENT_MUSIC_POSITION, (int) exoPlayer.getCurrentPosition() / 1000);
                updatePositionCurrentMusic.putExtra(KEY_MUSIC_DURATION, (int) exoPlayer.getDuration() / 1000);
                LocalBroadcastManager.getInstance(MusicPlayerService.this).sendBroadcast(updatePositionCurrentMusic);
                handler.postDelayed(runnable, 1000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public void installMediaPlayer() {
        if (databaseProvider == null) {
            databaseProvider = new StandaloneDatabaseProvider(this);
        }

        if (httpDataSourceFactory == null) {
            httpDataSourceFactory = new DefaultHttpDataSource.Factory();
        }
        if (cache == null) {
            cache = new SimpleCache(new File(MusicPlayerService.this.getCacheDir(), "cacheaudios"), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 90), databaseProvider);
        }
        //A DataSource that reads and writes a Cache.
        if (Objects.equals(playMode, MUSIC_PLAYER_MODE_ONLINE)) {
            dataSourceFactory = new CacheDataSource.Factory().setCache(cache).setUpstreamDataSourceFactory(httpDataSourceFactory);
        } else if (Objects.equals(playMode, MUSIC_PLAYER_MODE_LOCAL)) {
            dataSourceFactory = new FileDataSource.Factory();
        }

        //new vs player
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        exoPlayer = new ExoPlayer.Builder(this)
                .setLooper(Looper.getMainLooper())
                .build();
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        exoPlayerListener();
        mediaSessionCompat = new MediaSessionCompat(this, MusicPlayerService.class.getName());
    }

    public String getCurrentMediaId() {
        if (exoPlayer != null && exoPlayer.getCurrentMediaItem() != null) {
            return exoPlayer.getCurrentMediaItem().mediaId;
        }
        return "";
    }

    public String getMediaIdAtPosition(int position) {
        if (exoPlayer.getMediaItemCount() != 0) {
            return playlistSong.get(position).getId();
        }
        return "";
    }

    private void exoPlayerListener() {
        exoPlayer.addListener(new Player.Listener() {

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                Log.d(TAG, "onMediaItemTransition: currentSong" + getCurrentSong().getName());
                isPlaying = true;
                sendNotification();
                sendIntentToActivity(MUSIC_PLAYER_ACTION_UPDATE_PLAYER, exoPlayer.getCurrentMediaItemIndex());
                saveCurrentMusic(getCurrentSong());
                setUpTrackerProgress();
            }

        });
    }


    public Music getCurrentSong() {
        if (exoPlayer.getMediaItemCount() > 0) {
            return playlistSong.get(exoPlayer.getCurrentMediaItemIndex());
        }
        return null;
    }

    public void sendNotification() {
        Intent homeIntent = new Intent(this, MainActivity.class);
        PendingIntent homePendingIntent = PendingIntent.getActivity(this, 0, homeIntent, PendingIntent.FLAG_IMMUTABLE);
        final Bitmap[] musicThumbnail = new Bitmap[1];
        mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, CapitalizeWord.CapitalizeWords(getCurrentSong().getName()))
                .putString(MediaMetadata.METADATA_KEY_ARTIST, CapitalizeWord.CapitalizeWords(getCurrentSong().getSingerName()))
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, getCurrentSong().getThumbnailUrl())
                .putLong(MediaMetadata.METADATA_KEY_DURATION, exoPlayer.getDuration() / 1000)
                .build()
        );

        mediaControllerCompat = new MediaControllerCompat(getApplicationContext(), mediaSessionCompat.getSessionToken());

        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this, "AUDIO_SERVICE")
                .setSmallIcon(R.drawable.ic_notiffication_new)
                .setContentText(CapitalizeWord.CapitalizeWords(getCurrentSong().getSingerName()))
                .setContentTitle(CapitalizeWord.CapitalizeWords(getCurrentSong().getName()))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .setContentIntent(homePendingIntent)
                .setSound(null);

        if (isPlaying) {
            notificationCompatBuilder
                    .addAction(R.drawable.ic_small_previous, "Previous", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_PREVIOUS))
                    .addAction(R.drawable.ic_small_pause, "Pause", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_PAUSE))
                    .addAction(R.drawable.ic_small_next, "Next", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_NEXT))
                    .addAction(R.drawable.ic_close, "close", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_DESTROY));
        } else {
            notificationCompatBuilder
                    .addAction(R.drawable.ic_small_previous, "Previous", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_PREVIOUS))
                    .addAction(R.drawable.ic_small_play, "Resume", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_RESUME))
                    .addAction(R.drawable.ic_small_next, "Next", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_NEXT))
                    .addAction(R.drawable.ic_close, "close", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_DESTROY));
        }

        Glide.with(MusicPlayerService.this)
                .asBitmap()
                .load(getCurrentSong().getThumbnailUrl())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        notificationCompatBuilder.setLargeIcon(resource);
                        musicThumbnail[0] = resource;
                        Notification notification = notificationCompatBuilder.build();
                        startForeground(1, notification);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        notificationCompatBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.fallback_img));
                        Notification notification = notificationCompatBuilder.build();
                        startForeground(1, notification);
                    }
                });
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public PendingIntent getPendingIntentAction(Context context, int action) {
        Intent intent = new Intent(MusicPlayerService.this, EventMusicPlayerService.class);
        intent.putExtra("action", action);
        return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Đây là trình phát nhạc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            android.app.NotificationChannel channel = new android.app.NotificationChannel("AUDIO_SERVICE", "AUDIO_SERVICE", importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        runnable = null;
        if (exoPlayer != null) {
            if (exoPlayer.isPlaying()) {
                exoPlayer.stop();
            }
            exoPlayer.release();
            exoPlayer = null;
        }

        destroyMusicPlayer();
        changeStateResume(false);
    }

    public void destroyMusicPlayer() {
        SharedPreferences sharedPreferences = getSharedPreferences("music_player_state", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_STATE_IS_CREATED, true);
        editor.putBoolean(KEY_STATE_IS_DESTROYED, true);
        editor.putBoolean(KEY_STATE_IS_PLAYING, false);
        editor.putBoolean(KEY_STATE_IS_START, false);
        editor.apply();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        sendIntentToActivity(MUSIC_PLAYER_ACTION_COMPLETE, 0);
        Log.d(TAG, "onCompletion: hát xong");
    }

    public ArrayList<MediaItem> getListMediaItem(ArrayList<Music> list) {
        ArrayList<MediaItem> listMediaItem = new ArrayList<>();
        for (Music music : list) {
            MediaItem.Builder mediaItem = new MediaItem.Builder();
            if (Objects.equals(playMode, MUSIC_PLAYER_MODE_ONLINE)) {
                mediaItem.setUri(music.getUrl());
            } else {
                mediaItem.setUri(Uri.parse(music.getUrl()));
            }
            mediaItem.setMediaId(music.getId()).setMediaMetadata(getMetaDataSong(music));
            listMediaItem.add(mediaItem.build());
        }
        return listMediaItem;
    }

    public void setUpListMediaSource(ArrayList<ProgressiveMediaSource> list) {
        for (ProgressiveMediaSource mediaSource : list) {
            exoPlayer.addMediaSource(mediaSource);
        }
    }

    public ArrayList<ProgressiveMediaSource> getListMediaSource(ArrayList<Music> list) {
        ArrayList<ProgressiveMediaSource> result = new ArrayList<>();
        for (Music music : list) {
            MediaItem.Builder mediaItem = new MediaItem.Builder();
            mediaItem.setMediaId(music.getId()).setMediaMetadata(getMetaDataSong(music));
            ProgressiveMediaSource progressiveMediaSource = null;
            if (Objects.equals(playMode, MUSIC_PLAYER_MODE_ONLINE)) {
                mediaItem.setUri(music.getUrl());
//                progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem.build());
            } else {
                DataSpec dataSpec = new DataSpec(Uri.parse(music.getUrl()));
                final FileDataSource fileDataSource = new FileDataSource();
                try {
                    fileDataSource.open(dataSpec);
                } catch (FileDataSource.FileDataSourceException e) {
                    e.printStackTrace();
                }
                mediaItem.setUri(fileDataSource.getUri());
            }
            progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem.build());
            result.add(progressiveMediaSource);
        }
        return result;
    }

    public void setUpPlaylistForExoPlayer(ArrayList<MediaItem> listMediaItem) {
        for (MediaItem mediaItem : listMediaItem) {
            exoPlayer.addMediaItem(mediaItem);
        }
//        exoPlayer.prepare();
    }

    public com.google.android.exoplayer2.MediaMetadata getMetaDataSong(Music music) {
        return new com.google.android.exoplayer2.MediaMetadata.Builder()
                .setTitle(music.getName())
                .setArtworkUri(Uri.parse(music.getThumbnailUrl()))
                .setArtist(music.getSingerName())
                .build();
    }

    public boolean checkUniqueSong(String songName) {
        ArrayList<Music> list = (ArrayList<Music>) SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().checkSong(songName);
        return list.size() <= 0;
    }

    public void saveCurrentMusic(Music currentSong) {
        if (checkUniqueSong(currentSong.getName())) {
            SongRecentDatabase.getInstance(getApplicationContext()).musicRecentDAO().insertSong(currentSong);
        }
        SharedPreferences.Editor editor = MusicPlayerStorage.getInstance(this).edit();
        editor.putString(KEY_SONG_NAME, currentSong.getName());
        editor.putString(KEY_SONG_URL, currentSong.getUrl());
        editor.putString(KEY_SONG_THUMBNAIL_URL, currentSong.getThumbnailUrl());
        editor.putString(KEY_SONG_ID, currentSong.getId());
        editor.putLong(KEY_SONG_VIEWS, currentSong.getViews());
        editor.putString(KEY_SONG_SINGER_ID, currentSong.getSingerId());
        editor.putString(KEY_SONG_SINGER_NAME, currentSong.getSingerName());
        editor.putString(KEY_SONG_GENRES_ID, currentSong.getGenresId());
        editor.putLong(KEY_SONG_CREATION_DATE, currentSong.getCreationDate());
        editor.putLong(KEY_SONG_UPDATE_DATE, currentSong.getUpdateDate());
        editor.putInt(KEY_SONG_INDEX, playlistSong.indexOf(currentSong));
        Log.d(TAG, "currentIndexSong: " + playlistSong.indexOf(currentSong));
        Log.d(TAG, "currentIdPlaylist: " + MusicPlayerStorage.getInstance(this).getString(KEY_ID_OF_PLAYLIST, KEY_TOP_10));
        editor.apply();
    }

    public void handleStatePlayer() {
        SharedPreferences.Editor editor = MusicPlayerStorage.getInstance(this).edit();
        editor.putBoolean(KEY_IS_PLAYING, exoPlayer.isPlaying());
        editor.apply();
    }

    public void changeStateResume(boolean isResume) {
        SharedPreferences.Editor editor = StateMusicPlayerStorage.getInstance(this).edit();
        editor.putBoolean(KEY_IS_RESUME, isResume);
        editor.apply();
    }
}
