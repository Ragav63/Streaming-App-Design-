package com.example.streamingapp.presentation.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.databinding.AboutphotosListItemsBinding;
import com.example.streamingapp.domain.repository.OnPhotoClick;

import java.util.ArrayList;
import java.util.List;

public class AboutPhotosRecItemAdapter
        extends RecyclerView.Adapter<AboutPhotosRecItemAdapter.ItemViewHolder> {

    private final OnPhotoClick clickListener;

    public AboutPhotosRecItemAdapter(OnPhotoClick clickListener) {
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<AboutPhotosItems> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<AboutPhotosItems>() {
                @Override
                public boolean areItemsTheSame(@NonNull AboutPhotosItems oldItem,
                                               @NonNull AboutPhotosItems newItem) {
                    return oldItem.getAboutImg() == newItem.getAboutImg();
                }

                @Override
                public boolean areContentsTheSame(@NonNull AboutPhotosItems oldItem,
                                                  @NonNull AboutPhotosItems newItem) {
                    return oldItem.getAboutImg() == newItem.getAboutImg();
                }

            };

    public final AsyncListDiffer<AboutPhotosItems> differ =
            new AsyncListDiffer<>(this, DIFF_CALLBACK);

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AboutphotosListItemsBinding binding = AboutphotosListItemsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Log.d("PhotosAdapter", "onBindViewHolder called for position: " + position);

        AboutPhotosItems item = differ.getCurrentList().get(position);
        holder.bind(item, clickListener);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();

    }

    public void submitList(List<AboutPhotosItems> list) {
        differ.submitList(list);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final AboutphotosListItemsBinding binding;

        public ItemViewHolder(AboutphotosListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AboutPhotosItems item, OnPhotoClick listener) {
            binding.photosIv.setImageResource(item.getAboutImg());

            binding.getRoot().setOnClickListener(v -> listener.onClick("res://" + item.getAboutImg()));

        }
    }
}
