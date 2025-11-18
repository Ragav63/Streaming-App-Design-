package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TrailerItems;

import java.util.List;

public class TrailerRecItemAdapter extends RecyclerView.Adapter<TrailerRecItemAdapter.ItemViewHolder>{

    private static Context context;
    private List<TrailerItems> itemList;

    public TrailerRecItemAdapter(Context context, List<TrailerItems> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailers_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TrailerItems item = itemList.get(position);

        holder.trailerImg.setImageResource(item.getTrailerImg());
        holder.trailerTitle.setText(item.getTrailerTitle());
        holder.trailerTiming.setText(item.getTrailerTiming());
        int data=item.getTrailerImg();
        String title=item.getTrailerTitle();


        holder.itemll.setOnClickListener(v -> {
//            Intent intent = new Intent(context, FlashSaleItemActivity.class);
//            intent.putExtra("imageResource",data);
//            intent.putExtra("title",rating);
//            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView trailerImg;
        TextView trailerTitle, trailerTiming;
        LinearLayout itemll;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerImg = itemView.findViewById(R.id.trailerImg);
            trailerTitle = itemView.findViewById(R.id.trailerTitle_tv);
            trailerTiming = itemView.findViewById(R.id.trailerTiming_tv);
            itemll=itemView.findViewById(R.id.itemll);
        }
    }

}
