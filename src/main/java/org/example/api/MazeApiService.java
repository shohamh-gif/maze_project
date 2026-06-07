package org.example.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

/**
 * מחלקה האחראית על כל התקשורת מול השרת (API).
 * הפרדה זו שומרת על קוד נקי ומסודר לפי עקרונות הנדסת תוכנה.
 */
public class MazeApiService {
    private static final String CONFIG_URL = "https://backend-qcf9.onrender.com/fm1/get-render-config";
    private final OkHttpClient client;

    public MazeApiService() {
        this.client = new OkHttpClient();
    }

    /**
     * פונקציה המבצעת קריאת GET לשרת ומחזירה את נתוני הקונפיגורציה כאובייקט JSON.
     */
    public JSONObject fetchRenderConfig() throws Exception {
        Request request = new Request.Builder()
                .url(CONFIG_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("שגיאה בתגובת השרת: " + response.code());
            }
            String data = response.body().string();
            return new JSONObject(data);
        }
    }
}