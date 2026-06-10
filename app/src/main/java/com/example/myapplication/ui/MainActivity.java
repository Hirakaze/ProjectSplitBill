package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.data.TransactionRepository;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.ui.adapter.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Transaction> historyList;
    private HistoryAdapter historyAdapter;
    private TransactionRepository transactionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi Repository komponen data
        transactionRepository = new TransactionRepository();

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup RecyclerView Riwayat Transaksi
        historyList = new ArrayList<>();

        // Memanggil Adapter beserta logika tombol hapusnya
        historyAdapter = new HistoryAdapter(historyList,

                // 1. Aksi ketika kotak riwayat ditekan (Buka Halaman Detail)
                transaction -> {
                    Intent intent = new Intent(MainActivity.this, DetailTransaksiActivity.class);
                    intent.putExtra("EXTRA_TXN_ID", transaction.getId());
                    startActivity(intent);
                },

                // 2. Aksi ketika ikon tong sampah ditekan
                transaction -> {
                    new android.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("Hapus Riwayat")
                            .setMessage("Yakin ingin menghapus transaksi " + transaction.getRestaurantName() + "?")
                            .setPositiveButton("Hapus", (dialog, which) -> {
                                transactionRepository.deleteTransaction(transaction.getId(), () -> {
                                    Toast.makeText(MainActivity.this, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                                    fetchHistoryData();
                                }, e -> {
                                    Toast.makeText(MainActivity.this, "Gagal menghapus: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            })
                            .setNegativeButton("Batal", null)
                            .show();
                }
        );

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(historyAdapter);

        // Logika Klik Tombol Pindah Halaman
        binding.tambahTransaksiBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, TambahTransaksiActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Memuat ulang riwayat dari Firebase Firestore setiap kali user kembali ke halaman ini
        fetchHistoryData();
    }

    private void fetchHistoryData() {
        transactionRepository.loadTransactions(new TransactionRepository.OnTransactionsLoadedListener() {
            @Override
            public void onSuccess(List<Transaction> transactions) {
                historyList.clear();
                historyList.addAll(transactions);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e("MainActivity", "Gagal memuat riwayat", e);
                Toast.makeText(MainActivity.this, "Gagal memuat riwayat transaksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}