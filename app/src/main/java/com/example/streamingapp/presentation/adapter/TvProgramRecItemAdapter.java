package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvProgramItems;

import java.util.ArrayList;
import java.util.List;

public class TvProgramRecItemAdapter extends RecyclerView.Adapter<TvProgramRecItemAdapter.ItemViewHolder>{

    private Context context;
    private List<TvProgramItems> itemList;
    private int selectedPosition = 0;

    public TvProgramRecItemAdapter(Context context, List<TvProgramItems> itemList) {
        this.context = context;
        this.itemList = itemList ;
        if (!itemList.isEmpty()) {
            selectedPosition = 0; // Set the first item as selected
        }
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tvprogram_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TvProgramItems item = itemList.get(position);

        holder.programImg.setImageResource(item.getProgramImg());
        holder.timingTv.setText(item.getProgramTiming());
        holder.descriptionTv.setText(item.getProgramTitle());

        // Set the background based on selection
        if (position == selectedPosition) {
            holder.tvProgramCv.setBackgroundResource(R.drawable.lgblackbluestroke_bg);
        } else {
            holder.tvProgramCv.setBackgroundResource(R.drawable.lgtransparent_bg);
        }

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Currently Watching " + holder.descriptionTv.getText().toString(), Toast.LENGTH_SHORT).show();
            selectItem(position);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateProgramList(List<TvProgramItems> newItems) {
        this.itemList = new ArrayList<>(newItems); // Replace with new list
        notifyDataSetChanged(); // Notify adapter of data changes
    }

    private void selectItem(int position) {
        if (position != selectedPosition) {
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition); // Clear previous selection
            notifyItemChanged(selectedPosition); // Highlight new selection
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView tvProgramCv;
        ImageView programImg;
        TextView timingTv, descriptionTv ;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProgramCv = itemView.findViewById(R.id.tvProgramCv);
            programImg = itemView.findViewById(R.id.programIv);
            timingTv = itemView.findViewById(R.id.timing_tv);
            descriptionTv = itemView.findViewById(R.id.programDesc_tv);
        }
    }

}
