package com.example.myapplication.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Transaction;
import java.util.List;
import java.util.Map;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<Transaction> transactionList;
    private final OnItemClickListener clickListener;
    private final OnItemDeleteListener deleteListener;

    public interface OnItemClickListener { void onClick(Transaction transaction); }
    public interface OnItemDeleteListener { void onDeleteClick(Transaction transaction); }

    public HistoryAdapter(List<Transaction> transactionList, OnItemClickListener clickListener, OnItemDeleteListener deleteListener) {
        this.transactionList = transactionList;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Transaction tx = transactionList.get(position);
        holder.tvResto.setText(tx.getRestaurantName());
        holder.tvDate.setText(tx.getDate());

        // Kembalikan teks total ke bentuk normal (tanpa embel-embel lunas)
        holder.tvTotal.setText("Rp " + tx.getGrandTotal());

        // --- 1. LOGIKA CEK STATUS LUNAS ---
        boolean apakahSemuaLunas = true;
        List<Map<String, Object>> participants = tx.getParticipants();

        if (participants != null && !participants.isEmpty()) {
            for (Map<String, Object> p : participants) {
                Boolean isPaidObj = (Boolean) p.get("isPaid");
                boolean isPaid = isPaidObj != null ? isPaidObj : false;

                if (!isPaid) {
                    apakahSemuaLunas = false;
                    break;
                }
            }
        } else {
            apakahSemuaLunas = false;
        }

        // --- 2. UBAH WARNA BOX (CARDVIEW) BERDASARKAN STATUS ---
        CardView cardBox = (CardView) holder.itemView; // Tangkap kotak CardView-nya

        if (apakahSemuaLunas) {
            // Jika Lunas: Ubah background CardView jadi Hijau Lembut (Pastel Green)
            cardBox.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
        } else {
            // Jika Belum Lunas: Ubah background CardView jadi Merah Lembut (Pastel Red)
            cardBox.setCardBackgroundColor(Color.parseColor("#FFCDD2"));
        }

        // Aksi Buka Detail
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onClick(tx);
        });

        // Aksi Hapus
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDeleteClick(tx);
        });
    }

    @Override
    public int getItemCount() { return transactionList.size(); }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvResto, tvDate, tvTotal;
        ImageView btnDelete;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResto = itemView.findViewById(R.id.tvHistoryResto);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvTotal = itemView.findViewById(R.id.tvHistoryTotal);
            btnDelete = itemView.findViewById(R.id.btnDeleteHistory);
        }
    }
}