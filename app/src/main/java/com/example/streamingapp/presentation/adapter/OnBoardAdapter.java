package com.example.streamingapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.model.OnBoardItem;
import com.example.streamingapp.databinding.OnboardPageBinding;

import java.util.List;

public class OnBoardAdapter extends RecyclerView.Adapter<OnBoardAdapter.ViewHolder> {

    private final List<OnBoardItem> list;

    public OnBoardAdapter(List<OnBoardItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        OnboardPageBinding binding = OnboardPageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnBoardItem item = list.get(position);

        holder.binding.tvTitle.setText(item.getTitle());
        holder.binding.tvDescription.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        OnboardPageBinding binding;

        public ViewHolder(OnboardPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
