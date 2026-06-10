package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<String> actualDataList;
    private List<String> viewedDataList;
    private OnClickListener listener;

    public interface OnClickListener{
        void onItemClick(String friend);
    }

    public void dataFilter(String search){
        viewedDataList.clear();

        if(search.isEmpty()){
            viewedDataList.addAll(actualDataList);
        }else{
            search = search.toLowerCase();
            for (String item : actualDataList){
                if(item.toLowerCase().contains(search)){
                    viewedDataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public SearchAdapter(List<String> searchList, OnClickListener listener) {
        this.actualDataList = new ArrayList<>(searchList);
        this.viewedDataList = new ArrayList<>(searchList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        String friend = viewedDataList.get(position);
        holder.tvFriendsName.setText(friend);

        holder.tvFriendsName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return viewedDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvFriendsName;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tvFriendsName = itemView.findViewById(R.id.tvFriendsName);
        }
    }
}

