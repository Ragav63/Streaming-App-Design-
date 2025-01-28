package com.example.streamingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContinueWatchingFragmentItemAdapter extends RecyclerView.Adapter<ContinueWatchingFragmentItemAdapter.ItemViewHolder> {
    private static Context context;
    private List<ContinueWatchingItems> continueWatchingItemsList;

    public ContinueWatchingFragmentItemAdapter(Context context, List<ContinueWatchingItems> continueWatchingItemsList) {
        this.context = context;
        this.continueWatchingItemsList = continueWatchingItemsList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.continue_watching_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ContinueWatchingItems currentItem = continueWatchingItemsList.get(position);
        holder.conWatchTitleTv.setText(currentItem.getConWatchTitle());
        holder.conWatchDescTv.setText(currentItem.getConWatchDesc());
        holder.conWatchIv.setImageResource(currentItem.getConWatchImg());

        setProgressBar(holder.conWatchPbar, 25);

        holder.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item from the list
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    continueWatchingItemsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, continueWatchingItemsList.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return continueWatchingItemsList.size();
    }

    private void setProgressBar(ProgressBar progressBar, int progress) {
        // Set progress value
        progressBar.setProgress(progress);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView conWatchTitleTv, conWatchDescTv;
        ImageView conWatchIv, playIv, closeIv;
        ProgressBar conWatchPbar;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            conWatchTitleTv = itemView.findViewById(R.id.conWatchTitle_tv);
            conWatchDescTv = itemView.findViewById(R.id.conWatchDesc_tv);
            conWatchIv = itemView.findViewById(R.id.conWatch_iv);
            conWatchPbar = itemView.findViewById(R.id.conWatchPbar);
            playIv = itemView.findViewById(R.id.playIv);
            closeIv = itemView.findViewById(R.id.conWatchCancelIv);
        }
    }
}
