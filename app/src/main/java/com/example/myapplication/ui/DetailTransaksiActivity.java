package com.example.myapplication.ui;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.TransactionRepository;
import com.example.myapplication.model.Transaction;

import java.util.List;
import java.util.Map;

public class DetailTransaksiActivity extends AppCompatActivity {

    private TransactionRepository repository;
    private Transaction currentTransaction;
    private LinearLayout containerPatungan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        repository = new TransactionRepository();
        containerPatungan = findViewById(R.id.containerDetailPatungan);

        // --- LOGIKA TOMBOL BACK ---
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // Menutup halaman detail

        String transactionId = getIntent().getStringExtra("EXTRA_TXN_ID");
        if (transactionId != null) {
            loadTransactionData(transactionId);
        }
    }

    private void loadTransactionData(String id) {
        repository.getTransactionById(id, tx -> {
            if (tx == null) return;
            currentTransaction = tx;
            ((TextView) findViewById(R.id.tvDetailResto)).setText(tx.getRestaurantName());
            ((TextView) findViewById(R.id.tvDetailDate)).setText(tx.getDate());

            renderCheckboxes();
        }, e -> Toast.makeText(this, "Gagal muat data", Toast.LENGTH_SHORT).show());
    }

    private void renderCheckboxes() {
        containerPatungan.removeAllViews();
        List<Map<String, Object>> participants = currentTransaction.getParticipants();

        if (participants == null || participants.isEmpty()) {
            Toast.makeText(this, "Transaksi lama, tidak ada rincian patungan.", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < participants.size(); i++) {
            Map<String, Object> p = participants.get(i);

            String name = p.get("name") != null ? String.valueOf(p.get("name")) : "Tidak Diketahui";
            long total = p.get("totalFinal") != null ? ((Number) p.get("totalFinal")).longValue() : 0;

            Boolean isPaidObj = (Boolean) p.get("isPaid");
            boolean isPaid = isPaidObj != null ? isPaidObj : false;

            CheckBox cb = new CheckBox(this);
            cb.setTextSize(16f);
            cb.setPadding(0, 0, 0, 16);

            if (name.equals("Pribadi")) {
                cb.setText(name + " - Rp " + total + " (Lunas)");
                cb.setChecked(true);
                cb.setEnabled(false);
            } else {
                cb.setText(name + " - Rp " + total);
                cb.setChecked(isPaid);

                int finalI = i;
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    participants.get(finalI).put("isPaid", isChecked);

                    repository.updateParticipants(currentTransaction.getId(), participants,
                            () -> Toast.makeText(this, name + (isChecked ? " Lunas!" : " Belum Lunas"), Toast.LENGTH_SHORT).show(),
                            e -> {
                                Toast.makeText(this, "Gagal update!", Toast.LENGTH_SHORT).show();
                                cb.setChecked(!isChecked);
                            });
                });
            }

            containerPatungan.addView(cb);
        }
    }
}