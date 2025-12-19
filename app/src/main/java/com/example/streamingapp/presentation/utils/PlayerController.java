package com.example.streamingapp.presentation.utils;

import android.content.Context;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

public class PlayerController {
    protected ExoPlayer player;
    private Context context;

    public PlayerController(Context context) {
        player = new ExoPlayer.Builder(context).build();
    }

    public PlayerController(Context context, ExoPlayer existingPlayer) {
        this.context = context;
        this.player = existingPlayer;
    }


    public ExoPlayer getPlayer() {
        return player;
    }

    public void setMediaItem(MediaItem item) {
        player.setMediaItem(item);
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
