package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.Programme;
import com.example.streamingapp.data.model.TvChannel;
import com.example.streamingapp.databinding.NowOnTvListItemsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NowOnTvItemAdapter extends RecyclerView.Adapter<NowOnTvItemAdapter.ItemViewHolder> {

    private final Context context;
    private final AsyncListDiffer<TvChannel> differ;
    private final List<TvChannel> fullList = new ArrayList<>(); // Original list for filtering
    private final OnTvItemClickListener onItemClickListener;

    public NowOnTvItemAdapter(
            Context context,
            List<TvChannel> initialItems,
            OnTvItemClickListener onItemClickListener
            ) {
        this.context = context;
        if (initialItems != null) fullList.addAll(initialItems);
        this.onItemClickListener = onItemClickListener;

        DiffUtil.ItemCallback<TvChannel> diffCallback = new DiffUtil.ItemCallback<TvChannel>() {
            @Override
            public boolean areItemsTheSame(@NonNull TvChannel oldItem, @NonNull TvChannel newItem) {
                return oldItem.getChannelName().equals(newItem.getChannelName());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull TvChannel oldItem, @NonNull TvChannel newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<TvChannel> list) {
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
        TvChannel currentItem = differ.getCurrentList().get(position);

        holder.binding.channelNameTv.setText(currentItem.getChannelName());
        if (currentItem.getProgrammes() != null && !currentItem.getProgrammes().isEmpty()) {
            // Find the currently live programme first
            Programme liveProgramme = null;
            for (Programme p : currentItem.getProgrammes()) {
                if ("live".equalsIgnoreCase(p.getStatus())) {
                    liveProgramme = p;
                    break;
                }
            }

            // If no live programme, fallback to the first programme
            Programme current = liveProgramme != null ? liveProgramme : currentItem.getProgrammes().get(0);

            holder.binding.nowontvTitleTv.setText(current.getName());
            holder.binding.nowontvTimingTv.setText(current.getTiming());



            Glide.with(context)
                    .load(current.getUrl())
                    .into(holder.binding.nowontvIv);

        } else {
            holder.binding.nowontvTitleTv.setText("No program");
            holder.binding.nowontvTimingTv.setText("");
        }

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
        List<TvChannel> filteredList = new ArrayList<>();

        for (TvChannel item : fullList) {
            if (item.getChannelName().toLowerCase(Locale.ROOT).contains(filterPattern)) {
                filteredList.add(item);
            }
        }

        differ.submitList(filteredList);
    }

    public interface OnTvItemClickListener {
        void onTvItemClick(TvChannel item, int position);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final NowOnTvListItemsBinding binding;

        public ItemViewHolder(@NonNull NowOnTvListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
