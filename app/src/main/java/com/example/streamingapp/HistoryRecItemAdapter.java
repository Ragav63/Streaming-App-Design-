package com.example.streamingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryRecItemAdapter extends RecyclerView.Adapter<HistoryRecItemAdapter.ItemViewHolder>{

    private Context context;
    private List<HistoryItems> itemList;

    public HistoryRecItemAdapter(Context context, List<HistoryItems> itemList) {
        this.context = context;
        this.itemList = itemList ;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        HistoryItems item = itemList.get(position);

        holder.historyImg.setImageResource(item.getHistoryImg());
        holder.historyRating.setText(item.getHistoryRating());
        holder.historyTiming.setText(item.getHistoryTiming());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullScreenImageActivity.class);
            intent.putExtra("imageResource", item.getHistoryImg());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView historyImg;
        TextView historyRating, historyTiming;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            historyImg = itemView.findViewById(R.id.item_iv);
            historyRating = itemView.findViewById(R.id.item_rating);
            historyTiming = itemView.findViewById(R.id.item_timingTv);
        }
    }

}
