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
import com.example.streamingapp.data.model.TvChannel;
import com.example.streamingapp.data.model.TvChannelUiItem;
import com.example.streamingapp.databinding.TvnameListItemsBinding;

public class TvSelectionRecItemAdapter
        extends RecyclerView.Adapter<TvSelectionRecItemAdapter.ItemViewHolder> {

    private final Context context;

    // Functional Interfaces
    @FunctionalInterface
    public interface ItemClick {
        void onClick(TvChannelUiItem item);
    }


    private final ItemClick itemClick;

    public TvSelectionRecItemAdapter(Context context, ItemClick itemClick) {
        this.context = context;
        this.itemClick = itemClick;
    }

    private final DiffUtil.ItemCallback<TvChannelUiItem> DIFF = new DiffUtil.ItemCallback<TvChannelUiItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull TvChannelUiItem oldItem, @NonNull TvChannelUiItem newItem) {
            return oldItem.getProgrammeName().equals(newItem.getProgrammeName());
        }


        @Override
        public boolean areContentsTheSame(@NonNull TvChannelUiItem oldItem, @NonNull TvChannelUiItem newItem) {
            return oldItem.getProgrammeTiming().equals(newItem.getProgrammeTiming())
                    && oldItem.getProgrammeName().equals(newItem.getProgrammeName());        }
    };

    public final AsyncListDiffer<TvChannelUiItem> differ = new AsyncListDiffer<>(this, DIFF);

    public void submitList(java.util.List<TvChannelUiItem> list) {
        differ.submitList(list);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                TvnameListItemsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TvChannelUiItem item = differ.getCurrentList().get(position);

        holder.binding.tvLogoNameTv.setText(item.getChannelName());
        holder.binding.tvNameTv.setText(item.getChannelName());
        holder.binding.currentProgramNameTv.setText(item.getProgrammeName());

        Glide.with(context).load(item.getChannelLogo()).into(holder.binding.ivImage);

        holder.itemView.setOnClickListener(v -> itemClick.onClick(item));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TvnameListItemsBinding binding;

        public ItemViewHolder(@NonNull TvnameListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
