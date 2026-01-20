package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.databinding.HomeStartCardListItemsBinding;

import java.util.List;

public class HomeStartCardRecItemAdapter
        extends RecyclerView.Adapter<HomeStartCardRecItemAdapter.ItemViewHolder> {

    private final AsyncListDiffer<MovieItems> differ;
    private final OnItemClickListener listener;
    private int selectedPosition = -1;

    private Context context;

    public HomeStartCardRecItemAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;

        DiffUtil.ItemCallback<MovieItems> diffCallback =
                new DiffUtil.ItemCallback<MovieItems>() {
                    @Override
                    public boolean areItemsTheSame(MovieItems oldItem, MovieItems newItem) {
                        return oldItem.getTitle().equals(newItem.getTitle());
                    }


                    @Override
                    public boolean areContentsTheSame(MovieItems oldItem, MovieItems newItem) {
                        return oldItem.equals(newItem);
                    }
                };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<MovieItems> list) {
        differ.submitList(list);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HomeStartCardListItemsBinding binding =
                HomeStartCardListItemsBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                );
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MovieItems item = differ.getCurrentList().get(position);
        Glide.with(context).load(item.getPoster()).into(holder.binding.mainImg);

        // Click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);

            int oldPos = selectedPosition;
            selectedPosition = position;

            if (oldPos != RecyclerView.NO_POSITION) notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
        });

        // Highlight selected item
        if (position == selectedPosition) {
            holder.binding.mainImg.setColorFilter(null);
        } else {
            int dim = ContextCompat.getColor(holder.itemView.getContext(), R.color.semitransparent);
            holder.binding.mainImg.setColorFilter(dim, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public void updateSelectedPosition(int pos) {
        int oldPos = selectedPosition;
        selectedPosition = pos;

        if (oldPos != RecyclerView.NO_POSITION) notifyItemChanged(oldPos);
        notifyItemChanged(selectedPosition);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        HomeStartCardListItemsBinding binding;

        ItemViewHolder(HomeStartCardListItemsBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}