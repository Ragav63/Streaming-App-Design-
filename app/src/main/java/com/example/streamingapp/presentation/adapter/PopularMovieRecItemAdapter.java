package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.example.streamingapp.presentation.view.MovieScreenActivity;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PopularMovieRecItemAdapter extends RecyclerView.Adapter<PopularMovieRecItemAdapter.ItemViewHolder> implements Filterable {

    private Context context;
    private List<MovieItems> itemList;
    private List<MovieItems> itemListFull;

    public PopularMovieRecItemAdapter(Context context, List<MovieItems> itemList) {
        this.context = context;
        this.itemList = itemList != null ? itemList : new ArrayList<>();
        this.itemListFull = new ArrayList<>(itemList); // Initialize the full list
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_movies_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        MovieItems item = itemList.get(position);

        Glide.with(context).load(item.getImage()).into(holder.itemImg);
        holder.ratingTv.setText(item.getImdbRating());
        holder.itemTitleTv.setText(item.getTitle());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieScreenActivity.class);
            intent.putExtra("imageResource", item.getImage());
            intent.putExtra("rating", item.getImdbRating());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("year", item.getYear());
            intent.putExtra("genre", item.getGenre());
            intent.putExtra("country", item.getCountry());
            intent.putExtra("duration", item.getDuration());
            intent.putExtra("description", item.getDescription());
            intent.putParcelableArrayListExtra("popularMovieItemsList", new ArrayList<>(itemList));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {
        return popularMovieFilter;
    }

    private Filter popularMovieFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MovieItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                for (MovieItems item : itemListFull) {
                    boolean matches = item.getTitle().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getImdbRating().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getYear().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getGenre().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getCountry().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            item.getDuration().toLowerCase(Locale.ROOT).contains(filterPattern) ||
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
