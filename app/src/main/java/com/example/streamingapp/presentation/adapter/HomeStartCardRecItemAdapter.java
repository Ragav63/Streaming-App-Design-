package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.MovieItems;

import java.util.List;

public class HomeStartCardRecItemAdapter extends RecyclerView.Adapter<HomeStartCardRecItemAdapter.ItemViewHolder> {

    private List<MovieItems> itemList;
    private OnItemClickListener listener;
    private int selectedPosition = -1;
    private static final int WIDTH_NORMAL_DP = 120;
    private static final int HEIGHT_NORMAL_DP = 70;
    private static final int WIDTH_LARGE_DP = 220;
    private static final int HEIGHT_LARGE_DP = 140;
    private static final int WIDTH_NORMAL_LARGE_DP = 240;
    private static final int HEIGHT_NORMAL_LARGE_DP = 140;
    private static final int WIDTH_LARGE_LARGE_DP = 300;
    private static final int HEIGHT_LARGE_LARGE_DP = 240;


    public HomeStartCardRecItemAdapter(List<MovieItems> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_start_card_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        MovieItems item = itemList.get(position);

        holder.homeStartImg.setImageResource(item.getImage());

        Context context = holder.itemView.getContext();
        float density = context.getResources().getDisplayMetrics().density;

        int cardWidthNormal;
        int cardHeightNormal;
        int cardWidthLarge;
        int cardHeightLarge;

        if (shouldUseLargeDimensions(holder.itemView)) {
            cardWidthNormal = (int) (WIDTH_NORMAL_LARGE_DP * density);
            cardHeightNormal = (int) (HEIGHT_NORMAL_LARGE_DP * density);
            cardWidthLarge = (int) (WIDTH_LARGE_LARGE_DP * density);
            cardHeightLarge = (int) (HEIGHT_LARGE_LARGE_DP * density);
        } else {
            cardWidthNormal = (int) (WIDTH_NORMAL_DP * density);
            cardHeightNormal = (int) (HEIGHT_NORMAL_DP * density);
            cardWidthLarge = (int) (WIDTH_LARGE_DP * density);
            cardHeightLarge = (int) (HEIGHT_LARGE_DP * density);
        }


        ViewGroup.LayoutParams layoutParams = holder.mainCv.getLayoutParams();

        if (position == selectedPosition) {
            layoutParams.width = cardWidthLarge;
            layoutParams.height = cardHeightLarge;
            holder.homeStartImg.setColorFilter(null);
        } else {
            layoutParams.width = cardWidthNormal;
            layoutParams.height = cardHeightNormal;
            int semiTransparentDimColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.semitransparent);
            holder.homeStartImg.setColorFilter(semiTransparentDimColor, PorterDuff.Mode.SRC_ATOP);
        }
        holder.mainCv.setLayoutParams(layoutParams);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
                int oldPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    public void updateSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    // Method to determine if the large dimensions should be used
    private boolean shouldUseLargeDimensions(View view) {
        Context context = view.getContext();
        int screenWidthDp = context.getResources().getConfiguration().screenWidthDp;
        return screenWidthDp >= 720;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView homeStartImg;
        CardView mainCv;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            homeStartImg = itemView.findViewById(R.id.mainImg);
            mainCv = itemView.findViewById(R.id.mainCv);
        }
    }

}
