package com.example.streamingapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
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
    private final OnItemClick itemClick;
    private final OnRemoveClick removeClick;

    private static final DiffUtil.ItemCallback<FavouriteItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FavouriteItem>() {

                @Override
                public boolean areItemsTheSame(
                        @NonNull FavouriteItem oldItem,
                        @NonNull FavouriteItem newItem
                ) {
                    if (oldItem.type != newItem.type) return false;

                    if (oldItem.type == ContentType.MOVIE) {
                        return ((MovieItems) oldItem.data).getId()
                                == ((MovieItems) newItem.data).getId();
                    } else {
                        return ((SeriesItems) oldItem.data).getId()
                                == ((SeriesItems) newItem.data).getId();
                    }
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull FavouriteItem oldItem,
                        @NonNull FavouriteItem newItem
                ) {
                    return oldItem.equals(newItem);
                }
            };

    private final AsyncListDiffer<FavouriteItem> differ =
            new AsyncListDiffer<>(this, DIFF_CALLBACK);

    public FavouriteAdapter(OnItemClick itemClick, OnRemoveClick removeClick) {
        this.itemClick = itemClick;
        this.removeClick = removeClick;
    }

    public void submitList(List<FavouriteItem> newItems) {
        differ.submitList(newItems == null ? new ArrayList<>() : new ArrayList<>(newItems));
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
        FavouriteItem item = differ.getCurrentList().get(position);

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
        holder.binding.ivFavourite.setOnClickListener(v ->
                removeClick.onRemoveClick(item)
        );
    }


    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }


    static class FavVH extends RecyclerView.ViewHolder {
        FavouriteListItemBinding binding;

        FavVH(FavouriteListItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}

