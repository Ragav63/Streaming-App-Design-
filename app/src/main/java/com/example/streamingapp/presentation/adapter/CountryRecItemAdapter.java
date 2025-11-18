package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.model.CountryItems;
import com.example.streamingapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountryRecItemAdapter extends RecyclerView.Adapter<CountryRecItemAdapter.ItemViewHolder>{

    private Context context;
    private List<CountryItems> itemList;
    private boolean isAllSelected = false; // Track if "All" is selected
    private List<Integer> selectedOtherPositions = new ArrayList<>(); // Track the positions of other selected items
    private static final String PREFS_NAME = "country_prefs";
    private static final String SELECTED_COUNTRIES_KEY = "selected_countries";

    public CountryRecItemAdapter(Context context, List<CountryItems> itemList) {
        this.context = context;
        this.itemList = itemList ;
        loadSelectedItems(); // Load selected items on creation
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CountryItems item = itemList.get(position);

        holder.countryNameTv.setText(item.getCountryName());

        // Determine the color based on selection
        if (isAllSelected && position == 0) {
            holder.countryIv.setColorFilter(ContextCompat.getColor(context, R.color.bluemain));
        } else if (selectedOtherPositions.contains(position)) {
            holder.countryIv.setColorFilter(ContextCompat.getColor(context, R.color.bluemain));
        } else {
            holder.countryIv.setColorFilter(ContextCompat.getColor(context, android.R.color.transparent));
        }

        holder.itemView.setOnClickListener(v -> {
            if (position == 0) {
                // "All" is clicked
                isAllSelected = true; // Set "All" as selected
                selectedOtherPositions.clear(); // Clear other selections
                saveSelectedItems(); // Save selection
                notifyDataSetChanged(); // Update the UI
            } else {
                // Other item is clicked
                if (selectedOtherPositions.contains(position)) {
                    selectedOtherPositions.remove(Integer.valueOf(position)); // Deselect the item
                } else {
                    selectedOtherPositions.add(position); // Select the item
                }
                // Ensure "All" is unselected
                isAllSelected = false;
                saveSelectedItems(); // Save selection
                notifyDataSetChanged(); // Update the UI
            }
        });

    }

    public List<String> getSelectedItems() {
        List<String> selectedItems = new ArrayList<>();
        if (isAllSelected) {
            selectedItems.add(itemList.get(0).getCountryName());
        }
        for (int position : selectedOtherPositions) {
            selectedItems.add(itemList.get(position).getCountryName());
        }
        return selectedItems;
    }

    private void saveSelectedItems() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> selectedItems = new HashSet<>();
        if (isAllSelected) {
            selectedItems.add(itemList.get(0).getCountryName());
        }
        for (int position : selectedOtherPositions) {
            selectedItems.add(itemList.get(position).getCountryName());
        }
        editor.putStringSet(SELECTED_COUNTRIES_KEY, selectedItems);
        editor.apply();
    }

    private void loadSelectedItems() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> selectedItems = prefs.getStringSet(SELECTED_COUNTRIES_KEY, new HashSet<>());
        if (selectedItems.contains(itemList.get(0).getCountryName())) {
            isAllSelected = true;
        }
        selectedOtherPositions.clear();
        for (int i = 1; i < itemList.size(); i++) { // Start from 1 to skip "All"
            if (selectedItems.contains(itemList.get(i).getCountryName())) {
                selectedOtherPositions.add(i);
            }
        }
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView countryIv;
        TextView countryNameTv;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            countryIv = itemView.findViewById(R.id.countryIv);
            countryNameTv = itemView.findViewById(R.id.countryValTv);
        }
    }

}
