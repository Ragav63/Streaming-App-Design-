package com.example.streamingapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.FavouriteListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class FavouriteAdapter
        extends RecyclerView.Adapter<FavouriteAdapter.FavVH> {

    public interface OnFavouriteClick {
        void onClick(Object item);
    }

    private final List<Object> items = new ArrayList<>();
    private final OnFavouriteClick listener;

    public FavouriteAdapter(OnFavouriteClick listener) {
        this.listener = listener;
    }

    public void submitList(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
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
        Object item = items.get(position);

        if (item instanceof MovieItems) {
            MovieItems movie = (MovieItems) item;
            holder.binding.tvTitle.setText(movie.getTitle());
            Glide.with(holder.itemView)
                    .load(movie.getPoster())
                    .into(holder.binding.ivImage);

        } else if (item instanceof SeriesItems) {
            SeriesItems series = (SeriesItems) item;
            holder.binding.tvTitle.setText(series.getTitle());
            Glide.with(holder.itemView)
                    .load(series.getPoster())
                    .into(holder.binding.ivImage);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
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
