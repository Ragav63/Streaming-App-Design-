package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
                    return oldItem.getTrailerName().equals(newItem.getTrailerName());   // Make sure your model has unique id
                }


                @Override
                public boolean areContentsTheSame(@NonNull TrailerItems oldItem, @NonNull TrailerItems newItem) {
                    return oldItem.getTrailerName().equals(newItem.getTrailerName());
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

            String videoId = extractVideoId(item.getUrl());
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";

            Glide.with(binding.getRoot().getContext())
                    .load(thumbnailUrl)
                    .centerCrop() // forces Glide to fill the view
                    .into(binding.trailersView);


            binding.trailerTitleTv.setText(item.getTrailerName());
            binding.trailerTimingTv.setText(item.getDuration());

            binding.itemll.setOnClickListener(v -> listener.onTrailerClick(item));
        }

        private String extractVideoId(String url) {
            Uri uri = Uri.parse(url);
            return uri.getQueryParameter("v");
        }


    }
}