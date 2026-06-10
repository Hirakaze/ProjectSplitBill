package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ChoosenAdapter extends RecyclerView.Adapter<ChoosenAdapter.ViewHolder> {

    private List<String> choosenList;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener{
        void OnDeleteClick(int position);
    }

    public ChoosenAdapter(List<String> choosenList, OnDeleteClickListener listener) {
        this.choosenList = choosenList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChoosenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout. item_choosen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoosenAdapter.ViewHolder holder, int position) {
        String friend = choosenList.get(position);
        holder.tvChoosenName.setText(friend);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnDeleteClick(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return choosenList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvChoosenName;
        ImageButton deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChoosenName = itemView.findViewById(R.id.tvChoosenName);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
