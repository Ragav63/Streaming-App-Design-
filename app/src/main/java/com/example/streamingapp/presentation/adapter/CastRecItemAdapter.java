package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.databinding.AboutListItemsBinding;
import com.example.streamingapp.domain.repository.OnCastClick;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CastRecItemAdapter extends RecyclerView.Adapter<CastRecItemAdapter.ItemViewHolder> implements Filterable {

    private Context context;
    private final OnCastClick onCastClick;
    private final AsyncListDiffer<CastItems> differ;
    private List<CastItems> fullList = new ArrayList<>();

    public CastRecItemAdapter(Context context, OnCastClick onCastClick) {
        this.context = context;
        this.onCastClick = onCastClick;

        DiffUtil.ItemCallback<CastItems> diffCallback = new DiffUtil.ItemCallback<CastItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull CastItems oldItem, @NonNull CastItems newItem) {
                return oldItem.getPersonName().equals(newItem.getPersonName());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull CastItems oldItem, @NonNull CastItems newItem) {
                return oldItem.equals(newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<CastItems> list) {
        if (list == null) list = new ArrayList<>(); // protect from null
        fullList = new ArrayList<>(list);
        differ.submitList(list);
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AboutListItemsBinding binding = AboutListItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        CastItems item = differ.getCurrentList().get(position);

        String firstImage = item.getPersonImages().isEmpty() ? null : item.getPersonImages().get(0);

        Glide.with(context).load(firstImage).into(holder.binding.personIv);
        holder.binding.personNameTv.setText(item.getPersonName());
        holder.binding.personDesignationTv.setText(item.getPersonDesignation());

        holder.binding.getRoot().setOnClickListener(v -> {
            if (onCastClick != null) {
                onCastClick.onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    @Override
    public Filter getFilter() {
        return castFilter;
    }

    private final Filter castFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CastItems> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();
                for (CastItems item : fullList) {
                    if (item.getPersonName().toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            differ.submitList((List<CastItems>) results.values);
        }
    };

    public boolean isDataEmpty() {
        return differ.getCurrentList().isEmpty();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        AboutListItemsBinding binding;

        public ItemViewHolder(@NonNull AboutListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
