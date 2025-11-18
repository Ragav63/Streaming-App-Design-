package com.example.streamingapp.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.presentation.view.TvFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NowOnTvItemAdapter extends RecyclerView.Adapter<NowOnTvItemAdapter.ItemViewHolder> implements Filterable {
    private static Context context;
    private List<TvItems> nowOnTvItemsList;
    private List<TvItems> itemListFull;

    public NowOnTvItemAdapter(Context context, List<TvItems> nowOnTvItemsList) {
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
        TvItems currentItem = nowOnTvItemsList.get(position);

        holder.nowOnTvChannelName.setText(currentItem.getCurrentProgramName());
        holder.itemTitleTv.setText(currentItem.getTvName());
        holder.nowOnTvTiming.setText(currentItem.getCurrentProgramTiming());
        holder.nowOnTvImg.setImageResource(currentItem.getImg());

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
            List<TvItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                for (TvItems item : itemListFull) {
                    if (item.getTvName().toLowerCase(Locale.ROOT).contains(filterPattern)) {
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
