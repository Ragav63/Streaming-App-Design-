package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.SeasonItems;

import java.util.List;

public class SeasonEpRecItemAdapter extends RecyclerView.Adapter<SeasonEpRecItemAdapter.ItemViewHolder>{

    private static Context context;
    private List<SeasonItems> itemList;
    private boolean fromSeriesPlayerScreenActivity;
    private boolean fromSeriesLandscapePlayerScreenActivity;
    private int selectedPosition = 0;
    public SeasonEpRecItemAdapter(Context context, List<SeasonItems> itemList, boolean fromSeriesPlayerScreenActivity, boolean fromSeriesLandscapePlayerScreenActivity) {
        this.context = context;
        this.itemList = itemList;
        this.fromSeriesPlayerScreenActivity = fromSeriesPlayerScreenActivity;
        this.fromSeriesLandscapePlayerScreenActivity = fromSeriesLandscapePlayerScreenActivity;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.season_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        SeasonItems item = itemList.get(position);

        holder.seasonEpImg.setImageResource(item.getSeasonEpImg());
        holder.seasonEpTitle.setText(item.getSeasonEpTitle());
        holder.seasonEpTiming.setText(item.getSeasonEpTiming());

        if (fromSeriesLandscapePlayerScreenActivity) {
            holder.itemll.setOrientation(LinearLayout.VERTICAL);
            holder.seasonEpPlayIv.setVisibility(View.INVISIBLE); // Play icon
            holder.seasonEpPlayIv.clearColorFilter(); // Clear color filter
            holder.playRl.setVisibility(View.INVISIBLE); // Default background



            if (selectedPosition == position) {
                holder.itemCv.setBackground(ContextCompat.getDrawable(context, R.drawable.lgtransparentbluestroke_bg));
            } else {
                holder.itemCv.setBackgroundColor(Color.TRANSPARENT);
            }

            holder.itemView.setOnClickListener(v -> updateSelectedPosition(position));

        } else if (fromSeriesPlayerScreenActivity) {
            holder.itemll.setOrientation(LinearLayout.HORIZONTAL);
            holder.seasonEpPlayIv.setVisibility(View.INVISIBLE);
            holder.seasonEpPlayIv.clearColorFilter();
            holder.playRl.setVisibility(View.INVISIBLE);


            if (selectedPosition == position) {
                holder.itemCv.setBackground(ContextCompat.getDrawable(context, R.drawable.lgtransparentbluestroke_bg));
            } else {
                holder.itemCv.setBackgroundColor(Color.TRANSPARENT);
            }

            holder.itemView.setOnClickListener(v -> updateSelectedPosition(position));
        } else {
            holder.itemll.setOrientation(LinearLayout.HORIZONTAL);


            if (item.isDownloading()) {
                holder.seasonEpPlayIv.setImageResource(R.drawable.download64px);
                holder.seasonEpPlayIv.setColorFilter(ContextCompat.getColor(context, R.color.white));
                holder.playRl.setBackgroundResource(R.drawable.blueroundcircle_bg);
            } else {
                holder.seasonEpPlayIv.setImageResource(android.R.drawable.ic_media_play);
                holder.seasonEpPlayIv.clearColorFilter();
                holder.playRl.setBackgroundResource(R.drawable.dimcircle_bg);
            }

            holder.seasonEpPlayIv.setOnClickListener(v -> {
                if (item.isDownloading()) {
                    holder.seasonEpPlayIv.setImageResource(android.R.drawable.ic_media_play); // Default play icon
                    holder.seasonEpPlayIv.clearColorFilter();
                    holder.playRl.setBackgroundResource(R.drawable.dimcircle_bg); // Default background

                    Toast.makeText(context, "Download Paused. Click again to continue.", Toast.LENGTH_SHORT).show();
                    item.setDownloading(false); // Update state
                } else {
                    // Change to download state
                    holder.seasonEpPlayIv.setImageResource(R.drawable.download64px);
                    holder.seasonEpPlayIv.setColorFilter(ContextCompat.getColor(context, R.color.white));
                    holder.playRl.setBackgroundResource(R.drawable.blueroundcircle_bg);
                    openDownloadDialog(holder.seasonEpImg, holder.seasonEpTitle.getText().toString());
                    Toast.makeText(context, "Series Episode can be viewed only after the download", Toast.LENGTH_SHORT).show();
                    item.setDownloading(true);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView seasonEpImg, seasonEpPlayIv;
        TextView seasonEpTitle, seasonEpTiming;
        LinearLayout itemll, titleDescLl;
        RelativeLayout playRl;
        CardView itemCv;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            seasonEpImg = itemView.findViewById(R.id.seasonImg);
            seasonEpTitle = itemView.findViewById(R.id.seasonTitle_tv);
            seasonEpTiming = itemView.findViewById(R.id.seasonTiming_tv);
            seasonEpPlayIv = itemView.findViewById(R.id.playIv);
            itemll=itemView.findViewById(R.id.itemll);
            playRl = itemView.findViewById(R.id.playRl);
            itemCv = itemView.findViewById(R.id.itemCv);
        }
    }

    private void updateSelectedPosition(int position) {
        int previousSelectedPosition = selectedPosition;
        selectedPosition = position;

        // Notify previous and new selected item of the change
        if (previousSelectedPosition != -1) {
            notifyItemChanged(previousSelectedPosition);
        }
        notifyItemChanged(selectedPosition);
    }

    private void openDownloadDialog(ImageView movieImageView, String movieTitle) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_download);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView downloadIv = dialog.findViewById(R.id.downloadIv);
        TextView downloadTv = dialog.findViewById(R.id.downloadTv);
        TextView downloadTitle, downloadQualityVal, downloadAudio1, downloadAudio2, downloadSubtitleOff, downloadSubtitle1, downloadSubtitle2;
        SeekBar qualitySbar = dialog.findViewById(R.id.qualitySeekbar);

        downloadTitle = dialog.findViewById(R.id.downloadTitleTv);
        downloadQualityVal = dialog.findViewById(R.id.qualityValTv);
        downloadAudio1 = dialog.findViewById(R.id.audio1Tv);
        downloadAudio2 = dialog.findViewById(R.id.audio2Tv);
        downloadSubtitleOff = dialog.findViewById(R.id.subtitleOffTv);
        downloadSubtitle1 = dialog.findViewById(R.id.subtitle1Tv);
        downloadSubtitle2 = dialog.findViewById(R.id.subtitle2Tv);

        downloadIv.setImageDrawable(movieImageView.getDrawable());
        downloadTitle.setText(movieTitle);

        // Set default audio and subtitle selections
        downloadAudio1.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
        downloadSubtitleOff.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);

        // Configure SeekBar for quality selection
        qualitySbar.setMax(100);
        qualitySbar.setProgress(25); // Default to 25%

        qualitySbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 25) {
                    downloadQualityVal.setText("Low (360p)");
                } else if (progress < 50) {
                    downloadQualityVal.setText("Medium (480p)");
                } else if (progress < 75) {
                    downloadQualityVal.setText("High (720p)");
                } else {
                    downloadQualityVal.setText("HD (1080p)");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Set listeners for audio options
        downloadAudio1.setOnClickListener(v -> {
            downloadAudio1.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadAudio2.setBackgroundResource(R.drawable.dimcircle_bg);
        });

        downloadAudio2.setOnClickListener(v -> {
            downloadAudio2.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadAudio1.setBackgroundResource(R.drawable.dimcircle_bg);
        });

        downloadSubtitleOff.setOnClickListener(v -> {
            downloadSubtitleOff.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadSubtitle1.setBackgroundResource(R.drawable.dimcircle_bg);
            downloadSubtitle2.setBackgroundResource(R.drawable.dimcircle_bg);
        });
        // Set listeners for subtitle options
        downloadSubtitle1.setOnClickListener(v -> {
            downloadSubtitle1.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadSubtitle2.setBackgroundResource(R.drawable.dimcircle_bg);
            downloadSubtitleOff.setBackgroundResource(R.drawable.dimcircle_bg);
        });

        downloadSubtitle2.setOnClickListener(v -> {
            downloadSubtitle2.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadSubtitle1.setBackgroundResource(R.drawable.dimcircle_bg);
            downloadSubtitleOff.setBackgroundResource(R.drawable.dimcircle_bg);
        });


        downloadTv.setOnClickListener(v -> {
            Toast.makeText(dialog.getContext(), "Started to Download", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}
