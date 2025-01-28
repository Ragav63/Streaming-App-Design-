package com.example.streamingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NowOnTvItemAdapter extends RecyclerView.Adapter<NowOnTvItemAdapter.ItemViewHolder> implements Filterable {
    private static Context context;
    private List<NowOnTvItems> nowOnTvItemsList;
    private List<NowOnTvItems> itemListFull;

    public NowOnTvItemAdapter(Context context, List<NowOnTvItems> nowOnTvItemsList) {
        this.context = context;
        this.nowOnTvItemsList = nowOnTvItemsList;
        this.itemListFull = new ArrayList<>(nowOnTvItemsList);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.now_on_tv_list_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        NowOnTvItems currentItem = nowOnTvItemsList.get(position);

        holder.nowOnTvChannelName.setText(currentItem.getNowOnTvChannelName());
        holder.itemTitleTv.setText(currentItem.getNowOnTvTitle());
        holder.nowOnTvTiming.setText(currentItem.getNowOnTvTiming());
        holder.nowOnTvImg.setImageResource(currentItem.getNowOnTvImg());

        // Set click listener on the item view
        holder.itemView.setOnClickListener(v -> {
            Fragment tvFragment = new TvFragment();

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, tvFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return nowOnTvItemsList.size();
    }

    @Override
    public Filter getFilter() {
        return nowOnTvItemFilter;
    }

    private Filter nowOnTvItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<NowOnTvItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                for (NowOnTvItems item : itemListFull) {
                    if (item.getNowOnTvTitle().toLowerCase(Locale.ROOT).contains(filterPattern)) {
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
            nowOnTvItemsList.clear();
            nowOnTvItemsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public boolean isDataEmpty() {
        return nowOnTvItemsList.isEmpty();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nowOnTvChannelName, itemTitleTv, nowOnTvTiming;
        ImageView nowOnTvImg;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nowOnTvChannelName = itemView.findViewById(R.id.channelNameTv);
            itemTitleTv = itemView.findViewById(R.id.nowontvTitle_tv);
            nowOnTvTiming = itemView.findViewById(R.id.nowontvTiming_tv);
            nowOnTvImg = itemView.findViewById(R.id.nowontv_iv);
        }
    }
}
