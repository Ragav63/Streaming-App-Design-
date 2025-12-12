package com.example.streamingapp.presentation.utils;

import android.content.Context;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

public class PlayerController {
    protected ExoPlayer player;
    private Context context;
    private Player.Listener playerListener;

    public interface PlayerStateListener {
        void onPlayerReady(long duration);
        void onPlaybackStateChanged(boolean isPlaying);
        void onPlaybackEnded();
        void onPositionChanged(long position);
    }

    public PlayerController(Context context) {
        player = new ExoPlayer.Builder(context).build();
    }

    public PlayerController(Context context, ExoPlayer existingPlayer) {
        this.context = context;
        this.player = existingPlayer;
    }


    public void initialize(String videoUrl, long startPosition, PlayerStateListener listener) {
        if (videoUrl == null) return;

        playerListener = new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    listener.onPlayerReady(player.getDuration());
                    listener.onPlaybackStateChanged(player.isPlaying());
                } else if (state == Player.STATE_ENDED) {
                    listener.onPlaybackEnded();
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                listener.onPlaybackStateChanged(isPlaying);
            }
        };

        player.addListener(playerListener);
        player.setMediaItem(MediaItem.fromUri(videoUrl));
        player.prepare();
        player.seekTo(startPosition);
        player.play();
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public void setMediaItem(MediaItem item) {
        player.setMediaItem(item);
    }

    public void prepareAndPlay() {
        player.prepare();
        player.play();
    }

    public void togglePlayPause() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.play();
        }
    }

    // Add direct listener attachment method
    public void addPlayerListener(Player.Listener listener) {
        player.addListener(listener);
    }

    public void removePlayerListener(Player.Listener listener) {
        player.removeListener(listener);
    }

    public void prepare() {
        player.prepare();
    }

    public void play() {
        player.play();
    }

    public void pause() {
        player.pause();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void seekTo(long pos) {
        player.seekTo(pos);
    }

    public void seekForward(long ms) {
        seekTo(player.getCurrentPosition() + ms);
    }

    public void seekBackward(long ms) {
        seekTo(player.getCurrentPosition() - ms);
    }

    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public long getDuration() {
        return player.getDuration();
    }
}
