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
import com.example.streamingapp.R;
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

            @Override
            public boolean areContentsTheSame(@NonNull TvChannel oldItem, @NonNull TvChannel newItem) {
                return oldItem.getChannelName().equals(newItem.getChannelName());
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
        TvChannel channel = differ.getCurrentList().get(position);

        holder.binding.channelNameTv.setText(channel.getChannelName());

        Programme currentProgramme = getCurrentProgramme(channel);

        if (currentProgramme != null) {
            holder.binding.nowontvTitleTv.setText(currentProgramme.getName());
            holder.binding.nowontvTimingTv.setText(currentProgramme.getTiming());

            Glide.with(context)
                    .load(currentProgramme.getUrl())
                    .into(holder.binding.nowontvIv);
        } else {
            holder.binding.nowontvTitleTv.setText(R.string.no_program);
            holder.binding.nowontvTimingTv.setText("");
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            if (onItemClickListener != null && currentProgramme != null) {
                onItemClickListener.onTvItemClick(channel, currentProgramme, position);
            }
        });
    }


    private Programme getCurrentProgramme(TvChannel channel) {
        if (channel == null || channel.getProgrammes() == null || channel.getProgrammes().isEmpty()) {
            return null;
        }

        for (Programme p : channel.getProgrammes()) {
            if ("live".equalsIgnoreCase(p.getStatus())) {
                return p;
            }
        }

        return channel.getProgrammes().get(0);
    }



    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public boolean isDataEmpty() {
        return differ.getCurrentList().isEmpty();
    }


    public interface OnTvItemClickListener {
        void onTvItemClick(TvChannel channel, Programme programme, int position);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final NowOnTvListItemsBinding binding;

        public ItemViewHolder(@NonNull NowOnTvListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
