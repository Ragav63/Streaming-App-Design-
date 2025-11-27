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
import com.example.streamingapp.data.model.PickGenreTypeRecItem;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.GenreListItemsBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PickGenreRecItemAdapter extends RecyclerView.Adapter<PickGenreRecItemAdapter.ItemViewHolder> {

    private final AsyncListDiffer<PickGenreTypeRecItem> differ;
    private final Set<Integer> selectedPositions;
    private final Context context;
    private final LocalManager prefsManager;
    private final OnSelectionChangeListener selectionChangeListener;

    public interface OnSelectionChangeListener {
        void onSelectionChanged(Set<Integer> selectedPositions);
    }

    public PickGenreRecItemAdapter(Context context,
                                   List<PickGenreTypeRecItem> itemList,
                                   OnSelectionChangeListener listener) {
        this.context = context;
        this.selectionChangeListener = listener;
        this.prefsManager = new LocalManager(context);

        // Load persisted selections
        this.selectedPositions = prefsManager.loadGenreSelection();

        DiffUtil.ItemCallback<PickGenreTypeRecItem> diffCallback = new DiffUtil.ItemCallback<PickGenreTypeRecItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull PickGenreTypeRecItem oldItem, @NonNull PickGenreTypeRecItem newItem) {
                return oldItem.getItemTitle().equals(newItem.getItemTitle());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull PickGenreTypeRecItem oldItem, @NonNull PickGenreTypeRecItem newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
        differ.submitList(itemList);
    }

    public void submitList(List<PickGenreTypeRecItem> list) {
        differ.submitList(list);
    }

    public Set<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GenreListItemsBinding binding = GenreListItemsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PickGenreTypeRecItem item = differ.getCurrentList().get(position);

        Glide.with(context).load(item.getItemImg()).into(holder.binding.genreImgV);
        holder.binding.genreTitleTv.setText(item.getItemTitle());

        if (selectedPositions.contains(position)) {
            holder.binding.genreTCardview.setBackground(ContextCompat.getDrawable(context, R.drawable.lgtransparentbluestroke_bg));
            holder.binding.selectIV.setVisibility(android.view.View.VISIBLE);
            holder.binding.selectIV.setColorFilter(ContextCompat.getColor(context, R.color.bluemain));
        } else {
            holder.binding.genreTCardview.setBackgroundColor(Color.TRANSPARENT);
            holder.binding.selectIV.setVisibility(android.view.View.INVISIBLE);
            holder.binding.selectIV.setColorFilter(Color.TRANSPARENT);
        }

        holder.binding.genreTCardview.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (selectedPositions.contains(adapterPosition)) selectedPositions.remove(adapterPosition);
            else selectedPositions.add(adapterPosition);

            notifyItemChanged(adapterPosition);
            prefsManager.saveGenreSelection(selectedPositions);

            if (selectionChangeListener != null) selectionChangeListener.onSelectionChanged(selectedPositions);
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final GenreListItemsBinding binding;

        public ItemViewHolder(@NonNull GenreListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
