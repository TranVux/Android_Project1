package com.example.assignment_pro1121_nhom3.models;

import android.util.Log;

import com.example.assignment_pro1121_nhom3.utils.RemoveDuplicateArrayItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class MusicPlayer implements Serializable {

    //debug
    public static final String TAG = MusicPlayer.class.getSimpleName();
    //event của broadcast receiver
    public static final String MUSIC_PLAYER_EVENT = "MUSIC_PLAYER_EVENT";

    //state của player
    public static final String MUSIC_PLAYER_STATE_CREATED = "MUSIC_PLAYER_STATE_CREATED";
    public static final String MUSIC_PLAYER_STATE_IDLE = "MUSIC_PLAYER_STATE_IDLE";
    public static final String MUSIC_PLAYER_STATE_PLAYING = "MUSIC_PLAYER_STATE_PLAYING";
    public static final String MUSIC_PLAYER_STATE_DESTROYED = "MUSIC_PLAYER_STATE_DESTROYED";

    //action của player
    public static final int MUSIC_PLAYER_ACTION_START = 111;
    public static final int MUSIC_PLAYER_ACTION_NEXT = 222;
    public static final int MUSIC_PLAYER_ACTION_PREVIOUS = 333;
    public static final int MUSIC_PLAYER_ACTION_PAUSE = 444;
    public static final int MUSIC_PLAYER_ACTION_RESUME = 555;
    public static final int MUSIC_PLAYER_ACTION_DESTROY = 666;
    public static final int MUSIC_PLAYER_ACTION_COMPLETE = 777;
    public static final int MUSIC_PLAYER_ACTION_RESET_SONG = 888;
    public static final int MUSIC_PLAYER_ACTION_GO_TO_SONG = 999;
    public static final int MUSIC_PLAYER_ACTION_SEEK_TO_POSITION = 101010;

    private static MusicPlayer musicPlayer;
    private ArrayList<Music> playListMusic;
    private Music currentSong;
    private int currentPositionSong;
    private int durationCurrentSong;
    private String playerState;
    private MusicPlayerCallback musicPlayerCallback;

    public static MusicPlayer getInstance(MusicPlayerCallback musicPlayerCallback) {
        if (musicPlayer == null)
            return new MusicPlayer(musicPlayerCallback);
        return musicPlayer;
    }

    public static MusicPlayer getInstance() {
        if (musicPlayer == null) return new MusicPlayer();
        return musicPlayer;
    }

    public static MusicPlayer getInstance(ArrayList<Music> playListMusic) {
        if (musicPlayer == null) return new MusicPlayer(playListMusic);
        return musicPlayer;
    }

    private MusicPlayer() {
        initState();
    }

    private MusicPlayer(ArrayList<Music> playListMusic) {
        this.playListMusic = playListMusic;
        initState();
    }

    private MusicPlayer(MusicPlayerCallback musicPlayerCallback) {
        this.musicPlayerCallback = musicPlayerCallback;
        initState();
    }

    private MusicPlayer(ArrayList<Music> playListMusic, MusicPlayerCallback musicPlayerCallback) {
        this.playListMusic = playListMusic;
        this.musicPlayerCallback = musicPlayerCallback;
        initState();
    }

    public void setMusicPlayerCallBack(MusicPlayerCallback musicPlayerCallback) {
        this.musicPlayerCallback = musicPlayerCallback;
    }

    private void initState() {
        playerState = MUSIC_PLAYER_STATE_CREATED;
        currentSong = null;
        currentPositionSong = 0;
        durationCurrentSong = 0;
    }

    public void setPlayList(ArrayList<Music> playListMusic) {
        ArrayList<Music> listTemp = this.playListMusic;
        listTemp.addAll(playListMusic);
        this.playListMusic = RemoveDuplicateArrayItem.getList(listTemp);
    }

    public void setMusicAtPosition(int index) {
        currentSong = playListMusic.get(index);
    }

    public ArrayList<Music> getPlayListMusic() {
        return this.playListMusic;
    }

    public String getStateMusicPlayer() {
        return playerState;
    }

    public void setStateMusicPlayer(String playerState) throws Exception {
        switch (playerState) {
            case MUSIC_PLAYER_STATE_IDLE:
            case MUSIC_PLAYER_STATE_PLAYING:
            case MUSIC_PLAYER_STATE_CREATED:
            case MUSIC_PLAYER_STATE_DESTROYED: {
                this.playerState = playerState;
                break;
            }
            default:
                throw new Exception("Invalid State For Music Player");
        }
    }

    public void start() {
        if (playListMusic != null && playListMusic.size() > 0) {
            currentSong = playListMusic.get(0);
        }
    }

    public void destroyPlayer() {
        musicPlayer = null;
    }

    public int getCurrentIndexSong() {
        if (currentSong != null) return playListMusic.indexOf(currentSong);
        return 0;
    }

    public void setInitState(boolean isPlay, boolean isStart, boolean isDestroy, boolean isCreated) {
        if (isPlay && isStart) {
            this.playerState = MUSIC_PLAYER_STATE_PLAYING;
        } else if (isStart) {
            this.playerState = MUSIC_PLAYER_STATE_IDLE;
        } else if (isCreated) {
            this.playerState = MUSIC_PLAYER_STATE_CREATED;
        } else {
            this.playerState = MUSIC_PLAYER_STATE_DESTROYED;
        }
    }

    public Music getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Music song) {
        this.currentSong = song;
    }

    public Music getNextSong() {
        if (getCurrentIndexSong() >= playListMusic.size() - 1) {
            return playListMusic.get(0);
        }
        return playListMusic.get(getCurrentIndexSong() + 1);
    }

    public Music getPreviousSong() {
        if (getCurrentIndexSong() == 0) {
            return playListMusic.get(getSizeOfPlayList() - 1);
        } else {
            return playListMusic.get(getCurrentIndexSong() - 1);
        }
    }

    public int getCurrentPositionSong() {
        return currentPositionSong;
    }

    public int getDurationCurrentSong() {
        return durationCurrentSong;
    }

    public void setCurrentPositionSong(int currentPositionSong) {
        this.currentPositionSong = currentPositionSong;
    }

    public void setDurationCurrentSong(int durationCurrentSong) {
        this.durationCurrentSong = durationCurrentSong;
    }

    public int getSizeOfPlayList() {
        if (playListMusic == null) return 0;
        return playListMusic.size();
    }

    public Music nextSong(int currentIndex) {
        if (Objects.equals(playerState, MUSIC_PLAYER_STATE_DESTROYED)) return null;
        Music nextMusic;
        if (currentIndex >= playListMusic.size() - 1) {
            nextMusic = playListMusic.get(0);
        } else {
            nextMusic = playListMusic.get(currentIndex + 1);
        }
        this.currentSong = nextMusic;
        musicPlayerCallback.onResume();
        Log.d(TAG, "current song is: " + currentSong.getName());
        return nextMusic;
    }


    public Music previousSong(int currentIndex) {
        if (Objects.equals(playerState, MUSIC_PLAYER_STATE_DESTROYED)) return null;
        Music previousMusic;
        if (currentIndex == 0) {
            previousMusic = playListMusic.get(getSizeOfPlayList() - 1);
        } else {
            previousMusic = playListMusic.get(currentIndex - 1);
        }
        this.currentSong = previousMusic;
        musicPlayerCallback.onResume();
        Log.d(TAG, "current song is: " + currentSong.getName());
        return previousMusic;
    }

    public Music playSong() {
        playerState = MUSIC_PLAYER_STATE_PLAYING;
        return currentSong;
    }

    public int resumeSong() {
        if (Objects.equals(playerState, MUSIC_PLAYER_STATE_DESTROYED)) return 0;
        playerState = MUSIC_PLAYER_STATE_PLAYING;
        musicPlayerCallback.onResume();
        return currentPositionSong;
    }

    public void pauseSong(int currentPositionSong) {
        if (Objects.equals(playerState, MUSIC_PLAYER_STATE_DESTROYED)) return;
        playerState = MUSIC_PLAYER_STATE_IDLE;
        musicPlayerCallback.onPause();
        this.currentPositionSong = currentPositionSong;
    }

    public void updateSeekBar() {
        if (Objects.equals(playerState, MUSIC_PLAYER_STATE_DESTROYED)) return;
        musicPlayerCallback.updateSeekBar(currentPositionSong);
    }

    public void clearPlaylist() {
        this.playListMusic.clear();
    }

    public interface MusicPlayerCallback {
        void onPause();

        void onResume();

        void updateSeekBar(int currentPositionSong);
    }

}
