package com.example.assignment_pro1121_nhom3.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.MusicPlayer;
import com.example.assignment_pro1121_nhom3.utils.CapitalizeWord;
import com.example.assignment_pro1121_nhom3.views.MainActivity;

import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.*;
import static com.example.assignment_pro1121_nhom3.utils.Constants.*;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.jvm.internal.LocalVariableReference;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener {

    public static final String TAG = MusicPlayerService.class.getSimpleName();

    private Music currentSong;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Timer timer;
    private boolean isPlaying = false;
    MediaSessionCompat mediaSessionCompat;
    MediaControllerCompat mediaControllerCompat;
    private String stateServiceMusicPlayer = CHANGE_TO_SERVICE;
    private boolean isLoadSuccess = false;
    private String playMode;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        installMediaPlayer();
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
            case MUSIC_PLAYER_ACTION_RESET_SONG: {
                Music music = (Music) intent.getSerializableExtra(KEY_MUSIC);
                resetSong(music);
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
                resumeSong();
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
                Log.d(TAG, "handleActionMusicPlayer: " + musicDurationPosition);
                seekToPosition(musicDurationPosition);
                break;
            }
            default:
                Log.d(TAG, "handleActionMusicPlayer: Invalid action for music player");
                break;
        }
    }


    private void resetSong(Music music) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        timer.cancel();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        mediaPlayer.setOnCompletionListener(this);
        currentSong = music;
        timer = new Timer();
        setMusicUrl(currentSong.getUrl());
    }

    public void seekToPosition(int position) {
        if (!isLoadSuccess) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            sendNotification();
        }
        mediaPlayer.seekTo(position * 1000);
        Log.d(TAG, "seekToPosition: ");
        mediaPlayer.start();
        isPlaying = true;
        sendNotification();
        sendIntentToActivity(MUSIC_PLAYER_ACTION_SEEK_TO_POSITION, 0);
    }

    private void destroyPlayer() {
        onDestroy();
        sendIntentToActivity(MUSIC_PLAYER_ACTION_DESTROY, 0);
    }

    private void goToSong(int index) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            if (Objects.equals(stateServiceMusicPlayer, CHANGE_TO_SERVICE)) {
                sendIntentToActivity(MUSIC_PLAYER_ACTION_GO_TO_SONG, index);
            }
        }
    }

    private void resumeSong() {
        if (!isPlaying && mediaPlayer != null && currentSong != null && isLoadSuccess) {
            mediaPlayer.start();
            isPlaying = true;
            if (Objects.equals(stateServiceMusicPlayer, CHANGE_TO_SERVICE)) {
                sendIntentToActivity(MUSIC_PLAYER_ACTION_RESUME, 0);
            }
            sendNotification();
        } else if (currentSong == null) {
            sendIntentToActivity(MUSIC_PLAYER_ACTION_GO_TO_SONG, getSharedPreferences("music_player", MODE_PRIVATE).getInt(KEY_SONG_INDEX, 0));
        }
    }

    private void pauseSong() {
        if (isPlaying && mediaPlayer != null && isLoadSuccess) {
            mediaPlayer.pause();
            isPlaying = false;
            if (Objects.equals(stateServiceMusicPlayer, CHANGE_TO_SERVICE)) {
                sendIntentToActivity(MUSIC_PLAYER_ACTION_PAUSE, 0);
            }
            sendNotification();
        }
    }

    private void previousSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            if (Objects.equals(stateServiceMusicPlayer, CHANGE_TO_SERVICE)) {
                Log.d(TAG, "previousSong: ");
                sendIntentToActivity(MUSIC_PLAYER_ACTION_PREVIOUS, 0);
//                timer.cancel();
            }
        }
    }

    private void nextSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            if (Objects.equals(stateServiceMusicPlayer, CHANGE_TO_SERVICE)) {
                Log.d(TAG, "nextSong: ");
                sendIntentToActivity(MUSIC_PLAYER_ACTION_NEXT, 0);
//                timer.cancel();
            }
        }
    }

    public void sendIntentToActivity(int action, int index) {
        Intent outIntent = new Intent(MUSIC_PLAYER_EVENT);
        outIntent.putExtra("action", action);
        outIntent.putExtra(KEY_SONG_INDEX, index);
        LocalBroadcastManager.getInstance(this).sendBroadcast(outIntent);
    }

    private void initService(Intent intent) {
        Music songReceiver = (Music) intent.getSerializableExtra(KEY_MUSIC);
        if (songReceiver == null) {
            Log.d(TAG, "initService: chưa có bài hát chuyển qua service");
            return;
        }
        currentSong = songReceiver;
        setMusicUrl(currentSong.getUrl());
        sendNotification();
    }

    public void installMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaSessionCompat = new MediaSessionCompat(this, MusicPlayerService.class.getName());
        mediaPlayer.setAudioAttributes(new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        mediaPlayer.setOnCompletionListener(this);
        timer = new Timer();
    }

    public void setMusicUrl(String url) {
        try {
            isLoadSuccess = false;
            if (playMode.equals(MUSIC_PLAYER_MODE_ONLINE)) {
                mediaPlayer.setDataSource(url);
            } else if (playMode.equals(MUSIC_PLAYER_MODE_LOCAL)) {
                mediaPlayer.setDataSource(MusicPlayerService.this, Uri.parse(url));
            }

            Log.d(TAG, "onPrepared: Setup");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    sendNotification();
                    Log.d(TAG, "onPrepared: Complete");
                    mediaPlayer.start();
                    sendIntentToActivity(MUSIC_PLAYER_ACTION_RESUME, 0);
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            isLoadSuccess = true;
                            Intent durationIntent = new Intent(MUSIC_PLAYER_EVENT);
                            durationIntent.putExtra(KEY_CURRENT_MUSIC_POSITION, mediaPlayer.getCurrentPosition() / 1000);
                            durationIntent.putExtra(KEY_MUSIC_DURATION, mediaPlayer.getDuration() / 1000);
                            LocalBroadcastManager.getInstance(MusicPlayerService.this).sendBroadcast(durationIntent);
                        }
                    }, 0, 1000);
                }
            });
            mediaPlayer.prepareAsync();
            isPlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification() {
        createNotificationChannel();
        Intent homeIntent = new Intent(this, MainActivity.class);
        PendingIntent homePendingIntent = PendingIntent.getActivity(this, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final Bitmap[] musicThumbnail = new Bitmap[1];
        mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, CapitalizeWord.CapitalizeWords(currentSong.getName()))
                .putString(MediaMetadata.METADATA_KEY_ARTIST, CapitalizeWord.CapitalizeWords(currentSong.getSingerName()))
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, currentSong.getThumbnailUrl())
                .putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.getDuration() / 1000)
                .build()
        );

        mediaControllerCompat = new MediaControllerCompat(getApplicationContext(), mediaSessionCompat.getSessionToken());

        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this, "AUDIO_SERVICE")
                .setSmallIcon(R.drawable.ic_notiffication_new)
                .setContentText(CapitalizeWord.CapitalizeWords(currentSong.getSingerName()))
                .setContentTitle(CapitalizeWord.CapitalizeWords(currentSong.getName()))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .setContentIntent(homePendingIntent)
                .setSound(null);

        if (isPlaying) {
            notificationCompatBuilder
                    .addAction(R.drawable.ic_small_previous, "Previous", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_PREVIOUS))
                    .addAction(R.drawable.ic_small_pause, "Pause", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_PAUSE))
                    .addAction(R.drawable.ic_small_next, "Next", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_NEXT));
        } else {
            notificationCompatBuilder
                    .addAction(R.drawable.ic_small_previous, "Previous", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_PREVIOUS))
                    .addAction(R.drawable.ic_small_play, "Resume", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_RESUME))
                    .addAction(R.drawable.ic_small_next, "Next", getPendingIntentAction(this, MUSIC_PLAYER_ACTION_NEXT));
        }

        Glide.with(MusicPlayerService.this)
                .asBitmap()
                .load(currentSong.getThumbnailUrl())
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
        return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            timer.cancel();
            mediaPlayer = null;
        }
        destroyMusicPlayer();
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
}
