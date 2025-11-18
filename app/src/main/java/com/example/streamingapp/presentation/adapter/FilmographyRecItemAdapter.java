package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.presentation.view.MovieScreenActivity;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.view.SeriesScreenActivity;

import java.util.ArrayList;
import java.util.List;

public class FilmographyRecItemAdapter<T> extends RecyclerView.Adapter<FilmographyRecItemAdapter.ItemViewHolder>{

    private Context context;
    private List<T> itemList;

    public FilmographyRecItemAdapter(Context context, List<T> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filmography_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        T item = itemList.get(position);

        if (item instanceof MovieItems) {
            MovieItems movieItem = (MovieItems) item;
            holder.filmRating.setText(movieItem.getImdbRating());
            holder.filmImg.setImageResource(movieItem.getImage());
            holder.itemTitleTv.setText(movieItem.getTitle());
        } else if (item instanceof SeriesItems) {
            SeriesItems seriesItem = (SeriesItems) item;
            holder.filmRating.setText(seriesItem.getImdbRating());
            holder.filmImg.setImageResource(seriesItem.getImage());
            holder.itemTitleTv.setText(seriesItem.getTitle());
        }

        holder.itemView.setOnClickListener(v -> {

            Intent intent;
            if (item instanceof MovieItems) {
                MovieItems movieItem = (MovieItems) item;
                intent = new Intent(context, MovieScreenActivity.class);
                intent.putExtra("imageResource", movieItem.getImage());
                intent.putExtra("title", movieItem.getTitle());
                intent.putExtra("rating", movieItem.getImdbRating());
                intent.putExtra("year", movieItem.getYear());
                intent.putExtra("genre", movieItem.getGenre());
                intent.putExtra("country", movieItem.getCountry());
                intent.putExtra("duration", movieItem.getDuration());
                intent.putExtra("description", movieItem.getDescription());
                intent.putParcelableArrayListExtra("popularMovieItemsList", (ArrayList<? extends Parcelable>) new ArrayList<>(itemList));
            } else if (item instanceof SeriesItems) {
                SeriesItems seriesItem = (SeriesItems) item;
                intent = new Intent(context, SeriesScreenActivity.class);
                intent.putExtra("imageResource", seriesItem.getImage());
                intent.putExtra("title", seriesItem.getTitle());
                intent.putExtra("rating", seriesItem.getImdbRating());
                intent.putExtra("year", seriesItem.getYear());
                intent.putExtra("genre", seriesItem.getGenre());
                intent.putExtra("country", seriesItem.getCountry());
                intent.putExtra("seasons", seriesItem.getSeasons());
                intent.putExtra("description", seriesItem.getDescription());
                intent.putParcelableArrayListExtra("popularSeriesItemsList", (ArrayList<? extends Parcelable>) new ArrayList<>(itemList));
            } else {
                return; // Do nothing if item type is not recognized
            }

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView filmImg;
        TextView filmRating, itemTitleTv;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            filmImg = itemView.findViewById(R.id.item_iv);
            filmRating = itemView.findViewById(R.id.item_rating);
            itemTitleTv = itemView.findViewById(R.id.item_title);
        }
    }

}
