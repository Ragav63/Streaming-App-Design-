package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.model.DownloadItems;
import com.example.streamingapp.R;

import java.util.List;

public class DownloadRecItemAdapter extends RecyclerView.Adapter<DownloadRecItemAdapter.ItemViewHolder>{

    private Context context;
    private List<DownloadItems> itemList;

    public DownloadRecItemAdapter(Context context, List<DownloadItems> itemList) {
        this.context = context;
        this.itemList = itemList ;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DownloadItems item = itemList.get(position);

        holder.downloadImg.setImageResource(item.getDownloadImg());
        holder.downloadTitle.setText(item.getDownloadTitle());
        holder.downloadEpTitle.setText(item.getDownloadEpTitle());
        holder.downloadDuration.setText(item.getDownloadDuration());

        holder.cancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, itemList.size());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView downloadImg, playIv, cancelIv;
        TextView downloadTitle, downloadEpTitle, downloadDuration;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            downloadImg = itemView.findViewById(R.id.downloadIv);
            playIv = itemView.findViewById(R.id.playIv);
            cancelIv = itemView.findViewById(R.id.cancelIv);
            downloadTitle = itemView.findViewById(R.id.downloadTitle_tv);
            downloadEpTitle = itemView.findViewById(R.id.downloadEpTitle_tv);
            downloadDuration = itemView.findViewById(R.id.downloadDuration_tv);
        }
    }

}
