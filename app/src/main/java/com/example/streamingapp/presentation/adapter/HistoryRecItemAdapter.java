package com.example.streamingapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.databinding.HistoryListItemsBinding;
import com.example.streamingapp.domain.repository.OnHistoryClick;

public class HistoryRecItemAdapter
        extends RecyclerView.Adapter<HistoryRecItemAdapter.ItemViewHolder> {

    private final OnHistoryClick clickListener;

    public HistoryRecItemAdapter(OnHistoryClick clickListener) {
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<HistoryItems> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HistoryItems>() {
                @Override
                public boolean areItemsTheSame(@NonNull HistoryItems oldItem,
                                               @NonNull HistoryItems newItem) {
                    return oldItem.getHistoryImg() == newItem.getHistoryImg();
                }

                @Override
                public boolean areContentsTheSame(@NonNull HistoryItems oldItem,
                                                  @NonNull HistoryItems newItem) {
                    return oldItem.getHistoryImg() == newItem.getHistoryImg()
                            && oldItem.getHistoryRating().equals(newItem.getHistoryRating())
                            && oldItem.getHistoryTiming().equals(newItem.getHistoryTiming());
                }
            };

    public final AsyncListDiffer<HistoryItems> differ =
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
        HistoryItems item = differ.getCurrentList().get(position);
        holder.bind(item, clickListener);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final HistoryListItemsBinding binding;

        public ItemViewHolder(@NonNull HistoryListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HistoryItems item, OnHistoryClick listener) {
            binding.itemIv.setImageResource(item.getHistoryImg());
            binding.itemRating.setText(item.getHistoryRating());
            binding.itemTimingTv.setText(item.getHistoryTiming());

            binding.getRoot().setOnClickListener(v -> listener.onClick(item));
        }
    }
}
