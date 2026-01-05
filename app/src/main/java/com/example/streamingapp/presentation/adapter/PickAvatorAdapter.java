package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.databinding.PickAvatorListItemBinding;

import java.util.List;

public class PickAvatorAdapter extends RecyclerView.Adapter<PickAvatorAdapter.ItemViewHolder> {

    private final AsyncListDiffer<PickItem> differ;
    private final Context context;
    private final OnSelectionChangeListener selectionChangeListener;

    // Single selection
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnSelectionChangeListener {
        void onSelectionChanged(PickItem selectedItem);
    }

    public PickAvatorAdapter(Context context,
                             List<PickItem> itemList,
                             OnSelectionChangeListener listener) {
        this.context = context;
        this.selectionChangeListener = listener;

        DiffUtil.ItemCallback<PickItem> diffCallback = new DiffUtil.ItemCallback<PickItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull PickItem oldItem, @NonNull PickItem newItem) {
                return oldItem.getItemTitle().equals(newItem.getItemTitle());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull PickItem oldItem, @NonNull PickItem newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
        differ.submitList(itemList);

        // Load previously saved PickItem and find its position
        PickItem savedItem = LocalManager.loadAvatar();
        if (savedItem != null) {
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).getItemTitle().equals(savedItem.getItemTitle())) {
                    selectedPosition = i;
                    break;
                }
            }
        }

    }

    public void submitList(List<PickItem> list) {
        differ.submitList(list);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PickAvatorListItemBinding binding = PickAvatorListItemBinding
                .inflate(LayoutInflater.from(context), parent, false);
        return new ItemViewHolder(binding);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PickItem item = differ.getCurrentList().get(position);

        Glide.with(context).load(item.getItemImg()).into(holder.binding.ivImage);

        holder.binding.tvTitle.setVisibility(View.GONE);

        boolean isSelected = position == selectedPosition;

        if (isSelected) {
            holder.binding.cvRoot.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.lgtransparentbluestroke_bg)
            );
            holder.binding.selectIV.setVisibility(View.VISIBLE);
            holder.binding.selectIV.setColorFilter(
                    ContextCompat.getColor(context, R.color.bluemain)
            );
            holder.binding.cvRoot.setStrokeColor(android.R.color.transparent);
        } else {
            holder.binding.cvRoot.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.lgtransparent_bg)
            );
            holder.binding.selectIV.setVisibility(View.INVISIBLE);
            holder.binding.selectIV.setColorFilter(Color.TRANSPARENT);
            holder.binding.cvRoot.setStrokeColor(R.color.lgblackmain);
        }

        holder.binding.cvRoot.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Update UI efficiently
            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(selectedPosition);

            // Persist entire item (single selection)
            LocalManager.saveAvatar(item);

            if (selectionChangeListener != null) {
                selectionChangeListener.onSelectionChanged(item);
            }
        });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    @Nullable
    public PickItem getSelectedItem() {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return null;
        }

        List<PickItem> list = differ.getCurrentList();
        if (selectedPosition >= 0 && selectedPosition < list.size()) {
            return list.get(selectedPosition);
        }

        return null;
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final PickAvatorListItemBinding binding;

        public ItemViewHolder(@NonNull PickAvatorListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
