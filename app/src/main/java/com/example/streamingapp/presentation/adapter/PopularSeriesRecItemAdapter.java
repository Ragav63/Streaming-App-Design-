package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.view.SeriesScreenActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PopularSeriesRecItemAdapter extends RecyclerView.Adapter<PopularSeriesRecItemAdapter.ItemViewHolder> implements Filterable {

    private Context context;
    private List<SeriesItems> itemList;
    private List<SeriesItems> itemListFull;

    public PopularSeriesRecItemAdapter(Context context, List<SeriesItems> itemList) {
        this.context = context;
        this.itemList = itemList != null ? itemList : new ArrayList<>();
        this.itemListFull = new ArrayList<>(itemList); // Initialize the full list
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_series_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        SeriesItems item = itemList.get(position);

        // Set data to views
        holder.itemImg.setImageResource(item.getImage());
        holder.itemTitleTv.setText(item.getTitle());;
        holder.ratingTv.setText(item.getImdbRating());



        holder.itemView.setOnClickListener(v -> {
            for (SeriesItems items : itemList) {
                Log.d("PopularSeriesRecItemAdapter", "Item: " + items.toString());
            }

            Intent intent = new Intent(context, SeriesScreenActivity.class);
            intent.putExtra("imageResource", item.getImage());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("rating", item.getImdbRating());
            intent.putExtra("year", item.getYear());
            intent.putExtra("genre", item.getGenre());
            intent.putExtra("country", item.getCountry());
            intent.putExtra("seasons", item.getSeasons());
            intent.putExtra("description", item.getDescription());
            intent.putParcelableArrayListExtra("popularSeriesItemsList", new ArrayList<>(itemList));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {
        return popularSeriesFilter;
    }

    private Filter popularSeriesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SeriesItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                for (SeriesItems item : itemListFull) {
                    boolean matches = item.getTitle().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getImdbRating().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getYear().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getGenre().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getCountry().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getSeasons().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getDescription().toLowerCase(Locale.ROOT).contains(filterPattern);

                    if (matches) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public boolean isDataEmpty() {
        return itemList.isEmpty();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImg;
        TextView ratingTv, itemTitleTv;
        CardView itemCv;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.item_iv);
            ratingTv = itemView.findViewById(R.id.item_rating);
            itemTitleTv = itemView.findViewById(R.id.item_title);
            itemCv=itemView.findViewById(R.id.itemCv);
        }
    }

}
