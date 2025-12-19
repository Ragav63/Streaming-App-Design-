package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.HistoryUiItem;
import com.example.streamingapp.databinding.HistoryListItemsBinding;
import com.example.streamingapp.domain.repository.OnPhotoClick;

import java.util.ArrayList;
import java.util.List;

public class HistoryRecItemAdapter
        extends RecyclerView.Adapter<HistoryRecItemAdapter.ItemViewHolder> {

    private Context context;
    private final OnPhotoClick clickListener;

    public HistoryRecItemAdapter(Context context,OnPhotoClick clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<HistoryUiItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HistoryUiItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull HistoryUiItem oldItem,
                                               @NonNull HistoryUiItem newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull HistoryUiItem oldItem,
                                                  @NonNull HistoryUiItem newItem) {
                    return oldItem.getId() == newItem.getId()
                            && oldItem.getRating().equals(newItem.getRating())
                            && oldItem.getTiming().equals(newItem.getTiming());
                }
            };

    public final AsyncListDiffer<HistoryUiItem> differ =
            new AsyncListDiffer<>(this, DIFF_CALLBACK);

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HistoryListItemsBinding binding = HistoryListItemsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        HistoryUiItem item = differ.getCurrentList().get(position);
        holder.bind(context,item, clickListener);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public void submitList(List<HistoryUiItem> items) {
        differ.submitList(items);
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final HistoryListItemsBinding binding;

        public ItemViewHolder(@NonNull HistoryListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Context context, HistoryUiItem item, OnPhotoClick listener) {

            // normal UI binding
            Glide.with(context).load(item.getPosterUrl()).into(binding.itemIv);
            binding.itemRating.setText(item.getRating());
            binding.itemTimingTv.setText(item.getTiming());

            binding.getRoot().setOnClickListener(v ->
                    listener.onClick( item.getPosterUrl())
            );
        }
    }
}
