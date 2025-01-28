package com.example.streamingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TvSelectionRecItemAdapter extends RecyclerView.Adapter<TvSelectionRecItemAdapter.ItemViewHolder>{

    private Fragment fragment;
    private List<TvNameItems> itemList;

    public TvSelectionRecItemAdapter(Fragment fragment, List<TvNameItems> itemList) {
        this.fragment = fragment;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tvname_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TvNameItems item = itemList.get(position);

        holder.tvLogoName.setText(item.getTvLogoName());
        holder.tvName.setText(item.getTvName());
        holder.currentProgramName.setText(item.getCurrentProgramName());

        // Set the initial tint color based on the favorite status
        if (item.isFavorite()) {
            holder.favIv.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.bluemain));
        } else {
            holder.favIv.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.white));
        }

        holder.favIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setFavorite(!item.isFavorite());
                if (item.isFavorite()) {
                    holder.favIv.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.bluemain));
                    Toast.makeText(holder.itemView.getContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    holder.favIv.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.white));
                    Toast.makeText(holder.itemView.getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.itemView.setOnClickListener(v -> {
            TvProgramFragment tvProgramFragment = new TvProgramFragment();

            // Pass the tvName to the TvProgramFragment
            Bundle args = new Bundle();
            args.putString("tvName", item.getTvName());
            tvProgramFragment.setArguments(args);

            FragmentManager fragmentManager;

            if (fragment != null) {
                if (fragment.isAdded()) {
                    Log.e("TvSelectionRecItemAdapter", "fragment is added");
                    fragmentManager = fragment.getParentFragmentManager();
                } else {
                    Log.e("TvSelectionRecItemAdapter", "fragment is not added, its from activity");
                    fragmentManager = fragment.getActivity().getSupportFragmentManager();
                }
            } else {
                fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            }

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.tvFrameLayout, tvProgramFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView favIv;
        TextView tvLogoName, tvName, currentProgramName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            favIv = itemView.findViewById(R.id.favIv);
            tvLogoName = itemView.findViewById(R.id.tvLogoNameTv);
            tvName = itemView.findViewById(R.id.tvName_tv);
            currentProgramName = itemView.findViewById(R.id.currentProgramName_tv);
        }
    }

}
