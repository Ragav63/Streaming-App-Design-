package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.CategoryItems;

import java.util.List;

public class CategoryHomeRecItemAdapter extends RecyclerView.Adapter<CategoryHomeRecItemAdapter.ItemViewHolder>{

    private static Context context;
    private List<CategoryItems> itemList;

    public CategoryHomeRecItemAdapter(Context context, List<CategoryItems> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_home_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        CategoryItems item = itemList.get(position);

        // Set data to views
        holder.itemImg.setImageResource(item.getCategoryImg());
        holder.categoryTitle.setText(item.getCategoryTitle());

        int data=item.getCategoryImg();
        String title=item.getCategoryTitle();


        holder.itemCv.setOnClickListener(v -> {
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
        ImageView itemImg;
        TextView categoryTitle;
        CardView itemCv;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.item_category_iv);
            categoryTitle = itemView.findViewById(R.id.item_category_title);

            itemCv=itemView.findViewById(R.id.itemCv);
        }
    }

}
