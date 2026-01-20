package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.databinding.ContinueWatchingHomeListItemsBinding;
import com.example.streamingapp.domain.repository.ContinueWatchingItemClick;

import java.util.List;

public class ContinueWatchingItemAdapter extends RecyclerView.Adapter<ContinueWatchingItemAdapter.ItemViewHolder> {



    public final AsyncListDiffer<HistoryItems> differ;
    private final ContinueWatchingItemClick onItemClick;

    public ContinueWatchingItemAdapter(ContinueWatchingItemClick onItemClick) {
        this.onItemClick = onItemClick;

        DiffUtil.ItemCallback<HistoryItems> diffCallback = new DiffUtil.ItemCallback<HistoryItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull HistoryItems oldItem, @NonNull HistoryItems newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }


            @Override
            public boolean areContentsTheSame(@NonNull HistoryItems oldItem, @NonNull HistoryItems newItem) {
                return oldItem.getId() == newItem.getId()
                        && oldItem.getTitle().equals(newItem.getTitle());
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<HistoryItems> list) {
        differ.submitList(list);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContinueWatchingHomeListItemsBinding binding = ContinueWatchingHomeListItemsBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        HistoryItems item = differ.getCurrentList().get(position);

        holder.binding.conWatchTitleTv.setText(item.getTitle());
        holder.binding.conWatchDescTv.setText(item.getViewedAt());
        Glide.with(holder.binding.getRoot()).load(item.getImageUrl()).into(holder.binding.conWatchIv);
        long watchedMs = item.getWatchedMs();
        long totalMs = item.getDurationMs();

        if (totalMs > 0) {
            int progress = (int) ((watchedMs * 100f) / totalMs);
            holder.binding.conWatchPbar.setProgress(progress);
        } else {
            holder.binding.conWatchPbar.setProgress(0);
        }

//        // Delegate clicks to the fragment/activity
//        holder.binding.playIv.setOnClickListener(v -> {
//            if (onItemClick != null)
//                onItemClick.onClick(item, ContinueWatchingItemClick.ActionType.PLAY);
//        });
//
//        holder.binding.conWatchCancelIv.setOnClickListener(v -> {
//            if (onItemClick != null)
//                onItemClick.onClick(item, ContinueWatchingItemClick.ActionType.REMOVE);
//        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }



    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ContinueWatchingHomeListItemsBinding binding;

        public ItemViewHolder(@NonNull ContinueWatchingHomeListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}

