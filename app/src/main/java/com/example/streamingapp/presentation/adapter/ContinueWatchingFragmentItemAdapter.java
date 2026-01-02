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
import com.example.streamingapp.databinding.ContinueWatchingListItemsBinding;
import com.example.streamingapp.domain.repository.ActionType;
import com.example.streamingapp.domain.repository.ContinueWatchingItemClick;

import java.util.List;

public class ContinueWatchingFragmentItemAdapter extends RecyclerView.Adapter<ContinueWatchingFragmentItemAdapter.ItemViewHolder> {

    public final AsyncListDiffer<HistoryItems> differ;
    private final ContinueWatchingItemClick onItemClick;


    public ContinueWatchingFragmentItemAdapter(ContinueWatchingItemClick onItemClick) {
        this.onItemClick = onItemClick;

        DiffUtil.ItemCallback<HistoryItems> diffCallback = new DiffUtil.ItemCallback<HistoryItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull HistoryItems oldItem, @NonNull HistoryItems newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull HistoryItems oldItem, @NonNull HistoryItems newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContinueWatchingListItemsBinding binding = ContinueWatchingListItemsBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        HistoryItems item = differ.getCurrentList().get(position);

        holder.binding.conWatchTitleTv.setText(item.getTitle());
        holder.binding.conWatchDescTv.setText(item.getViewedAt());
        Glide.with(holder.binding.getRoot()).load(item.getImageUrl()).into(holder.binding.conWatchIv);

        setProgressBar(holder.binding.conWatchPbar, 25);

        holder.binding.playIv.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(item, ActionType.PLAY);
        });

        holder.binding.conWatchCancelIv.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(item, ActionType.REMOVE);
        });

    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public void submitList(List<HistoryItems> list) {
        differ.submitList(list);
    }

    private void setProgressBar(ProgressBar progressBar, int progress) {
        progressBar.setProgress(progress);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ContinueWatchingListItemsBinding binding;

        public ItemViewHolder(@NonNull ContinueWatchingListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }



}
