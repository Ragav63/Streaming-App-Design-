package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.CategoryItems;
import com.example.streamingapp.databinding.CategoriesHomeListItemsBinding;
import com.example.streamingapp.domain.repository.OnCategoryClick;

import java.util.List;

public class CategoryHomeRecItemAdapter
        extends RecyclerView.Adapter<CategoryHomeRecItemAdapter.ItemViewHolder> {

    private final OnCategoryClick onCategoryClick;
    private final AsyncListDiffer<CategoryItems> differ;

    public CategoryHomeRecItemAdapter(OnCategoryClick onCategoryClick) {
        this.onCategoryClick = onCategoryClick;

        DiffUtil.ItemCallback<CategoryItems> diffCallback =
                new DiffUtil.ItemCallback<CategoryItems>() {
                    @Override
                    public boolean areItemsTheSame(
                            @NonNull CategoryItems oldItem,
                            @NonNull CategoryItems newItem
                    ) {
                        return oldItem.getCategoryTitle()
                                .equals(newItem.getCategoryTitle());
                    }

                    @SuppressLint("DiffUtilEquals")
                    @Override
                    public boolean areContentsTheSame(
                            @NonNull CategoryItems oldItem,
                            @NonNull CategoryItems newItem
                    ) {
                        return oldItem.equals(newItem);
                    }
                };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        CategoriesHomeListItemsBinding binding =
                CategoriesHomeListItemsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        CategoryItems item = differ.getCurrentList().get(position);

        holder.binding.itemCategoryIv.setImageResource(item.getCategoryImg());
        holder.binding.itemCategoryTitle.setText(item.getCategoryTitle());

        int total = getItemCount();

        int height;
        if (total >= 3) {
            if (position == 0) height = dp(holder.itemView.getContext(), 300); // tall
            else height = dp(holder.itemView.getContext(), 150); // small
        } else if (total == 2) {
            height = dp(holder.itemView.getContext(), 150);
        } else {
            height = dp(holder.itemView.getContext(), 200);
        }

        RecyclerView.LayoutParams params =
                (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

        params.height = height;
        holder.itemView.setLayoutParams(params);

        holder.binding.itemCv.setOnClickListener(v -> {
            if (onCategoryClick != null) onCategoryClick.onClick(item);
        });
    }


    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public void submitList(java.util.List<CategoryItems> list) {
        differ.submitList(list);
    }

    private int dp(Context c, int dp) {
        return Math.round(dp * c.getResources().getDisplayMetrics().density);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        CategoriesHomeListItemsBinding binding;

        public ItemViewHolder(@NonNull CategoriesHomeListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
