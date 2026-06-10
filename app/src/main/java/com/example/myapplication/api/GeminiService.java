package com.example.myapplication.api;
import com.example.myapplication.BuildConfig;

import org.json.JSONObject;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class GeminiService {

    // 2. DEKLARASI API KEY & URL
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    private final OkHttpClient client;

    // Interface untuk mengembalikan hasil ke Activity
    public interface GeminiCallback {
        void onSuccess(String extractedText);
        void onError(String errorMessage);
    }

    public GeminiService() {
        // Konfigurasi OkHttp dilakukan saat objek GeminiService dibuat
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
    }

    public void analyzeReceipt(String base64Image, GeminiCallback callback) {
        new Thread(() -> {
            try {
                JSONObject inlineData = new JSONObject();
                inlineData.put("mime_type", "image/jpeg");
                inlineData.put("data", base64Image);

                JSONObject partImage = new JSONObject();
                partImage.put("inline_data", inlineData);

                // PROMPT: Minta JSON Object dengan info Header
                JSONObject partText = new JSONObject();
                partText.put("text", "Ekstrak SELURUH informasi dari struk ini tanpa terkecuali. Pastikan SEMUA BARANG yang dibeli masuk ke dalam array 'items'. Jangan ada yang terlewat atau diringkas! " +
                        "Berikan hasil akhir dalam format JSON RAW saja tanpa markdown (tanpa ```json ). " +
                        "Format JSON harus berupa Object seperti ini:\n" +
                        "{\n" +
                        "  \"restaurant_name\": \"Nama Restoran / Toko\",\n" +
                        "  \"date\": \"Tanggal (DD-MM-YYYY)\",\n" +
                        "  \"tax\": 15000,\n" +
                        "  \"service_charge\": 5000,\n" +
                        "  \"discount\": 10000,\n" +
                        "  \"packaging_fee\": 2000,\n" +
                        "  \"delivery_fee\": 12000,\n" +
                        "  \"total\": 120000,\n" +
                        "  \"items\": [\n" +
                        "    {\"name\": \"Nama Barang 1\", \"price\": 50000},\n" +
                        "    {\"name\": \"Nama Barang 2\", \"price\": 50000}\n" +
                        "  ]\n" +
                        "}\n" +
                        "Jika ada elemen biaya yang tidak tertera (seperti pajak, diskon, kemasan, atau ongkir), isi dengan 0.");

                org.json.JSONArray partsArray = new org.json.JSONArray();
                partsArray.put(partText);
                partsArray.put(partImage);

                JSONObject content = new JSONObject();
                content.put("parts", partsArray);

                org.json.JSONArray contentsArray = new org.json.JSONArray();
                contentsArray.put(content);

                JSONObject requestBodyJson = new JSONObject();
                requestBodyJson.put("contents", contentsArray);

                okhttp3.MediaType JSON = okhttp3.MediaType.get("application/json; charset=utf-8");
                okhttp3.RequestBody body = okhttp3.RequestBody.create(requestBodyJson.toString(), JSON);

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URL)
                        .post(body)
                        .header("Content-Type", "application/json")
                        .build();

                okhttp3.Response response = client.newCall(request).execute();
                String responseData = response.body() != null ? response.body().string() : "";

                if (!response.isSuccessful()) {
                    callback.onError("Server Error: " + response.code());
                    return;
                }

                JSONObject jsonResponse = new JSONObject(responseData);
                org.json.JSONArray candidates = jsonResponse.getJSONArray("candidates");
                JSONObject firstCandidate = candidates.getJSONObject(0);
                JSONObject contentObj = firstCandidate.getJSONObject("content");
                org.json.JSONArray parts = contentObj.getJSONArray("parts");

                String extractedText = parts.getJSONObject(0).getString("text");
                callback.onSuccess(extractedText);

            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(e.getMessage());
            }
        }).start();
    }
}