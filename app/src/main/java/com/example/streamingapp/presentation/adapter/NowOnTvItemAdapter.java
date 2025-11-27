package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.databinding.NowOnTvListItemsBinding;
import com.example.streamingapp.presentation.view.TvFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NowOnTvItemAdapter extends RecyclerView.Adapter<NowOnTvItemAdapter.ItemViewHolder> {

    private final AsyncListDiffer<TvItems> differ;
    private final List<TvItems> fullList; // Original list for filtering
    private final OnTvItemClickListener onItemClickListener;

    public NowOnTvItemAdapter(OnTvItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.fullList = new ArrayList<>();

        DiffUtil.ItemCallback<TvItems> diffCallback = new DiffUtil.ItemCallback<TvItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull TvItems oldItem, @NonNull TvItems newItem) {
                return oldItem.getTvName().equals(newItem.getTvName());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull TvItems oldItem, @NonNull TvItems newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<TvItems> list) {
        fullList.clear();
        fullList.addAll(list);
        differ.submitList(new ArrayList<>(list));
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NowOnTvListItemsBinding binding = NowOnTvListItemsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TvItems currentItem = differ.getCurrentList().get(position);

        holder.binding.channelNameTv.setText(currentItem.getCurrentProgramName());
        holder.binding.nowontvTitleTv.setText(currentItem.getTvName());
        holder.binding.nowontvTimingTv.setText(currentItem.getCurrentProgramTiming());
        holder.binding.nowontvIv.setImageResource(currentItem.getImg());

        holder.binding.getRoot().setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onTvItemClick(currentItem, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public boolean isDataEmpty() {
        return differ.getCurrentList().isEmpty();
    }

    /**
     * Filters the current list dynamically using AsyncListDiffer
     * without overwriting the original full list.
     */
    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            differ.submitList(new ArrayList<>(fullList));
            return;
        }

        String filterPattern = query.toLowerCase(Locale.ROOT).trim();
        List<TvItems> filteredList = new ArrayList<>();

        for (TvItems item : fullList) {
            if (item.getTvName().toLowerCase(Locale.ROOT).contains(filterPattern)) {
                filteredList.add(item);
            }
        }

        differ.submitList(filteredList);
    }

    public interface OnTvItemClickListener {
        void onTvItemClick(TvItems item, int position);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final NowOnTvListItemsBinding binding;

        public ItemViewHolder(@NonNull NowOnTvListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
