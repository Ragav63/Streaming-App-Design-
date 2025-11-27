package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.databinding.TvnameListItemsBinding;
import com.example.streamingapp.presentation.view.TvProgramFragment;

import java.util.List;

public class TvSelectionRecItemAdapter
        extends RecyclerView.Adapter<TvSelectionRecItemAdapter.ItemViewHolder> {

    // Functional Interfaces
    @FunctionalInterface
    public interface ItemClick {
        void onClick(TvItems item);
    }

    @FunctionalInterface
    public interface FavClick {
        void onToggle(TvItems item);
    }

    private final ItemClick itemClick;
    private final FavClick favClick;

    public TvSelectionRecItemAdapter(ItemClick itemClick, FavClick favClick) {
        this.itemClick = itemClick;
        this.favClick = favClick;
    }

    private final DiffUtil.ItemCallback<TvItems> DIFF = new DiffUtil.ItemCallback<TvItems>() {
        @Override
        public boolean areItemsTheSame(@NonNull TvItems oldItem, @NonNull TvItems newItem) {
            return oldItem.getTvName().equals(newItem.getTvName());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull TvItems oldItem, @NonNull TvItems newItem) {
            return oldItem.equals(newItem);
        }
    };

    public final AsyncListDiffer<TvItems> differ = new AsyncListDiffer<>(this, DIFF);

    public void submitList(java.util.List<TvItems> list) {
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
        TvItems item = differ.getCurrentList().get(position);

        holder.binding.tvLogoNameTv.setText(item.getTvLogoName());
        holder.binding.tvNameTv.setText(item.getTvName());
        holder.binding.currentProgramNameTv.setText(item.getCurrentProgramName());

        holder.binding.favIv.setColorFilter(
                holder.itemView.getContext().getColor(
                        item.isFavorite() ? R.color.bluemain : R.color.white
                )
        );

        holder.binding.favIv.setOnClickListener(v -> favClick.onToggle(item));

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
