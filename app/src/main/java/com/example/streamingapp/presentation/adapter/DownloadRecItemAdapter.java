package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.model.DownloadItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.DownloadListItemsBinding;
import com.example.streamingapp.domain.repository.ActionType;
import com.example.streamingapp.domain.repository.OnDownloadClick;

import java.util.List;

public class DownloadRecItemAdapter extends RecyclerView.Adapter<DownloadRecItemAdapter.ItemViewHolder> {

    public final AsyncListDiffer<DownloadItems> differ;
    private final OnDownloadClick onDownloadClick;

    public DownloadRecItemAdapter(OnDownloadClick onDownloadClick) {
        this.onDownloadClick = onDownloadClick;

        DiffUtil.ItemCallback<DownloadItems> diffCallback = new DiffUtil.ItemCallback<DownloadItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull DownloadItems oldItem, @NonNull DownloadItems newItem) {
                return oldItem.getDownloadTitle().equals(newItem.getDownloadTitle())
                        && oldItem.getDownloadEpTitle().equals(newItem.getDownloadEpTitle());
            }


            @Override
            public boolean areContentsTheSame(@NonNull DownloadItems oldItem, @NonNull DownloadItems newItem) {
                return oldItem.getDownloadTitle().equals(newItem.getDownloadTitle());
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<DownloadItems> list) {
        differ.submitList(list);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloadListItemsBinding binding = DownloadListItemsBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DownloadItems item = differ.getCurrentList().get(position);

        holder.binding.downloadIv.setImageResource(item.getDownloadImg());
        holder.binding.downloadTitleTv.setText(item.getDownloadTitle());
        holder.binding.downloadEpTitleTv.setText(item.getDownloadEpTitle());
        holder.binding.downloadDurationTv.setText(item.getDownloadDuration());

        // Play click
        holder.binding.playIv.setOnClickListener(v -> {
            if (onDownloadClick != null) onDownloadClick.onClick(item, ActionType.PLAY, holder.getAdapterPosition());
        });

        // Cancel/remove click
        holder.binding.cancelIv.setOnClickListener(v -> {
            if (onDownloadClick != null) onDownloadClick.onClick(item, ActionType.REMOVE, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final DownloadListItemsBinding binding;

        public ItemViewHolder(@NonNull DownloadListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
