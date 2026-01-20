package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.databinding.TvprogramTimingListItemsBinding;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvProgramTimingItems;

import java.util.List;

public class TvProgramTimingRecItemAdapter
        extends RecyclerView.Adapter<TvProgramTimingRecItemAdapter.ItemViewHolder> {

    private int selectedPosition = 0;
    private final OnTimeSelectedListener listener;

    public interface OnTimeSelectedListener {
        void onTimeSelected(String timing);
    }

    private final AsyncListDiffer<TvProgramTimingItems> differ =
            new AsyncListDiffer<>(this, DIFF_CALLBACK);

    private static final DiffUtil.ItemCallback<TvProgramTimingItems> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TvProgramTimingItems>() {
                @Override
                public boolean areItemsTheSame(@NonNull TvProgramTimingItems oldItem,
                                               @NonNull TvProgramTimingItems newItem) {
                    return oldItem.getTiming().equals(newItem.getTiming()); // ensure model has ID
                }


                @Override
                public boolean areContentsTheSame(@NonNull TvProgramTimingItems oldItem,
                                                  @NonNull TvProgramTimingItems newItem) {
                    return oldItem.getTiming().equals(newItem.getTiming());
                }
            };

    public TvProgramTimingRecItemAdapter(OnTimeSelectedListener listener) {
        this.listener = listener;
    }

    public void submitList(java.util.List<TvProgramTimingItems> list) {
        differ.submitList(list);
        if (!list.isEmpty()) selectedPosition = 0;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                TvprogramTimingListItemsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(differ.getCurrentList().get(position), position);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    // ------------------------------------------------------

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TvprogramTimingListItemsBinding binding;

        ItemViewHolder(TvprogramTimingListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TvProgramTimingItems item, int position) {

            binding.timingTv.setText(item.getTiming());

            if (position == selectedPosition) {
                binding.itemlRl.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            } else {
                binding.itemlRl.setBackgroundResource(R.drawable.lgtransparentgreystroke_bg);
            }

            binding.getRoot().setOnClickListener(v -> {
                updateSelection(position);
                listener.onTimeSelected(item.getTiming());
            });
        }
    }

    // ------------------------------------------------------

    private void updateSelection(int newPos) {
        int oldPos = selectedPosition;
        selectedPosition = newPos;

        notifyItemChanged(oldPos);
        notifyItemChanged(newPos);
    }
}
