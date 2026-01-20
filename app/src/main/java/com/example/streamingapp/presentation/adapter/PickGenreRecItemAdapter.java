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
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.databinding.PickAvatorListItemBinding;

import java.util.List;
import java.util.Set;

public class PickGenreRecItemAdapter extends RecyclerView.Adapter<PickGenreRecItemAdapter.ItemViewHolder> {

    private final AsyncListDiffer<PickItem> differ;
    private final Set<Integer> selectedPositions;
    private final Context context;
    private final OnSelectionChangeListener selectionChangeListener;

    public interface OnSelectionChangeListener {
        void onSelectionChanged(Set<Integer> selectedPositions);
    }

    public PickGenreRecItemAdapter(Context context,
                                   List<PickItem> itemList,
                                   OnSelectionChangeListener listener) {
        this.context = context;
        this.selectionChangeListener = listener;

        // Load persisted selections
        this.selectedPositions = LocalManager.loadGenreSelection();

        DiffUtil.ItemCallback<PickItem> diffCallback = new DiffUtil.ItemCallback<PickItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull PickItem oldItem, @NonNull PickItem newItem) {
                return oldItem.getItemTitle().equals(newItem.getItemTitle());
            }


            @Override
            public boolean areContentsTheSame(@NonNull PickItem oldItem, @NonNull PickItem newItem) {
                return oldItem.getItemTitle().equals(newItem.getItemTitle());
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
        differ.submitList(itemList);
    }

    public void submitList(List<PickItem> list) {
        differ.submitList(list);
    }

    public Set<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PickAvatorListItemBinding binding = PickAvatorListItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PickItem item = differ.getCurrentList().get(position);

        Glide.with(context).load(item.getItemImg()).into(holder.binding.ivImage);
        holder.binding.tvTitle.setText(item.getItemTitle());

        if (selectedPositions.contains(position)) {
            holder.binding.tvTitle.setBackground(ContextCompat.getDrawable(context, R.drawable.lgtransparentbluestroke_bg));
            holder.binding.selectIV.setVisibility(android.view.View.VISIBLE);
            holder.binding.selectIV.setColorFilter(ContextCompat.getColor(context, R.color.bluemain));
        } else {
            holder.binding.selectIV.setVisibility(android.view.View.INVISIBLE);
            holder.binding.selectIV.setColorFilter(Color.TRANSPARENT);
        }

        holder.binding.cvRoot.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (selectedPositions.contains(adapterPosition)) selectedPositions.remove(adapterPosition);
            else selectedPositions.add(adapterPosition);

            notifyItemChanged(adapterPosition);
            LocalManager.saveGenreSelection(selectedPositions);

            if (selectionChangeListener != null) selectionChangeListener.onSelectionChanged(selectedPositions);
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final PickAvatorListItemBinding binding;

        public ItemViewHolder(@NonNull PickAvatorListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
