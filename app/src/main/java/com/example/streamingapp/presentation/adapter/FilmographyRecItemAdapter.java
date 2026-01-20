package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.databinding.FilmographyListItemsBinding;
import com.example.streamingapp.domain.repository.OnFilmographyClick;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilmographyRecItemAdapter<T extends Parcelable> extends RecyclerView.Adapter<FilmographyRecItemAdapter.ItemViewHolder> {

    private final AsyncListDiffer<T> differ;
    private final OnFilmographyClick<T> onFilmographyClick;
    private final Context context;


    public FilmographyRecItemAdapter(Context context,OnFilmographyClick<T> onFilmographyClick) {
        this.context = context;
        this.onFilmographyClick = onFilmographyClick;


        DiffUtil.ItemCallback<T> diffCallback = new DiffUtil.ItemCallback<T>() {
            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                if (oldItem instanceof MovieItems && newItem instanceof MovieItems) {
                    return ((MovieItems) oldItem).getId() == ((MovieItems) newItem).getId();
                } else if (oldItem instanceof SeriesItems && newItem instanceof SeriesItems) {
                    return ((SeriesItems) oldItem).getId() == ((SeriesItems) newItem).getId();
                }
                return false;
            }



            @Override
            public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {

                if (oldItem instanceof MovieItems && newItem instanceof MovieItems) {
                    MovieItems oldM = (MovieItems) oldItem;
                    MovieItems newM = (MovieItems) newItem;

                    return Objects.equals(oldM.getTitle(), newM.getTitle())
                            && Objects.equals(oldM.getPoster(), newM.getPoster())
                            && Objects.equals(oldM.getImdb_rating(), newM.getImdb_rating());
                }

                if (oldItem instanceof SeriesItems && newItem instanceof SeriesItems) {
                    SeriesItems oldS = (SeriesItems) oldItem;
                    SeriesItems newS = (SeriesItems) newItem;

                    return Objects.equals(oldS.getTitle(), newS.getTitle())
                            && Objects.equals(oldS.getPoster(), newS.getPoster())
                            && Objects.equals(oldS.getImdb_rating(), newS.getImdb_rating());
                }

                return false;
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
            holder.binding.itemRating.setText(movieItem.getImdb_rating());
            Glide.with(context).load(movieItem.getPoster()).into(holder.binding.itemIv);
        } else if (item instanceof SeriesItems) {
            SeriesItems seriesItem = (SeriesItems) item;
            holder.binding.itemTitle.setText(seriesItem.getTitle());
            holder.binding.itemRating.setText(seriesItem.getImdb_rating());
            Glide.with(context).load(seriesItem.getPoster()).into(holder.binding.itemIv);
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

    public List<T> getCurrentList() {
        return differ.getCurrentList();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final FilmographyListItemsBinding binding;

        public ItemViewHolder(@NonNull FilmographyListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
