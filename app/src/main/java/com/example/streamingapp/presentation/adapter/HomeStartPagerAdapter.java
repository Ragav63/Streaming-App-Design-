package com.example.streamingapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.MovieItems;

import java.util.ArrayList;
import java.util.List;

public class HomeStartPagerAdapter extends RecyclerView.Adapter<HomeStartPagerAdapter.ItemViewHolder> {

    private List<MovieItems> homeStartItemsList;
    private List<Integer> favoritePositions; // List to track favorite positions
    private boolean isDownloaded = false;

    public HomeStartPagerAdapter(List<MovieItems> homeStartItemsList) {
        this.homeStartItemsList = homeStartItemsList;
        this.favoritePositions = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_start_item_page, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        MovieItems currentItem = homeStartItemsList.get(position);
        holder.homeStartRating.setText(currentItem.getImdbRating());
        holder.homeStartTitle.setText(currentItem.getTitle());
        holder.homeStartImage.setImageResource(currentItem.getImage());

        holder.homeStartWatchNow.setOnClickListener(v -> {
            if (isDownloaded) {
                Toast.makeText(v.getContext(), "Already added to Download", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "Added to Download", Toast.LENGTH_SHORT).show();
                isDownloaded = true;
            }
        });

        // Check if this position is in the list of favorite positions
        if (favoritePositions.contains(position)) {
            holder.favIv.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.bluemain));
        } else {
            holder.favIv.clearColorFilter(); // Remove the tint
        }

        holder.favIv.setOnClickListener(v -> {
            // Toggle the favorite state
            if (favoritePositions.contains(position)) {
                favoritePositions.remove(Integer.valueOf(position)); // Remove from favorites
            } else {
                favoritePositions.add(position); // Add to favorites
                Toast.makeText(v.getContext(), holder.homeStartTitle.getText().toString()+" Added to Favourite", Toast.LENGTH_SHORT).show();
            }
            notifyItemChanged(position); // Notify the adapter to refresh the item
        });
    }

    @Override
    public int getItemCount() {
        return homeStartItemsList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView homeStartRating, homeStartTitle, homeStartWatchNow;
        ImageView homeStartImage, favIv;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            homeStartRating = itemView.findViewById(R.id.homeRatingTv);
            homeStartTitle = itemView.findViewById(R.id.hometitleTv);
            homeStartImage = itemView.findViewById(R.id.homeIV);
            homeStartWatchNow = itemView.findViewById(R.id.watchNowTv);
            favIv = itemView.findViewById(R.id.favIv);
        }
    }
}
