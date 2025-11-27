package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.PickVideoTypeRecItem;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.PickvideoListItemsBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PickVideoRecItemAdapter extends RecyclerView.Adapter<PickVideoRecItemAdapter.ItemViewHolder> {

    private final AsyncListDiffer<PickVideoTypeRecItem> differ;
    private final Set<Integer> selectedPositions;
    private final Context context;
    private final LocalManager prefsManager;
    private final OnSelectionChangeListener selectionChangeListener;

    public interface OnSelectionChangeListener {
        void onSelectionChanged(Set<Integer> selectedPositions);
    }

    public PickVideoRecItemAdapter(Context context,
                                   List<PickVideoTypeRecItem> itemList,
                                   OnSelectionChangeListener listener) {
        this.context = context;
        this.selectionChangeListener = listener;
        this.prefsManager = new LocalManager(context);

        // Load persisted selections - use category positions
        this.selectedPositions = prefsManager.loadCategoryPositions();

        DiffUtil.ItemCallback<PickVideoTypeRecItem> diffCallback = new DiffUtil.ItemCallback<PickVideoTypeRecItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull PickVideoTypeRecItem oldItem, @NonNull PickVideoTypeRecItem newItem) {
                return oldItem.getItemTitle().equals(newItem.getItemTitle());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull PickVideoTypeRecItem oldItem, @NonNull PickVideoTypeRecItem newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
        differ.submitList(itemList);
    }

    public void submitList(List<PickVideoTypeRecItem> list) {
        differ.submitList(list);
    }

    public Set<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PickvideoListItemsBinding binding = PickvideoListItemsBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PickVideoTypeRecItem item = differ.getCurrentList().get(position);

        Glide.with(context).load(item.getItemImg()).into(holder.binding.pvImgV);
        holder.binding.pvTitleTv.setText(item.getItemTitle());

        if (selectedPositions.contains(position)) {
            holder.binding.pvtCardview.setBackground(ContextCompat.getDrawable(context, R.drawable.lgtransparentbluestroke_bg));
            holder.binding.selectIV.setVisibility(android.view.View.VISIBLE);
            holder.binding.selectIV.setColorFilter(ContextCompat.getColor(context, R.color.bluemain));
        } else {
            holder.binding.pvtCardview.setBackgroundColor(Color.TRANSPARENT);
            holder.binding.selectIV.setVisibility(android.view.View.INVISIBLE);
            holder.binding.selectIV.setColorFilter(Color.TRANSPARENT);
        }

        holder.binding.pvtCardview.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            if (selectedPositions.contains(adapterPosition)) {
                selectedPositions.remove(adapterPosition);
            } else {
                selectedPositions.add(adapterPosition);
            }

            notifyItemChanged(adapterPosition);
            // Save category positions
            prefsManager.saveCategoryPositions(selectedPositions);

            if (selectionChangeListener != null) {
                selectionChangeListener.onSelectionChanged(selectedPositions);
            }
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final PickvideoListItemsBinding binding;

        public ItemViewHolder(@NonNull PickvideoListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
