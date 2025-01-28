package com.example.streamingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PickVideoRecItemAdapter extends RecyclerView.Adapter<PickVideoRecItemAdapter.ItemViewHolder>{

    private static Context context;
    private List<PickVideoTypeRecItem> itemList;
    private Set<Integer> selectedPositions = new HashSet<>();
    private Runnable selectionChangedCallback;
    public PickVideoRecItemAdapter(Context context, List<PickVideoTypeRecItem> itemList, Runnable selectionChangedCallback) {
        this.context = context;
        this.itemList = itemList;
        this.selectionChangedCallback = selectionChangedCallback;
        loadSelectedPositions(); // Load state
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickvideo_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    private void saveSelectedPositions() {
        SharedPreferences prefs = context.getSharedPreferences("video_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("selected_positions", selectedPositions.stream().map(String::valueOf).collect(Collectors.toSet()));
        editor.apply();
    }

    private void loadSelectedPositions() {
        SharedPreferences prefs = context.getSharedPreferences("video_prefs", Context.MODE_PRIVATE);
        Set<String> selectedSet = prefs.getStringSet("selected_positions", new HashSet<>());
        selectedPositions = selectedSet.stream().map(Integer::parseInt).collect(Collectors.toSet());
    }

    public Set<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PickVideoTypeRecItem item = itemList.get(position);

        Glide.with(context).load(item.getItemImg()).into(holder.itemImg);
        holder.itemTitle.setText(item.getItemTitle());

        if (selectedPositions.contains(position)) {
            holder.contentLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.lgtransparentbluestroke_bg));
            holder.selectIv.setVisibility(View.VISIBLE);
            holder.selectIv.setColorFilter(ContextCompat.getColor(context, R.color.bluemain));
        } else {
            holder.contentLayout.setBackgroundColor(Color.TRANSPARENT);
            holder.selectIv.setColorFilter(Color.TRANSPARENT);
            holder.selectIv.setVisibility(View.INVISIBLE);
        }

        holder.contentLayout.setOnClickListener(v -> {
            if (selectedPositions.contains(holder.getAdapterPosition())) {
                selectedPositions.remove(holder.getAdapterPosition());
            } else {
                selectedPositions.add(holder.getAdapterPosition());
            }
            notifyItemChanged(holder.getAdapterPosition());
            saveSelectedPositions(); // Save state
            selectionChangedCallback.run();
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    public boolean isAnyItemSelected() {
        return !selectedPositions.isEmpty();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImg, selectIv;
        TextView itemTitle;
        CardView contentLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            contentLayout=itemView.findViewById(R.id.pvtCardview);
            selectIv = itemView.findViewById(R.id.selectIV);
            itemImg = itemView.findViewById(R.id.pvImgV);
            itemTitle = itemView.findViewById(R.id.pvTitleTv);
        }
    }

}
