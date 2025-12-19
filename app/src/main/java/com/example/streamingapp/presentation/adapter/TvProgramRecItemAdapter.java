package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvChannelUiItem;
import com.example.streamingapp.databinding.TvprogramListItemsBinding;

public class TvProgramRecItemAdapter extends RecyclerView.Adapter<TvProgramRecItemAdapter.ItemViewHolder> {

    private int selectedPosition = 0;
    private final OnProgramClickListener listener;

    public interface OnProgramClickListener {
        void onProgramClick(TvChannelUiItem item);
    }

    private final AsyncListDiffer<TvChannelUiItem> differ =
            new AsyncListDiffer<TvChannelUiItem>(this, DIFF_CALLBACK);

    private static final DiffUtil.ItemCallback<TvChannelUiItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TvChannelUiItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull TvChannelUiItem oldItem, @NonNull TvChannelUiItem newItem) {
                    return oldItem.getProgrammeName() == newItem.getProgrammeName(); // ensure ID exists
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull TvChannelUiItem oldItem, @NonNull TvChannelUiItem newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public TvProgramRecItemAdapter(OnProgramClickListener listener) {
        this.listener = listener;
    }

    public void submitList(java.util.List<TvChannelUiItem> list) {
        differ.submitList(list);
        if (!list.isEmpty()) selectedPosition = 0;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                TvprogramListItemsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(differ.getCurrentList().get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    // ------------------- ViewHolder --------------------------

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TvprogramListItemsBinding binding;

        ItemViewHolder(TvprogramListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TvChannelUiItem item, int position, OnProgramClickListener listener) {

            Glide.with(binding.getRoot()).load(item.getProgrammeUrl()).into(binding.programIv);
            binding.tvLogoName.setText(item.getChannelName());
            binding.tvProgramName.setText(item.getProgrammeName());

            // selection highlight logic
            if (position == selectedPosition) {
                binding.tvProgramCv.setBackgroundResource(R.drawable.lgblackbluestroke_bg);
            } else {
                binding.tvProgramCv.setBackgroundResource(R.drawable.lgtransparent_bg);
            }

            binding.getRoot().setOnClickListener(v -> {
                updateSelection(position);
                listener.onProgramClick(item);
            });
        }
    }

    // ------------------- Selection Logic --------------------------

    private void updateSelection(int newPosition) {
        int oldPosition = selectedPosition;
        selectedPosition = newPosition;

        notifyItemChanged(oldPosition);
        notifyItemChanged(newPosition);
    }
}
