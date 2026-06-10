package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ReceiptItem;

import java.util.List;

public class ReceiptItemAdapter extends RecyclerView.Adapter<ReceiptItemAdapter.ViewHolder> {

    private List<ReceiptItem> itemList;
    private List<String> choosenFriends;

    public ReceiptItemAdapter(List<ReceiptItem> itemList, List<String> choosenFriends) {
        this.itemList = itemList;
        this.choosenFriends = choosenFriends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceiptItem item = itemList.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvItemPrice.setText("Rp " + item.getPrice());

        holder.containerCheckboxes.removeAllViews();

        for (String friend : choosenFriends) {
            CheckBox cb = new CheckBox(holder.itemView.getContext());
            cb.setText(friend);
            cb.setTextSize(14f);

            cb.setChecked(item.getSharedWith().contains(friend));

            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    item.addFriend(friend);
                } else {
                    item.removeFriend(friend);
                }
            });

            holder.containerCheckboxes.addView(cb);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemPrice;
        LinearLayout containerCheckboxes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            containerCheckboxes = itemView.findViewById(R.id.containerCheckboxes);
        }
    }
}