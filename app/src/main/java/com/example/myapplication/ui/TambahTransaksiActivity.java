package com.example.myapplication.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.FriendRepository;
import com.example.myapplication.api.GeminiService;
import com.example.myapplication.ImageHelper;
import com.example.myapplication.model.PersonSummary;
import com.example.myapplication.R;
import com.example.myapplication.model.ReceiptItem;
import com.example.myapplication.data.SplitBillCalculator;
import com.example.myapplication.databinding.ActivityTambahTransaksiBinding;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.data.TransactionRepository;
import com.example.myapplication.ui.adapter.ChoosenAdapter;
import com.example.myapplication.ui.adapter.ReceiptItemAdapter;
import com.example.myapplication.ui.adapter.SearchAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TambahTransaksiActivity extends AppCompatActivity {

    private ActivityTambahTransaksiBinding binding;
    private List<String> searchList;
    private List<String> choosenList;
    private SearchAdapter searchAdapter;
    private ChoosenAdapter choosenAdapter;
    private Button BtnUploadImage;

    // OOP Services / Repositories
    private GeminiService geminiService;
    private FriendRepository friendRepository;

    // Variabel untuk Item Struk
    private List<ReceiptItem> receiptItemList;
    private ReceiptItemAdapter receiptItemAdapter;

    // Variabel Global untuk Kalkulasi dan Simpan Transaksi
    private String globalRestoName = "Restoran Tidak Diketahui";
    private String globalDate = "Tanggal Tidak Terbaca";
    private long globalTotal = 0;
    private long globalTax = 0;
    private long globalService = 0;
    private long globalDiscount = 0;
    private long globalPackaging = 0;
    private long globalDelivery = 0;

    private List<PersonSummary> globalHasilAkhir = new ArrayList<>();

    // Tombol Aksi
    private Button btnHitungPatungan;
    private Button btnSimpanTransaksi;

    // Variabel penampung status Checkbox dari layar
    private List<android.widget.CheckBox> listCheckboxOrang = new ArrayList<>();

    private void extractDataFromReceipt(android.net.Uri uri) {
        try {
            String base64Image = ImageHelper.getBase64FromUri(this, uri, 1024);

            geminiService.analyzeReceipt(base64Image, new GeminiService.GeminiCallback() {

                @Override
                public void onSuccess(String extractedText) {
                    runOnUiThread(() -> {
                        try {
                            Toast.makeText(TambahTransaksiActivity.this, "Ekstraksi Berhasil!", Toast.LENGTH_SHORT).show();
                            receiptItemList.clear();

                            // 1. Parse JSON ke Variabel Global
                            org.json.JSONObject receiptData = new org.json.JSONObject(extractedText);
                            globalRestoName = receiptData.optString("restaurant_name", "Restoran Tidak Diketahui");
                            globalDate = receiptData.optString("date", "Tanggal Tidak Terbaca");
                            globalTotal = receiptData.optLong("total", 0);

                            globalTax = receiptData.optLong("tax", 0);
                            globalService = receiptData.optLong("service_charge", 0);
                            globalDiscount = receiptData.optLong("discount", 0);
                            globalPackaging = receiptData.optLong("packaging_fee", 0);
                            globalDelivery = receiptData.optLong("delivery_fee", 0);

                            // 2. Set UI
                            LinearLayout layoutReceiptInfo = findViewById(R.id.layoutReceiptInfo);
                            ((TextView) findViewById(R.id.tvRestoName)).setText(globalRestoName);
                            ((TextView) findViewById(R.id.tvReceiptDate)).setText("Tanggal: " + globalDate);
                            ((TextView) findViewById(R.id.tvTax)).setText("Tax: Rp " + globalTax);
                            ((TextView) findViewById(R.id.tvService)).setText("Service: Rp " + globalService);
                            ((TextView) findViewById(R.id.tvGrandTotal)).setText("Grand Total: Rp " + globalTotal);
                            ((TextView) findViewById(R.id.tvDiscount)).setText("Diskon: -Rp " + globalDiscount);
                            ((TextView) findViewById(R.id.tvPackaging)).setText("Kemasan: Rp " + globalPackaging);
                            ((TextView) findViewById(R.id.tvDelivery)).setText("Ongkir: Rp " + globalDelivery);

                            layoutReceiptInfo.setVisibility(View.VISIBLE);
                            btnHitungPatungan.setVisibility(View.VISIBLE);

                            // 3. Parse Array
                            org.json.JSONArray jsonArray = receiptData.getJSONArray("items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                org.json.JSONObject obj = jsonArray.getJSONObject(i);
                                receiptItemList.add(new ReceiptItem(obj.getString("name"), obj.getLong("price")));
                            }

                            receiptItemAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TambahTransaksiActivity.this, "Gagal memparse data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(TambahTransaksiActivity.this, "Gagal: " + errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memproses gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Toast.makeText(this, "Menganalisis Struk...", Toast.LENGTH_SHORT).show();
                    extractDataFromReceipt(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTambahTransaksiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi Objek
        geminiService = new GeminiService();
        friendRepository = new FriendRepository();

        binding.main.setOnClickListener(v -> binding.searchView.clearFocus());
        binding.backButton.setOnClickListener(view -> finish());

        // Inisialisasi List
        searchList = new ArrayList<>();
        choosenList = new ArrayList<>();
        choosenList.add("Pribadi");

        // Load data menggunakan Repository
        fetchFriendsData();

        // Setup Adapters
        receiptItemList = new ArrayList<>();
        receiptItemAdapter = new ReceiptItemAdapter(receiptItemList, choosenList);
        RecyclerView rvReceiptItems = findViewById(R.id.recyclerViewReceiptItems);
        rvReceiptItems.setLayoutManager(new LinearLayoutManager(this));
        rvReceiptItems.setAdapter(receiptItemAdapter);

        binding.recyclerViewChoosen.setLayoutManager(new LinearLayoutManager(this));
        choosenAdapter = new ChoosenAdapter(choosenList, position -> {
            choosenList.remove(position);
            choosenAdapter.notifyItemRemoved(position);
            choosenAdapter.notifyItemRangeRemoved(position, choosenList.size());
            receiptItemAdapter.notifyDataSetChanged();
        });
        binding.recyclerViewChoosen.setAdapter(choosenAdapter);

        binding.friendViewHolder.setLayoutManager(new LinearLayoutManager(this));
        setupSearchAdapter();

        // Search View Logic
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String search) {
                searchAdapter.dataFilter(search);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                String newName = query.trim();

                if (!newName.isEmpty()) {
                    if (!choosenList.contains(newName)) {
                        choosenList.add(newName);
                        choosenAdapter.notifyItemInserted(choosenList.size() - 1);

                        if (!searchList.contains(newName)) {
                            searchList.add(newName);
                            friendRepository.addFriend(newName);
                            setupSearchAdapter();
                        }

                        if (receiptItemAdapter != null) receiptItemAdapter.notifyDataSetChanged();
                        Toast.makeText(TambahTransaksiActivity.this, newName + " ditambahkan!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TambahTransaksiActivity.this, newName + " sudah ada.", Toast.LENGTH_SHORT).show();
                    }

                    binding.searchView.setQuery("", false);
                    binding.searchView.clearFocus();
                    binding.friendViewHolder.setVisibility(View.GONE);
                }
                return true;
            }
        });

        binding.searchView.setOnQueryTextFocusChangeListener((view, hasFocus) ->
                binding.friendViewHolder.setVisibility(hasFocus ? View.VISIBLE : View.GONE));

        // Tombol Upload Image
        BtnUploadImage = findViewById(R.id.BtnUploadImage);
        BtnUploadImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));

        // Tombol Hitung Patungan
        btnHitungPatungan = findViewById(R.id.btnHitungPatungan);
        btnHitungPatungan.setOnClickListener(v -> hitungPatunganFinal());

        // --- TOMBOL SIMPAN TRANSAKSI KE FIREBASE ---
        btnSimpanTransaksi = findViewById(R.id.btnSimpanTransaksi);
        btnSimpanTransaksi.setOnClickListener(v -> {
            if (globalHasilAkhir.isEmpty()) {
                Toast.makeText(this, "Hitung patungan terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            String txnId = "txn_" + System.currentTimeMillis();

            List<Map<String, Object>> participantsMapList = new ArrayList<>();

            // Gunakan index (i) agar bisa membaca data orang sekaligus membaca UI Checkbox-nya
            for (int i = 0; i < globalHasilAkhir.size(); i++) {
                PersonSummary p = globalHasilAkhir.get(i);
                boolean statusCentangDariLayar = listCheckboxOrang.get(i).isChecked();

                Map<String, Object> participantData = new HashMap<>();
                participantData.put("name", p.getName());
                participantData.put("totalFinal", Math.round(p.getGrandTotal()));

                // Jika Pribadi otomatis true, selain itu ikuti centangan di layar
                participantData.put("isPaid", p.getName().equals("Pribadi") ? true : statusCentangDariLayar);

                participantsMapList.add(participantData);
            }

            Transaction transaksiBaru = new Transaction(
                    txnId,
                    globalRestoName,
                    globalDate,
                    globalTotal,
                    participantsMapList
            );

            TransactionRepository txRepo = new TransactionRepository();
            txRepo.saveTransaction(transaksiBaru, () -> {
                Toast.makeText(TambahTransaksiActivity.this, "Transaksi Berhasil Disimpan!", Toast.LENGTH_SHORT).show();
                finish();
            }, e -> {
                Toast.makeText(TambahTransaksiActivity.this, "Gagal Menyimpan: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });
    }

    private void setupSearchAdapter() {
        searchAdapter = new SearchAdapter(searchList, friend -> {
            if (!choosenList.contains(friend)) {
                choosenList.add(friend);
                choosenAdapter.notifyItemInserted(choosenList.size() - 1);
                if (receiptItemAdapter != null) receiptItemAdapter.notifyDataSetChanged();
            }
            binding.searchView.setQuery("", false);
            binding.searchView.clearFocus();
            binding.friendViewHolder.setVisibility(View.GONE);
        });
        binding.friendViewHolder.setAdapter(searchAdapter);
    }

    private void fetchFriendsData() {
        friendRepository.loadFriends(new FriendRepository.OnFriendsLoadedCallback() {
            @Override
            public void onSuccess(List<String> loadedFriends) {
                searchList.clear();
                searchList.addAll(loadedFriends);
                setupSearchAdapter();
            }

            @Override
            public void onError(Exception e) {
                Log.e("TambahTransaksi", "Error muat teman", e);
            }
        });
    }

    private void hitungPatunganFinal() {
        if (receiptItemList.isEmpty()) {
            Toast.makeText(this, "Tidak ada item untuk dihitung", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan hasil kalkulasi ke Variabel Global
        globalHasilAkhir = SplitBillCalculator.calculateSplitBill(
                receiptItemList, choosenList, globalTax, globalService, globalDiscount, globalPackaging, globalDelivery
        );

        View layoutHasil = findViewById(R.id.layoutHasilPatungan);
        LinearLayout containerListHasil = findViewById(R.id.containerListHasil);
        // Bersihkan hasil sebelumnya
        containerListHasil.removeAllViews();
        listCheckboxOrang.clear(); // Bersihkan memori list UI checkbox

        // Loop untuk menampilkan UI dari hasil perhitungan global
        for (PersonSummary person : globalHasilAkhir) {
            android.widget.CheckBox cb = new android.widget.CheckBox(this);
            String detailText = person.getName() +
                    "\nBelanja: Rp " + Math.round(person.getBelanja()) +
                    "\nBiaya Ekstra/Diskon: Rp " + Math.round(person.getExtraFee()) +
                    "\nTotal Final: Rp " + Math.round(person.getGrandTotal()) + "\n";

            cb.setText(detailText);
            cb.setTextSize(15f);
            cb.setPadding(0, 0, 0, 16);

            // LOGIKA KHUSUS "PRIBADI"
            if (person.getName().equals("Pribadi")) {
                cb.setChecked(true); // Otomatis dicentang
                cb.setEnabled(false); // Dikunci agar tidak bisa diubah-ubah
            }

            containerListHasil.addView(cb);
            listCheckboxOrang.add(cb); // Simpan objek Checkbox ke list agar bisa dibaca saat klik simpan
        }

        layoutHasil.setVisibility(View.VISIBLE);
    }
}