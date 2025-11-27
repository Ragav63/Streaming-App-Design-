package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.databinding.SeasonListItemsBinding;
import com.example.streamingapp.domain.repository.EpisodeViewMode;

import java.util.List;

public class SeasonEpRecItemAdapter
        extends RecyclerView.Adapter<SeasonEpRecItemAdapter.ItemViewHolder> {

    private final EpisodeViewMode mode;

    // external callbacks
    public interface OnEpisodeSelectedListener {
        void onEpisodeSelected(SeasonItems item, int position);
    }

    public interface OnDownloadClickedListener {
        void onDownloadClick(SeasonItems item, int position);
    }

    private final OnEpisodeSelectedListener episodeListener;
    private final OnDownloadClickedListener downloadListener;

    private int selectedPosition = -1;

    // differ
    private final AsyncListDiffer<SeasonItems> differ;

    public AsyncListDiffer<SeasonItems> getDiffer() {
        return differ;
    }


    public SeasonEpRecItemAdapter(
            List<SeasonItems> initialList,
            EpisodeViewMode mode,
            OnEpisodeSelectedListener episodeListener,
            OnDownloadClickedListener downloadListener
    ) {
        this.mode = mode;
        this.episodeListener = episodeListener;
        this.downloadListener = downloadListener;

        DiffUtil.ItemCallback<SeasonItems> callback = new DiffUtil.ItemCallback<SeasonItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull SeasonItems oldItem, @NonNull SeasonItems newItem) {
                return oldItem.getSeasonEpTitle().equals(newItem.getSeasonEpTitle());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull SeasonItems oldItem, @NonNull SeasonItems newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, callback);
        differ.submitList(initialList);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SeasonListItemsBinding binding =
                SeasonListItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        SeasonItems item = differ.getCurrentList().get(position);

        holder.binding.seasonImg.setImageResource(item.getSeasonEpImg());
        holder.binding.seasonTitleTv.setText(item.getSeasonEpTitle());
        holder.binding.seasonTimingTv.setText(item.getSeasonEpTiming());

        holder.binding.itemll.setOrientation(
                mode == EpisodeViewMode.PLAYER_LANDSCAPE ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL
        );

        // MODE BEHAVIOR
        if (mode == EpisodeViewMode.NORMAL) {
            // download icon state
            if (item.isDownloading()) {
                holder.binding.playIv.setImageResource(R.drawable.download64px);
                holder.binding.playIv.setColorFilter(ContextCompat.getColor(holder.binding.getRoot().getContext(), R.color.white));
                holder.binding.playRl.setBackgroundResource(R.drawable.blueroundcircle_bg);
            } else {
                holder.binding.playIv.setImageResource(android.R.drawable.ic_media_play);
                holder.binding.playIv.clearColorFilter();
                holder.binding.playRl.setBackgroundResource(R.drawable.dimcircle_bg);
            }

            holder.binding.playIv.setOnClickListener(v -> downloadListener.onDownloadClick(item, position));

        } else { // Player portrait or landscape
            holder.binding.playIv.setVisibility(View.INVISIBLE);
            holder.binding.playRl.setVisibility(View.INVISIBLE);

            if (selectedPosition == position) {
                holder.binding.itemCv.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            } else {
                holder.binding.itemCv.setBackgroundColor(Color.TRANSPARENT);
            }

            holder.binding.getRoot().setOnClickListener(v -> setSelected(position));
        }
    }

    private void setSelected(int position) {
        int prev = selectedPosition;
        selectedPosition = position;

        if (prev != -1) notifyItemChanged(prev);
        notifyItemChanged(position);

        if (episodeListener != null) {
            episodeListener.onEpisodeSelected(
                    differ.getCurrentList().get(position),
                    position
            );
        }
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        final SeasonListItemsBinding binding;

        public ItemViewHolder(@NonNull SeasonListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

