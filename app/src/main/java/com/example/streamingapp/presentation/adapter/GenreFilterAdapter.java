package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenreFilterAdapter
        extends RecyclerView.Adapter<GenreFilterAdapter.ItemViewHolder> {

    public interface OnSelectionChangeListener {
        void onSelectionChanged(Set<String> selectedTitles);
    }

    private final Context context;
    private final boolean isAssignOnly;
    private final OnSelectionChangeListener listener;

    // ✅ Identity-based selection (CORRECT)
    private final Set<String> selectedTitles = new HashSet<>();

    private final AsyncListDiffer<PickItem> differ =
            new AsyncListDiffer<>(this, DIFF_CALLBACK);

    public GenreFilterAdapter(
            Context context,
            boolean isAssignOnly,
            OnSelectionChangeListener listener
    ) {
        this.context = context;
        this.isAssignOnly = isAssignOnly;
        this.listener = listener;
    }

    // -------------------- PUBLIC API --------------------

    public void submitList(List<PickItem> list) {
        differ.submitList(list);
    }

    public void clearSelection() {
        if (isAssignOnly || selectedTitles.isEmpty()) return;

        selectedTitles.clear();
        notifyItemRangeChanged(0, getItemCount());
        dispatchSelection();
    }

    public void setSelectedTitles(Set<String> titles) {
        if (isAssignOnly) return;

        selectedTitles.clear();
        if (titles != null) {
            selectedTitles.addAll(titles);
        }

        notifyItemRangeChanged(0, getItemCount());
        dispatchSelection();
    }

    public Set<String> getSelectedTitles() {
        return new HashSet<>(selectedTitles);
    }

    // -------------------- ADAPTER --------------------

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        GenreFilterItemBinding binding =
                GenreFilterItemBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                );
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ItemViewHolder holder,
            int position
    ) {
        PickItem item = differ.getCurrentList().get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    // -------------------- VIEW HOLDER --------------------

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final GenreFilterItemBinding binding;

        ItemViewHolder(@NonNull GenreFilterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PickItem item) {
            binding.tvTitle.setText(item.getItemTitle());

            if (isAssignOnly) {
                bindAssignOnly();
                return;
            }

            boolean isSelected = selectedTitles.contains(item.getItemTitle());
            bindSelectable(item, isSelected);
        }

        private void bindAssignOnly() {
            binding.clRoot.setBackground(
                    ContextCompat.getDrawable(
                            context,
                            R.drawable.lgtransparentbluestroke_bg
                    )
            );

            binding.tvTitle.setTypeface(null, Typeface.BOLD);
            binding.tvTitle.setTextColor(
                    ContextCompat.getColor(context, R.color.bluemain)
            );

            binding.clRoot.setOnClickListener(null);
            binding.clRoot.setClickable(false);
        }

        private void bindSelectable(PickItem item, boolean isSelected) {
            binding.clRoot.setBackground(
                    ContextCompat.getDrawable(
                            context,
                            isSelected
                                    ? R.drawable.blue_bg
                                    : R.drawable.lgblackcircle_bg
                    )
            );

            binding.tvTitle.setTypeface(null, Typeface.NORMAL);
            binding.tvTitle.setTextColor(
                    ContextCompat.getColor(context, R.color.white)
            );

            binding.clRoot.setOnClickListener(v -> toggleSelection(item));
        }

        private void toggleSelection(PickItem item) {
            String key = item.getItemTitle();

            if (selectedTitles.contains(key)) {
                selectedTitles.remove(key);
            } else {
                selectedTitles.add(key);
            }

            int pos = getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                notifyItemChanged(pos);
            }
            dispatchSelection();
        }
    }

    // -------------------- HELPERS --------------------

    private void dispatchSelection() {
        if (listener != null) {
            listener.onSelectionChanged(new HashSet<>(selectedTitles));
        }
    }

    private static final DiffUtil.ItemCallback<PickItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PickItem>() {

                @Override
                public boolean areItemsTheSame(
                        @NonNull PickItem oldItem,
                        @NonNull PickItem newItem
                ) {
                    return oldItem.getItemTitle()
                            .equals(newItem.getItemTitle());
                }

                @Override
                public boolean areContentsTheSame(@NonNull PickItem oldItem, @NonNull PickItem newItem) {
                    return oldItem.getItemTitle().equals(newItem.getItemTitle())
                            && oldItem.getItemImg() == newItem.getItemImg();
                }
            };
}

