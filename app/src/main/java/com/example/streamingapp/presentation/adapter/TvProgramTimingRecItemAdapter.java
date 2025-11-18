package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.domain.repository.OnTimeSelectedListener;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvProgramTimingItems;

import java.util.List;

public class TvProgramTimingRecItemAdapter extends RecyclerView.Adapter<TvProgramTimingRecItemAdapter.ItemViewHolder>{

    private Context context;
    private List<TvProgramTimingItems> itemList;
    private OnTimeSelectedListener listener;
    private int selectedPosition = 0;

    public interface OnTimingSelectedListener {
        void onTimingSelected(int position);
    }

    public TvProgramTimingRecItemAdapter(Context context, List<TvProgramTimingItems> itemList, OnTimeSelectedListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
        if (!itemList.isEmpty()) {
            selectedPosition = 0; // Set the first item as selected
        }
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tvprogram_timing_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TvProgramTimingItems item = itemList.get(position);

        holder.timingTv.setText(item.getTiming());

        // Set the background based on selection
        if (position == selectedPosition) {
            holder.itemRl.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg); // Selected background
        } else {
            holder.itemRl.setBackgroundResource(R.drawable.lgtransparentgreystroke_bg); // Default background
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimeSelected(item.getTiming());
                selectItem(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
        RelativeLayout itemRl;
        TextView timingTv;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRl = itemView.findViewById(R.id.itemlRl);
            timingTv = itemView.findViewById(R.id.timing_tv);
        }
    }

}
