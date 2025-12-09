package com.example.streamingapp.presentation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;

import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;

import java.util.Locale;

public class PopupMenuHelper {

    public interface SpeedMenuCallback {
        void onSpeedSelected(float speed);
    }

    public interface AudioMenuCallback {
        void onAudioSelected(String audioOption);
    }

    public interface SettingsMenuCallback {
        void onSpeedMenuSelected(View anchor);
        void onAudioMenuSelected(View anchor);
    }

    /**
     * Shows the main settings menu (Speed, Audio)
     */
    public static void showSettingsMenu(View anchor, Context context, SettingsMenuCallback callback) {
        PopupMenu popupMenu = new PopupMenu(context, anchor, 0, 0, R.style.PopupTransparent);
        popupMenu.getMenu().add("Speed");
        popupMenu.getMenu().add("Audio");
        forcePopupMenuTextWhite(popupMenu);

        popupMenu.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();

            if (title.equals("Speed")) {
                if (callback != null) {
                    callback.onSpeedMenuSelected(anchor);
                }
            } else if (title.equals("Audio")) {
                if (callback != null) {
                    callback.onAudioMenuSelected(anchor);
                }
            }
            return true;
        });

        popupMenu.show();
    }

    /**
     * Shows speed selection menu with callback
     */
    public static void showSpeedMenu(View anchor, Context context, SpeedMenuCallback callback) {
        LocalManager local = new LocalManager(context);

        PopupMenu menu = new PopupMenu(context, anchor, 0, 0, R.style.PopupTransparent);

        String[] speeds = {"0.25x","0.5x","0.75x","Normal","1.25x","1.5x","2x"};
        float saved = local.getSpeed();

        for (String s : speeds) {
            MenuItem item = menu.getMenu().add(s);

            if (getSpeedValueFromText(s) == saved) {
                setRightIcon(item, context); // blue tick on right
            }
        }

        menu.setOnMenuItemClickListener(item -> {
            float val = getSpeedValueFromText(item.getTitle().toString());
            local.setSpeed(val);

            clearRightIcons(menu);        // remove all ticks
            setRightIcon(item, context);  // set tick on selected item

            if (callback != null) callback.onSpeedSelected(val);
            return true;
        });

        forcePopupMenuTextWhite(menu);
        menu.show();
    }


    @SuppressLint("RestrictedApi")
    private static void forceIcons(PopupMenu menu, View anchor, Context context) {
        MenuPopupHelper helper = new MenuPopupHelper(context, (MenuBuilder) menu.getMenu(), anchor);
        helper.setForceShowIcon(true);
        helper.show();
    }





    /**
     * Shows audio selection menu with callback
     */
    @SuppressLint("RestrictedApi")
    public static void showAudioMenu(View anchor, Context context, AudioMenuCallback callback) {
        LocalManager local = new LocalManager(context);

        PopupMenu menu = new PopupMenu(context, anchor, 0, 0, R.style.PopupTransparent);

        String[] audios = {"Auto", "Stereo", "0.17 Mbps", "0.32 Mbps", "0.64 Mbps"};
        String saved = local.getAudio();

        for (String a : audios) {
            MenuItem item = menu.getMenu().add(a);

            if (a.equals(saved)) {

                item.setIcon(R.drawable.ic_check_blue);
            }
        }
        forcePopupMenuTextWhite(menu);

        menu.setOnMenuItemClickListener(item -> {
            String value = item.getTitle().toString();
            local.setAudio(value);

            if (callback != null) callback.onAudioSelected(value);

            return true;
        });


        menu.show();
        return;

    }

    private static void setRightIcon(MenuItem item, Context context) {
        SpannableString s = new SpannableString(item.getTitle() + "   "); // spacing
        s.setSpan(new ImageSpan(context, R.drawable.ic_check_blue, ImageSpan.ALIGN_BOTTOM),
                s.length() - 1, s.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(s);
    }

    private static void clearRightIcons(PopupMenu menu) {
        for (int i = 0; i < menu.getMenu().size(); i++) {
            MenuItem item = menu.getMenu().getItem(i);
            item.setTitle(item.getTitle().toString()); // reset
        }
    }



    private static void clearMenuIcons(PopupMenu menu) {
        for (int i = 0; i < menu.getMenu().size(); i++) {
            menu.getMenu().getItem(i).setIcon(null);
        }
    }




    /**
     * Shows combined settings menu with player integration
     */
    public static void showPlayerSettingsMenu(View anchor, Context context, ExoPlayer exoPlayer) {
        showSettingsMenu(anchor, context, new SettingsMenuCallback() {
            @Override
            public void onSpeedMenuSelected(View speedAnchor) {
                showSpeedMenu(speedAnchor, context, new SpeedMenuCallback() {
                    @Override
                    public void onSpeedSelected(float speed) {
                        if (exoPlayer != null) {
                            exoPlayer.setPlaybackParameters(new PlaybackParameters(speed));
                        }
                    }
                });
            }

            @Override
            public void onAudioMenuSelected(View audioAnchor) {
                showAudioMenu(audioAnchor, context, new AudioMenuCallback() {
                    @Override
                    public void onAudioSelected(String audioOption) {
                        // Handle audio selection here
                        // You can add audio track selection logic
                    }
                });
            }
        });
    }

    /**
     * Converts speed text to float value
     */
    public static float getSpeedValueFromText(String speedText) {
        switch (speedText) {
            case "0.25x": return 0.25f;
            case "0.5x": return 0.5f;
            case "0.75x": return 0.75f;
            case "1.25x": return 1.25f;
            case "1.5x": return 1.5f;
            case "2x": return 2f;
            case "Normal":
            default: return 1f;
        }
    }

    /**
     * Force popup menu text to be white
     */
    private static void forcePopupMenuTextWhite(PopupMenu menu) {
        menu.setOnDismissListener(null); // avoid leaks
        for (int i = 0; i < menu.getMenu().size(); i++) {
            MenuItem item = menu.getMenu().getItem(i);
            SpannableString s = new SpannableString(item.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            item.setTitle(s);
        }
    }


}
