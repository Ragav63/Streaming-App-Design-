package com.example.streamingapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;

public class PlayerViewModel extends ViewModel {
    private MutableLiveData<PlayerState> playerState = new MutableLiveData<>(new PlayerState());
    private ExoPlayer exoPlayer;

    public LiveData<PlayerState> getPlayerState() {
        return playerState;
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public void setExoPlayer(ExoPlayer player) {
        this.exoPlayer = player;
    }

    public void updateState(PlayerState newState) {
        playerState.setValue(newState);
    }

    public void updatePlaying(boolean isPlaying) {
        PlayerState state = getPlayerState().getValue();
        if (state != null) {
            state.isPlaying = isPlaying;
            playerState.setValue(state);
        }
    }

    public void updatePlaybackState(boolean isPlaying, long position, long duration) {
        PlayerState current = playerState.getValue();
        if (current != null) {
            PlayerState newState = new PlayerState(
                    current.title,
                    current.rating,
                    current.seasonNumber,
                    current.episodeNumber,
                    isPlaying,
                    position,
                    duration,
                    current.playbackSpeed,
                    current.isFavourite,
                    current.isDownloaded
            );
            playerState.setValue(newState);
        }
    }

    public void setPlaybackSpeed(float speed) {
        PlayerState current = playerState.getValue();
        if (current != null && exoPlayer != null) {
            PlayerState newState = new PlayerState(
                    current.title,
                    current.rating,
                    current.seasonNumber,
                    current.episodeNumber,
                    current.isPlaying,
                    current.position,
                    current.duration,
                    speed,
                    current.isFavourite,
                    current.isDownloaded
            );
            playerState.setValue(newState);
            exoPlayer.setPlaybackParameters(new PlaybackParameters(speed));
        }
    }

    public void toggleFavourite() {
        PlayerState current = playerState.getValue();
        if (current != null) {
            PlayerState newState = new PlayerState(
                    current.title,
                    current.rating,
                    current.seasonNumber,
                    current.episodeNumber,
                    current.isPlaying,
                    current.position,
                    current.duration,
                    current.playbackSpeed,
                    !current.isFavourite,
                    current.isDownloaded
            );
            playerState.setValue(newState);
        }
    }

    public void toggleDownloaded() {
        PlayerState current = playerState.getValue();
        if (current != null) {
            PlayerState newState = new PlayerState(
                    current.title,
                    current.rating,
                    current.seasonNumber,
                    current.episodeNumber,
                    current.isPlaying,
                    current.position,
                    current.duration,
                    current.playbackSpeed,
                    current.isFavourite,
                    !current.isDownloaded
            );
            playerState.setValue(newState);
        }
    }

    @Override
    protected void onCleared() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        super.onCleared();
    }

    public static class PlayerState {
        public final String title;
        public final String rating;
        public final int seasonNumber;
        public final int episodeNumber;
        public boolean isPlaying;
        public final long position;
        public final long duration;
        public final float playbackSpeed;
        public final boolean isFavourite;
        public final boolean isDownloaded;

        public PlayerState() {
            this("", "", 1, 1, false, 0, 0, 1.0f, false, false);
        }

        public PlayerState(String title, String rating, int seasonNumber, int episodeNumber,
                           boolean isPlaying, long position, long duration, float playbackSpeed,
                           boolean isFavourite, boolean isDownloaded) {
            this.title = title;
            this.rating = rating;
            this.seasonNumber = seasonNumber;
            this.episodeNumber = episodeNumber;
            this.isPlaying = isPlaying;
            this.position = position;
            this.duration = duration;
            this.playbackSpeed = playbackSpeed;
            this.isFavourite = isFavourite;
            this.isDownloaded = isDownloaded;
        }
    }
}
