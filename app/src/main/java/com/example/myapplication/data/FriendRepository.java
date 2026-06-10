package com.example.myapplication.data;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendRepository {

    private final FirebaseFirestore db;

    public FriendRepository() {
        // Inisialisasi Firebase di dalam konstruktor
        db = FirebaseFirestore.getInstance();
    }

    // Interface (Callback) untuk mengirim data kembali ke Activity setelah selesai download
    public interface OnFriendsLoadedCallback {
        void onSuccess(List<String> loadedFriends);
        void onError(Exception e);
    }

    // Fungsi OOP untuk mengambil data
    public void loadFriends(OnFriendsLoadedCallback callback) {
        db.collection("friends")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> friends = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            if (name != null) {
                                friends.add(name);
                            }
                        }
                        // Kirim data ke pemanggil (Activity)
                        callback.onSuccess(friends);
                    } else {
                        // Kirim pesan error jika gagal
                        callback.onError(task.getException());
                    }
                });
    }

    // Fungsi OOP untuk menyimpan data
    public void addFriend(String name) {
        HashMap<String, Object> friendData = new HashMap<>();
        friendData.put("name", name);

        db.collection("friends")
                .document(name)
                .set(friendData)
                .addOnSuccessListener(aVoid -> Log.d("FriendRepository", "Nama berhasil disimpan ke Firebase!"))
                .addOnFailureListener(e -> Log.e("FriendRepository", "Gagal menyimpan nama", e));
    }
}