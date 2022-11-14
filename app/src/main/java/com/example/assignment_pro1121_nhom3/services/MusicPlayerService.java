package com.example.assignment_pro1121_nhom3.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.assignment_pro1121_nhom3.R;
import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.views.MainActivity;

import static com.example.assignment_pro1121_nhom3.models.MusicPlayer.*;
import static com.example.assignment_pro1121_nhom3.utils.Constants.*;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;

import kotlin.jvm.internal.LocalVariableReference;

public class MusicPlayerService extends Service {
    private Music currentSong;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private boolean isPlaying = false;
    MediaSessionCompat mediaSessionCompat;
    MediaControllerCompat mediaControllerCompat;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int action = intent.getIntExtra("action", -1);
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
            default:
                Log.d(TAG, "handleActionMusicPlayer: Invalid action for music player");
                break;
        }
    }

    private void destroyPlayer() {
        onDestroy();
        sendIntentToActivity(MUSIC_PLAYER_ACTION_DESTROY);
    }

    private void resumeSong() {
        if (!isPlaying && mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            sendIntentToActivity(MUSIC_PLAYER_ACTION_RESUME);
            sendNotification();
        }
    }

    private void pauseSong() {
        if (isPlaying && mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            sendIntentToActivity(MUSIC_PLAYER_ACTION_PAUSE);
            sendNotification();
        }
    }

    private void previousSong() {
        if (mediaPlayer != null) {
            sendIntentToActivity(MUSIC_PLAYER_ACTION_PREVIOUS);
        }
    }

    private void nextSong() {
        Log.d(TAG, "nextSong: ");
        if (mediaPlayer != null) {
            sendIntentToActivity(MUSIC_PLAYER_ACTION_NEXT);
        }
    }

    public void sendIntentToActivity(int action) {
        Intent outIntent = new Intent(MUSIC_PLAYER_EVENT);
        outIntent.putExtra("action", action);
        if (mediaPlayer != null) {
            outIntent.putExtra(KEY_CURRENT_MUSIC_POSITION, mediaPlayer.getCurrentPosition() / 1000);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(outIntent);
    }

    private void initService(Intent intent) {
        Music songReceiver = (Music) intent.getSerializableExtra(KEY_MUSIC);
        if (songReceiver == null) {
            Log.d(TAG, "initService: chưa có bài hát chuyển qua service");
            return;
        }
        currentSong = songReceiver;
        installMediaPlayer();
        sendNotification();
    }

    public void installMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaSessionCompat = new MediaSessionCompat(this, MusicPlayerService.class.getName());
        mediaPlayer.setAudioAttributes(new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        setMusicUrl(currentSong.getUrl());
        timer = new Timer();

    }

    public void setMusicUrl(String url) {
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNotification() {
        createNotificationChannel();
        Intent homeIntent = new Intent(this, MainActivity.class);
        PendingIntent homePendingIntent = PendingIntent.getActivity(this, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final Bitmap[] musicThumbnail = new Bitmap[1];
        mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, currentSong.getName())
                .putString(MediaMetadata.METADATA_KEY_ARTIST, currentSong.getSingerName())
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, currentSong.getThumbnailUrl())
                .putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.getDuration() / 1000)
                .build()
        );
        mediaControllerCompat = new MediaControllerCompat(getApplicationContext(), mediaSessionCompat.getSessionToken());

        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this, "AUDIO_SERVICE")
                .setSmallIcon(R.drawable.ic_notiffication_new)
                .setContentText(currentSong.getSingerName())
                .setContentTitle(currentSong.getName())
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
    }
}
