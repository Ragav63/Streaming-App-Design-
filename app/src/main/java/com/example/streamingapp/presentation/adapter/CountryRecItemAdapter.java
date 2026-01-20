package com.example.streamingapp.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.CountryItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.CountryListItemsBinding;
import com.example.streamingapp.domain.repository.OnCountryClick;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountryRecItemAdapter extends RecyclerView.Adapter<CountryRecItemAdapter.ItemViewHolder> {

    private final Context context;
    private final AsyncListDiffer<CountryItems> differ;
    private final OnCountryClick onCountryClick;

    private boolean isAllSelected = false;
    private final List<Integer> selectedOtherPositions = new ArrayList<>();



    public CountryRecItemAdapter(Context context, OnCountryClick onCountryClick) {
        this.context = context;
        this.onCountryClick = onCountryClick;

        DiffUtil.ItemCallback<CountryItems> diffCallback = new DiffUtil.ItemCallback<CountryItems>() {
            @Override
            public boolean areItemsTheSame(@NonNull CountryItems oldItem, @NonNull CountryItems newItem) {
                return oldItem.getCountryName().equals(newItem.getCountryName());
            }


            @Override
            public boolean areContentsTheSame(@NonNull CountryItems oldItem, @NonNull CountryItems newItem) {
                return oldItem.getCountryName().equals(newItem.getCountryName());
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    public void submitList(List<CountryItems> list) {
        differ.submitList(list);
        loadSelectedItems(list);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CountryListItemsBinding binding = CountryListItemsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        CountryItems item = differ.getCurrentList().get(position);
        holder.binding.countryValTv.setText(item.getCountryName());

        // Set selection color
        if (isAllSelected && position == 0) {
            holder.binding.countryIv.setColorFilter(ContextCompat.getColor(context, R.color.bluemain));
        } else if (selectedOtherPositions.contains(position)) {
            holder.binding.countryIv.setColorFilter(ContextCompat.getColor(context, R.color.bluemain));
        } else {
            holder.binding.countryIv.setColorFilter(ContextCompat.getColor(context, android.R.color.transparent));
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            int previousAllPosition = 0; // position of "All" item
            List<Integer> changedPositions = new ArrayList<>();

            if (position == 0) { // "All" clicked
                if (!isAllSelected) {
                    // mark "All" selected, clear others
                    isAllSelected = true;
                    changedPositions.addAll(selectedOtherPositions); // these will change color
                    selectedOtherPositions.clear();
                    changedPositions.add(position); // "All" changes too
                }
            } else { // specific country clicked
                if (selectedOtherPositions.contains(position)) {
                    selectedOtherPositions.remove(Integer.valueOf(position));
                } else {
                    selectedOtherPositions.add(position);
                }
                changedPositions.add(position);

                // "All" item may change as well
                if (isAllSelected) {
                    isAllSelected = false;
                    changedPositions.add(previousAllPosition);
                }
            }

            saveSelectedItems();

            // Update only the items that changed
            for (int pos : changedPositions) {
                notifyItemChanged(pos);
            }

            if (onCountryClick != null) {
                onCountryClick.onClick(getSelectedItems());
            }
        });

    }

    public List<String> getSelectedItems() {
        List<String> selectedItems = new ArrayList<>();
        List<CountryItems> currentList = differ.getCurrentList();
        if (currentList.isEmpty()) return selectedItems;

        if (isAllSelected) {
            selectedItems.add(currentList.get(0).getCountryName());
        }
        for (int pos : selectedOtherPositions) {
            selectedItems.add(currentList.get(pos).getCountryName());
        }
        return selectedItems;
    }

    private void saveSelectedItems() {
        LocalManager.saveCountrySelection(new HashSet<>(getSelectedItems()));
    }


    private void loadSelectedItems(List<CountryItems> list) {
        Set<String> saved = LocalManager.loadCountrySelection();

        isAllSelected = saved.contains(list.get(0).getCountryName());
        selectedOtherPositions.clear();

        for (int i = 1; i < list.size(); i++) {
            if (saved.contains(list.get(i).getCountryName())) {
                selectedOtherPositions.add(i);
            }
        }
    }


    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final CountryListItemsBinding binding;

        public ItemViewHolder(@NonNull CountryListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
