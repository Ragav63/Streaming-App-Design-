package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.model.ContinueWatchingItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.ContinueWatchingHomeListItemsBinding;
import com.example.streamingapp.domain.repository.ContinueWatchingItemClick;

import java.util.List;

public class ContinueWatchingItemAdapter extends RecyclerView.Adapter<ContinueWatchingItemAdapter.ItemViewHolder> {



    public final AsyncListDiffer<ContinueWatchingItems> differ;
    private final ContinueWatchingItemClick onItemClick;

    public ContinueWatchingItemAdapter(ContinueWatchingItemClick onItemClick) {
        this.onItemClick = onItemClick;

        DiffUtil.ItemCallback<ContinueWatchingItems> diffCallback = new DiffUtil.ItemCallback<ContinueWatchingItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull ContinueWatchingItems oldItem, @NonNull ContinueWatchingItems newItem) {
                return oldItem.getConWatchTitle().equals(newItem.getConWatchTitle());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull ContinueWatchingItems oldItem, @NonNull ContinueWatchingItems newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<ContinueWatchingItems> list) {
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
        ContinueWatchingItems item = differ.getCurrentList().get(position);

        holder.binding.conWatchTitleTv.setText(item.getConWatchTitle());
        holder.binding.conWatchDescTv.setText(item.getConWatchDesc());
        holder.binding.conWatchIv.setImageResource(item.getConWatchImg());

        setProgressBar(holder.binding.conWatchPbar, 25); // demo progress

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

    private void setProgressBar(ProgressBar progressBar, int progress) {
        progressBar.setProgress(progress);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ContinueWatchingHomeListItemsBinding binding;

        public ItemViewHolder(@NonNull ContinueWatchingHomeListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}

