package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.databinding.MoviesLandListItemsBinding;
import com.example.streamingapp.databinding.PopularMoviesListItemsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MovieLandItemAdapter extends RecyclerView.Adapter<MovieLandItemAdapter.MovieViewHolder> {

    private final Context context;

    // full list for filtering (never mutated)
    private final List<MovieItems> originalList = new ArrayList<>();

    // async differ for efficient UI updates
    public final AsyncListDiffer<MovieItems> differ;

    // --- optional click listener interface ---
    public interface OnMovieClickListener {
        void onMovieClick(MovieItems item, int position);
    }

    private final OnMovieClickListener clickListener;

    public MovieLandItemAdapter(Context context, List<MovieItems> items, OnMovieClickListener listener) {
        this.context = context;
        this.clickListener = listener;

        originalList.clear();
        if (items != null) originalList.addAll(items);

        DiffUtil.ItemCallback<MovieItems> diffCallback = new DiffUtil.ItemCallback<MovieItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull MovieItems oldItem, @NonNull MovieItems newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }


            @Override
            public boolean areContentsTheSame(@NonNull MovieItems oldItem, @NonNull MovieItems newItem) {
                return oldItem.getId() == newItem.getId()
                        && oldItem.getTitle().equals(newItem.getTitle());
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
        differ.submitList(new ArrayList<>(originalList));
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MoviesLandListItemsBinding binding =
                MoviesLandListItemsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        MovieItems item = differ.getCurrentList().get(position);

        Glide.with(context)
                .load(item.getPoster())
                .into(holder.binding.itemIv);

        holder.binding.itemRating.setText(item.getImdb_rating());
        holder.binding.itemTitle.setText(item.getTitle());

        holder.binding.itemCv.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onMovieClick(item, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public List<MovieItems> getCurrentList() {
        return differ.getCurrentList();
    }
    public void submitList(List<MovieItems> items) {
        originalList.clear();

        if (items != null) {
            originalList.addAll(items);
        }

        // IMPORTANT: new list instance
        differ.submitList(new ArrayList<>(originalList));
    }



    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final MoviesLandListItemsBinding binding;

        MovieViewHolder(@NonNull MoviesLandListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
