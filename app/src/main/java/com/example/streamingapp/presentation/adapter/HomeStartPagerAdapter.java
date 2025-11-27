package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.databinding.HomeStartItemPageBinding;
import com.example.streamingapp.domain.repository.HomeStartItemClick;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeStartPagerAdapter extends RecyclerView.Adapter<HomeStartPagerAdapter.ItemViewHolder> {

    private final AsyncListDiffer<MovieItems> differ;
    private final HomeStartItemClick onItemActionListener;
    private final Set<Integer> favoritePositions = new HashSet<>();
    private boolean isDownloaded = false;

    public HomeStartPagerAdapter(HomeStartItemClick onItemActionListener) {
        this.onItemActionListener = onItemActionListener;

        DiffUtil.ItemCallback<MovieItems> diffCallback = new DiffUtil.ItemCallback<MovieItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull MovieItems oldItem, @NonNull MovieItems newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull MovieItems oldItem, @NonNull MovieItems newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<MovieItems> list) {
        differ.submitList(new ArrayList<>(list));
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HomeStartItemPageBinding binding = HomeStartItemPageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        MovieItems currentItem = differ.getCurrentList().get(position);

        holder.binding.homeRatingTv.setText(currentItem.getImdbRating());
        holder.binding.hometitleTv.setText(currentItem.getTitle());
        holder.binding.homeIV.setImageResource(currentItem.getImage());

        // Item click
        holder.binding.getRoot().setOnClickListener(v ->
                onItemActionListener.onAction(currentItem, position, HomeStartItemClick.ActionType.ITEM_CLICK, false)
        );

        // Watch Now click
        holder.binding.watchNowTv.setOnClickListener(v -> {
            if (!isDownloaded) {
                isDownloaded = true;
                Toast.makeText(v.getContext(), "Added to Download", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "Already added to Download", Toast.LENGTH_SHORT).show();
            }
            onItemActionListener.onAction(currentItem, position, HomeStartItemClick.ActionType.WATCH_NOW_CLICK, false);
        });

        // Favorite click
        holder.binding.favIv.setOnClickListener(v -> {
            boolean nowFavorite = !favoritePositions.contains(position);
            if (nowFavorite) {
                favoritePositions.add(position);
                Toast.makeText(v.getContext(), currentItem.getTitle() + " Added to Favourite", Toast.LENGTH_SHORT).show();
            } else {
                favoritePositions.remove(position);
            }
            notifyItemChanged(position);
            onItemActionListener.onAction(currentItem, position, HomeStartItemClick.ActionType.FAVORITE_CLICK, nowFavorite);
        });

        // Favorite state coloring
        if (favoritePositions.contains(position)) {
            holder.binding.favIv.setColorFilter(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.bluemain)
            );
        } else {
            holder.binding.favIv.clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final HomeStartItemPageBinding binding;

        public ItemViewHolder(@NonNull HomeStartItemPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
