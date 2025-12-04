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
import com.example.streamingapp.databinding.PopularMoviesListItemsBinding;
import com.example.streamingapp.data.model.MovieItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PopularMovieRecItemAdapter extends RecyclerView.Adapter<PopularMovieRecItemAdapter.MovieViewHolder> {

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

    public PopularMovieRecItemAdapter(Context context, List<MovieItems> items, OnMovieClickListener listener) {
        this.context = context;
        this.clickListener = listener;

        originalList.clear();
        if (items != null) originalList.addAll(items);

        DiffUtil.ItemCallback<MovieItems> diffCallback = new DiffUtil.ItemCallback<MovieItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull MovieItems oldItem, @NonNull MovieItems newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull MovieItems oldItem, @NonNull MovieItems newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
        differ.submitList(new ArrayList<>(originalList));
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PopularMoviesListItemsBinding binding =
                PopularMoviesListItemsBinding.inflate(
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

        holder.binding.itemRating.setText(item.getImdbRating());
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


    // -----------------------------------------------------------------------
    //  ASYNC FILTERING – does NOT mutate original list
    // -----------------------------------------------------------------------
    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            differ.submitList(new ArrayList<>(originalList));
            return;
        }

        String filter = query.toLowerCase(Locale.ROOT).trim();

        List<MovieItems> filtered = new ArrayList<>();
        for (MovieItems item : originalList) {
            boolean matches =
                    item.getTitle().toLowerCase(Locale.ROOT).contains(filter) ||
                            item.getImdbRating().toLowerCase(Locale.ROOT).contains(filter) ||
                            item.getYear().toLowerCase(Locale.ROOT).contains(filter) ||
                            item.getGenres().stream()
                                    .anyMatch(g -> g.toLowerCase(Locale.ROOT).contains(filter))||
                            item.getCountry().toLowerCase(Locale.ROOT).contains(filter) ||
                            item.getFormattedDuration().toLowerCase(Locale.ROOT).contains(filter) ||
                            item.getPlot().toLowerCase(Locale.ROOT).contains(filter);

            if (matches) filtered.add(item);
        }

        differ.submitList(filtered);
    }

    public boolean isDataEmpty() {
        return differ.getCurrentList().isEmpty();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final PopularMoviesListItemsBinding binding;

        MovieViewHolder(@NonNull PopularMoviesListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
