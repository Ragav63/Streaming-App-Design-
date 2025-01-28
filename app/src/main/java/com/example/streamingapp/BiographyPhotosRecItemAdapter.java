package com.example.streamingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BiographyPhotosRecItemAdapter extends RecyclerView.Adapter<BiographyPhotosRecItemAdapter.ItemViewHolder>{

    private Context context;
    private List<BiographyPhotosItems> itemList;

    public BiographyPhotosRecItemAdapter(Context context, List<BiographyPhotosItems> itemList) {
        this.context = context;
        this.itemList = itemList ;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.biography_photos_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        BiographyPhotosItems item = itemList.get(position);

        holder.photosImg.setImageResource(item.getBioPhotos());

        holder.itemView.setOnClickListener(v -> {
            // Handle item click
            Intent intent = new Intent(context, FullScreenImageActivity.class);
            intent.putExtra("imageResource", item.getBioPhotos());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView photosImg;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            photosImg = itemView.findViewById(R.id.photosIv);
        }
    }

}
