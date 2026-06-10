package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageHelper {

    // Method statis agar bisa dipanggil tanpa perlu membuat objek (instansiasi) berkali-kali
    public static String getBase64FromUri(Context context, Uri uri, int maxWidth) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

        if (originalBitmap == null) {
            throw new Exception("Gagal membaca gambar");
        }

        // Mengecilkan resolusi gambar
        float scale = Math.min((float) maxWidth / originalBitmap.getWidth(), (float) maxWidth / originalBitmap.getHeight());
        int newWidth = Math.round(originalBitmap.getWidth() * scale);
        int newHeight = Math.round(originalBitmap.getHeight() * scale);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

        // Kompresi menjadi format JPEG kualitas 80%
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteBuffer);

        return Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP);
    }
}