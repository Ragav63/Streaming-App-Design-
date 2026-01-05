package com.example.streamingapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.ContentType;
import com.example.streamingapp.data.model.FavouriteItem;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.FavouriteListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class FavouriteAdapter
        extends RecyclerView.Adapter<FavouriteAdapter.FavVH> {

    public interface OnItemClick {
        void onItemClick(FavouriteItem item);
    }

    public interface OnRemoveClick {
        void onRemoveClick(FavouriteItem item);
    }
    private final List<FavouriteItem> items = new ArrayList<>();
    private final OnItemClick itemClick;
    private final OnRemoveClick removeClick;

    public FavouriteAdapter(OnItemClick itemClick, OnRemoveClick removeClick) {
        this.itemClick = itemClick;
        this.removeClick = removeClick;
    }

    public void submitList(List<FavouriteItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged(); // simple & safe
    }

    @NonNull
    @Override
    public FavVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavouriteListItemBinding binding =
                FavouriteListItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );
        return new FavVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavVH holder, int position) {
        FavouriteItem item = items.get(position);

        if (item.type == ContentType.MOVIE) {
            MovieItems movie = (MovieItems) item.data;

            holder.binding.tvTitle.setText(movie.getTitle());
            Glide.with(holder.itemView)
                    .load(movie.getPoster())
                    .into(holder.binding.ivImage);

        } else {
            SeriesItems series = (SeriesItems) item.data;

            holder.binding.tvTitle.setText(series.getTitle());
            Glide.with(holder.itemView)
                    .load(series.getPoster())
                    .into(holder.binding.ivImage);
        }

        holder.itemView.setOnClickListener(v -> itemClick.onItemClick(item));
        // 🔥 Favourite icon click → remove
        holder.binding.ivFavourite.setOnClickListener(v ->
                removeClick.onRemoveClick(item)
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FavVH extends RecyclerView.ViewHolder {
        FavouriteListItemBinding binding;

        FavVH(FavouriteListItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}

