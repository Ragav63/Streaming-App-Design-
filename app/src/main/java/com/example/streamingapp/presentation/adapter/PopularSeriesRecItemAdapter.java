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
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.PopularSeriesListItemsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PopularSeriesRecItemAdapter
        extends RecyclerView.Adapter<PopularSeriesRecItemAdapter.ItemViewHolder> {

    private final Context context;

    // full immutable list for filtering
    private final List<SeriesItems> originalList = new ArrayList<>();

    // differ for efficient updates
    public final AsyncListDiffer<SeriesItems> differ;

    // functional interface for click callback
    public interface OnSeriesClickListener {
        void onSeriesClick(SeriesItems item, int position);
    }

    private final OnSeriesClickListener clickListener;

    public PopularSeriesRecItemAdapter(
            Context context,
            List<SeriesItems> initialItems,
            OnSeriesClickListener listener
    ) {
        this.context = context;
        this.clickListener = listener;

        if (initialItems != null) originalList.addAll(initialItems);

        DiffUtil.ItemCallback<SeriesItems> diffCallback = new DiffUtil.ItemCallback<SeriesItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull SeriesItems oldItem, @NonNull SeriesItems newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull SeriesItems oldItem, @NonNull SeriesItems newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);

        // initial list
        differ.submitList(new ArrayList<>(originalList));
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PopularSeriesListItemsBinding binding =
                PopularSeriesListItemsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        SeriesItems item = differ.getCurrentList().get(position);

        Glide.with(context)
                .load(item.getPoster())
                .into(holder.binding.itemIv);

        holder.binding.itemTitle.setText(item.getTitle());
        holder.binding.itemRating.setText(item.getImdb_rating());

        holder.binding.itemCv.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSeriesClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public List<SeriesItems> getCurrentList() {
        return differ.getCurrentList();
    }

    public void submitList(List<SeriesItems> items) {
        originalList.clear();

        if (items != null) {
            originalList.addAll(items);
        }

        // IMPORTANT: new list instance
        differ.submitList(new ArrayList<>(originalList));
    }


    // --------------------------------------------------------------------
    //  FILTERING WITHOUT MUTATING THE ORIGINAL LIST
    // --------------------------------------------------------------------
    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            differ.submitList(new ArrayList<>(originalList));
            return;
        }

        String filter = query.toLowerCase(Locale.ROOT).trim();
        List<SeriesItems> filtered = new ArrayList<>();

        for (SeriesItems item : originalList) {
            boolean matches =
                    item.getTitle().toLowerCase(Locale.ROOT).contains(filter) ||
                            item.getImdb_rating().toLowerCase(Locale.ROOT).contains(filter) ||
                            item.getYear().toLowerCase(Locale.ROOT).contains(filter) ||
                            item.getGenres().stream().anyMatch(g -> g.toLowerCase(Locale.ROOT).contains(filter))||
                            item.getCountry().toLowerCase(Locale.ROOT).contains(filter) ||
                            item.getSeasons()
                                    .stream()
                                    .flatMap(season -> season.getEpisodes().stream())
                                    .anyMatch(ep -> ep.getEpisodeTitle().toLowerCase(Locale.ROOT).contains(filter)) ||
                            item.getPlot().toLowerCase(Locale.ROOT).contains(filter);

            if (matches) filtered.add(item);
        }

        differ.submitList(filtered);
    }

    public boolean isDataEmpty() {
        return differ.getCurrentList().isEmpty();
    }

    // ViewHolder
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final PopularSeriesListItemsBinding binding;

        public ItemViewHolder(@NonNull PopularSeriesListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
