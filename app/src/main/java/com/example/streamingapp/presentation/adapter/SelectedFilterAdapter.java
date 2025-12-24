package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.databinding.SelectedFilterItemBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SelectedFilterAdapter
        extends RecyclerView.Adapter<SelectedFilterAdapter.ItemViewHolder> {

    private final Context context;
    private final AsyncListDiffer<String> differ;
    private OnFilterRemovedListener onFilterRemovedListener; // ADD THIS

    // ADD THIS INTERFACE
    public interface OnFilterRemovedListener {
        void onFilterRemoved(String removedFilter);
    }

    // ADD THIS SETTER
    public void setOnFilterRemovedListener(OnFilterRemovedListener listener) {
        this.onFilterRemovedListener = listener;
    }

    public SelectedFilterAdapter(Context context) {
        this.context = context;

        DiffUtil.ItemCallback<String> diffCallback =
                new DiffUtil.ItemCallback<String>() {
                    @Override
                    public boolean areItemsTheSame(
                            @NonNull String oldItem,
                            @NonNull String newItem
                    ) {
                        return oldItem.equals(newItem);
                    }

                    @Override
                    public boolean areContentsTheSame(
                            @NonNull String oldItem,
                            @NonNull String newItem
                    ) {
                        return oldItem.equals(newItem);
                    }
                };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    // 🔹 Submit string list
    public void submitList(List<String> list) {
        differ.submitList(new ArrayList<>(list)); // defensive copy
    }

    public Collection<String> getCurrentList() {
        return differ.getCurrentList();
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        SelectedFilterItemBinding binding =
                SelectedFilterItemBinding.inflate(
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
        String item = differ.getCurrentList().get(position);

        holder.binding.tvTitle.setText(item);

        // ❌ Remove item on close click
        holder.binding.ivClose.setOnClickListener(v -> {
            List<String> currentList =
                    new ArrayList<>(differ.getCurrentList());

            String removedFilter = currentList.get(holder.getAdapterPosition());
            currentList.remove(holder.getAdapterPosition());
            submitList(currentList);

            // NOTIFY LISTENER
            if (onFilterRemovedListener != null) {
                onFilterRemovedListener.onFilterRemoved(removedFilter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final SelectedFilterItemBinding binding;

        ItemViewHolder(@NonNull SelectedFilterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
