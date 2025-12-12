package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.databinding.SeasonListItemsBinding;
import com.example.streamingapp.domain.repository.EpisodeViewMode;

import java.util.List;

public class SeasonEpRecItemAdapter
        extends RecyclerView.Adapter<SeasonEpRecItemAdapter.ItemViewHolder> {

    private final Context context;

    // external callback
    public interface OnEpisodeSelectedListener {
        void onEpisodeSelected(Episode item, int position);
    }

    private final OnEpisodeSelectedListener episodeListener;

    private int selectedPosition = -1;


    // differ
    private final AsyncListDiffer<Episode> differ;
    public AsyncListDiffer<Episode> getDiffer() {
        return differ;
    }


    public SeasonEpRecItemAdapter(
            Context context,
            List<Episode> initialList,
            int selectedEpisodeNumber,
            OnEpisodeSelectedListener episodeListener
    ) {
        this.context = context;
        this.selectedPosition = findPositionByEpisodeNumber(selectedEpisodeNumber, initialList);
        this.episodeListener = episodeListener;

        DiffUtil.ItemCallback<Episode> callback = new DiffUtil.ItemCallback<Episode>() {
            @Override
            public boolean areItemsTheSame(@NonNull Episode oldItem, @NonNull Episode newItem) {
                return oldItem.getEpisodeNumber() == newItem.getEpisodeNumber();
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull Episode oldItem, @NonNull Episode newItem) {
                return oldItem.getEpisodeNumber() == newItem.getEpisodeNumber()
                        && oldItem.getEpisodeTitle().equals(newItem.getEpisodeTitle());
            }
        };

        differ = new AsyncListDiffer<>(this, callback);
        differ.submitList(initialList);
    }

    private int findPositionByEpisodeNumber(int episodeNumber, List<Episode> list) {
        if (episodeNumber < 1) return -1; // no selection

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEpisodeNumber() == episodeNumber) {
                return i;
            }
        }
        return -1;
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

        Episode item = differ.getCurrentList().get(position);

        // image
        Glide.with(context)
                .load(item.getImages().get(0))
                .into(holder.binding.seasonImg);

        // text
        holder.binding.seasonTitleTv.setText(item.getEpisodeTitle());
        holder.binding.seasonTimingTv.setText(item.getRuntime());

        // highlight selected item
        if (selectedPosition == position) {
            holder.binding.playRl.setBackgroundResource(R.drawable.blueroundcircle_bg);
            holder.binding.playIv.setColorFilter(ContextCompat.getColor(context, R.color.white));
            holder.binding.itemCv.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
        } else {
            holder.binding.playRl.setBackgroundResource(R.drawable.dimcircle_bg);
            holder.binding.playIv.clearColorFilter();
            holder.binding.itemCv.setBackgroundColor(Color.TRANSPARENT);
        }

        // ONLY PLAY BUTTON
        holder.binding.playIv.setImageResource(android.R.drawable.ic_media_play);

        // play button selects the episode
        holder.binding.playRl.setOnClickListener(v -> setSelected(position));
        holder.binding.getRoot().setOnClickListener(v -> setSelected(position));
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

    public void updateList(List<Episode> newList, int selectedEpisodeNumber) {
        differ.submitList(newList);
        this.selectedPosition = findPositionByEpisodeNumber(selectedEpisodeNumber, newList);
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        final SeasonListItemsBinding binding;

        public ItemViewHolder(@NonNull SeasonListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

