package com.example.myapplication.data;

import com.example.myapplication.model.Transaction;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    private final FirebaseFirestore db;

    public TransactionRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface OnTransactionsLoadedListener {
        void onSuccess(List<Transaction> transactions);
        void onError(Exception e);
    }

    // Fungsi untuk menyimpan transaksi baru
    public void saveTransaction(Transaction transaction, Runnable onSuccess, java.util.function.Consumer<Exception> onError) {
        db.collection("transactions")
                .document(transaction.getId())
                .set(transaction)
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(onError::accept);
    }

    // Fungsi untuk load transaksi
    public void loadTransactions(OnTransactionsLoadedListener listener) {

        // --- TAMBAHKAN .orderBy DI SINI ---
        db.collection("transactions")
                .orderBy("id", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Transaction> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Transaction tx = doc.toObject(Transaction.class);
                            list.add(tx);
                        }
                        listener.onSuccess(list);
                    } else {
                        listener.onError(task.getException());
                    }
                });
    }

    // Fungsi untuk menghapus transaksi dari Firebase
    public void deleteTransaction(String transactionId, Runnable onSuccess, java.util.function.Consumer<Exception> onError) {
        db.collection("transactions")
                .document(transactionId)
                .delete()
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(onError::accept);
    }

    // Fungsi untuk menarik 1 transaksi spesifik berdasarkan ID
    public void getTransactionById(String id, java.util.function.Consumer<Transaction> onSuccess, java.util.function.Consumer<Exception> onError) {
        db.collection("transactions").document(id).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        onSuccess.accept(doc.toObject(Transaction.class));
                    }
                })
                .addOnFailureListener(onError::accept);
    }

    // Fungsi untuk meng-update status bayar (isPaid) ke awan
    public void updateParticipants(String transactionId, List<java.util.Map<String, Object>> participants, Runnable onSuccess, java.util.function.Consumer<Exception> onError) {
        db.collection("transactions").document(transactionId)
                .update("participants", participants)
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(onError::accept);
    }
}