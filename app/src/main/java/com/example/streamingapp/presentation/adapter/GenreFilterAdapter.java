package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.databinding.GenreFilterItemBinding;
import com.example.streamingapp.databinding.PickAvatorListItemBinding;

import java.util.List;
import java.util.Set;

public class GenreFilterAdapter extends RecyclerView.Adapter<GenreFilterAdapter.ItemViewHolder> {

    private final AsyncListDiffer<PickItem> differ;
    private final Set<Integer> selectedPositions;
    private final Context context;
    private final LocalManager prefsManager;
    private final OnSelectionChangeListener selectionChangeListener;
    private final boolean isAssignOnly;

    public interface OnSelectionChangeListener {
        void onSelectionChanged(Set<Integer> selectedPositions);
    }

    public GenreFilterAdapter(
            Context context,
            List<PickItem> itemList,
            boolean isAssignOnly,
            OnSelectionChangeListener listener
    ) {
        this.context = context;
        this.isAssignOnly = isAssignOnly;
        this.selectionChangeListener = listener;
        this.prefsManager = new LocalManager(context);

        // Load persisted selections ONLY when selectable
        this.selectedPositions = isAssignOnly
                ? new java.util.HashSet<>()
                : prefsManager.loadGenreSelection();

        DiffUtil.ItemCallback<PickItem> diffCallback = new DiffUtil.ItemCallback<PickItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull PickItem oldItem, @NonNull PickItem newItem) {
                return oldItem.getItemTitle().equals(newItem.getItemTitle());
            }

            @Override
            public boolean areContentsTheSame(@NonNull PickItem oldItem, @NonNull PickItem newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
        differ.submitList(itemList);
    }

    public void submitList(List<PickItem> list) {
        differ.submitList(list);
    }

    public void clearSelection() {
        if (isAssignOnly) return;
        selectedPositions.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GenreFilterItemBinding binding =
                GenreFilterItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PickItem item = differ.getCurrentList().get(position);

        holder.binding.tvTitle.setText(item.getItemTitle());

        if (isAssignOnly) {
            // 🔒 ASSIGN-ONLY MODE

            // Background
            holder.binding.clRoot.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.lgtransparentbluestroke_bg)
            );

            // Text styling
            holder.binding.tvTitle.setPadding(0, 0, 0, 0);
            holder.binding.tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.binding.tvTitle.setTextColor(
                    ContextCompat.getColor(context, R.color.bluemain)
            );

            // Disable interaction
            holder.binding.clRoot.setOnClickListener(null);
            holder.binding.clRoot.setClickable(false);
            holder.binding.clRoot.setFocusable(false);
            return;
        }

        // ✅ SELECTABLE MODE (RESET EVERYTHING)

        // Restore padding (must match XML values)
        int padding = dpToPx(2);
        holder.binding.tvTitle.setPadding(padding, padding, padding, padding);

        holder.binding.tvTitle.setTypeface(null, android.graphics.Typeface.NORMAL);
        holder.binding.tvTitle.setTextColor(
                ContextCompat.getColor(context, R.color.white)
        );

        if (selectedPositions.contains(position)) {
            holder.binding.clRoot.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.blue_bg)
            );
        } else {
            holder.binding.clRoot.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.lgblackcircle_bg)
            );
        }

        holder.binding.clRoot.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            if (selectedPositions.contains(adapterPosition)) {
                selectedPositions.remove(adapterPosition);
            } else {
                selectedPositions.add(adapterPosition);
            }

            notifyItemChanged(adapterPosition);

            if (selectionChangeListener != null) {
                selectionChangeListener.onSelectionChanged(selectedPositions);
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

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final GenreFilterItemBinding binding;

        public ItemViewHolder(@NonNull GenreFilterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

