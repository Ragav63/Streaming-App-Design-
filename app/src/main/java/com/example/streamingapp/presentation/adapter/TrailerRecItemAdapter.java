package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.databinding.TrailersListItemsBinding;

public class TrailerRecItemAdapter extends RecyclerView.Adapter<TrailerRecItemAdapter.ItemViewHolder> {

    private final OnTrailerClickListener listener;

    public interface OnTrailerClickListener {
        void onTrailerClick(TrailerItems item);
    }

    private final AsyncListDiffer<TrailerItems> differ = new AsyncListDiffer<>(this, DIFF_CALLBACK);

    private static final DiffUtil.ItemCallback<TrailerItems> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TrailerItems>() {
                @Override
                public boolean areItemsTheSame(@NonNull TrailerItems oldItem, @NonNull TrailerItems newItem) {
                    return oldItem.getTrailerTitle() == newItem.getTrailerTitle();   // Make sure your model has unique id
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull TrailerItems oldItem, @NonNull TrailerItems newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public TrailerRecItemAdapter(OnTrailerClickListener listener) {
        this.listener = listener;
    }

    public void submitList(java.util.List<TrailerItems> list) {
        differ.submitList(list);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                TrailersListItemsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TrailerItems item = differ.getCurrentList().get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TrailersListItemsBinding binding;

        ItemViewHolder(TrailersListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TrailerItems item, OnTrailerClickListener listener) {

            String videoId = extractVideoId(item.getTrailerUrl());

            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId;

            // Load thumbnail
            Glide.with(binding.getRoot().getContext())
                    .load(thumbnailUrl)
                    .into(binding.trailersView);

            binding.trailerTitleTv.setText(item.getTrailerTitle());
            binding.trailerTimingTv.setText(item.getTrailerTiming());

            binding.itemll.setOnClickListener(v -> listener.onTrailerClick(item));
        }

        private String extractVideoId(String url) {
            return url.replace("https://www.youtube.com/watch?v=", "")
                    .replace("&pp", "");
        }

    }
}