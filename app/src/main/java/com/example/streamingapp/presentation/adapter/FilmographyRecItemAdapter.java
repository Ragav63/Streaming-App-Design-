package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.databinding.FilmographyListItemsBinding;
import com.example.streamingapp.domain.repository.OnFilmographyClick;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;

import java.util.ArrayList;
import java.util.List;

public class FilmographyRecItemAdapter<T extends Parcelable> extends RecyclerView.Adapter<FilmographyRecItemAdapter.ItemViewHolder> {

    private final AsyncListDiffer<T> differ;
    private final OnFilmographyClick<T> onFilmographyClick;

    public FilmographyRecItemAdapter(OnFilmographyClick<T> onFilmographyClick) {
        this.onFilmographyClick = onFilmographyClick;

        DiffUtil.ItemCallback<T> diffCallback = new DiffUtil.ItemCallback<T>() {
            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                if (oldItem instanceof MovieItems && newItem instanceof MovieItems) {
                    return ((MovieItems) oldItem).getTitle().equals(((MovieItems) newItem).getTitle());
                } else if (oldItem instanceof SeriesItems && newItem instanceof SeriesItems) {
                    return ((SeriesItems) oldItem).getTitle().equals(((SeriesItems) newItem).getTitle());
                }
                return false;
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<? extends T> list) {
        differ.submitList(new ArrayList<>(list));
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FilmographyListItemsBinding binding = FilmographyListItemsBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        T item = differ.getCurrentList().get(position);

        // Set data for MovieItems or SeriesItems
        if (item instanceof MovieItems) {
            MovieItems movieItem = (MovieItems) item;
            holder.binding.itemTitle.setText(movieItem.getTitle());
            holder.binding.itemRating.setText(movieItem.getImdbRating());
            holder.binding.itemIv.setImageResource(movieItem.getImage());
        } else if (item instanceof SeriesItems) {
            SeriesItems seriesItem = (SeriesItems) item;
            holder.binding.itemTitle.setText(seriesItem.getTitle());
            holder.binding.itemRating.setText(seriesItem.getImdbRating());
            holder.binding.itemIv.setImageResource(seriesItem.getImage());
        }

        // Delegate click handling to fragment/activity
        holder.binding.getRoot().setOnClickListener(v -> {
            if (onFilmographyClick != null) onFilmographyClick.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final FilmographyListItemsBinding binding;

        public ItemViewHolder(@NonNull FilmographyListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
