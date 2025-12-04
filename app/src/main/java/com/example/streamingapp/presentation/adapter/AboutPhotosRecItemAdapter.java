package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.databinding.AboutphotosListItemsBinding;
import com.example.streamingapp.domain.repository.OnPhotoClick;

import java.util.List;

public class AboutPhotosRecItemAdapter
        extends RecyclerView.Adapter<AboutPhotosRecItemAdapter.ItemViewHolder> {

    private final OnPhotoClick clickListener;
    private Context context;

    public AboutPhotosRecItemAdapter(Context context,OnPhotoClick clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<AboutPhotosItems> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<AboutPhotosItems>() {
                @Override
                public boolean areItemsTheSame(@NonNull AboutPhotosItems oldItem,
                                               @NonNull AboutPhotosItems newItem) {
                    return oldItem.getAboutImg() == newItem.getAboutImg();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull AboutPhotosItems oldItem,
                                                  @NonNull AboutPhotosItems newItem) {
                    return oldItem.getAboutImg().equals(newItem.getAboutImg());
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
        holder.bind(context,item, clickListener);
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

        public void bind(Context context, AboutPhotosItems item, OnPhotoClick listener) {
            Glide.with(context).load(item.getAboutImg()).into(binding.photosIv);

            binding.getRoot().setOnClickListener(v -> listener.onClick("res://" + item.getAboutImg()));

        }
    }
}
