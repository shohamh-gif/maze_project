package org.example.api;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * מחלקה האחראית על כל התקשורת מול השרת (API).
 * הפרדה זו שומרת על קוד נקי ומסודר לפי עקרונות הנדסת תוכנה.
 */
public class MazeApiService {
    private static final String CONFIG_URL = "https://backend-qcf9.onrender.com/fm1/get-render-config";
    private static final String MAZE_IMAGE_URL = "https://backend-qcf9.onrender.com/fm1/get-maze-image";
    private final OkHttpClient client;

    public MazeApiService() {
        // אתחול לקוח ה-HTTP שיבצע את הבקשות
        this.client = new OkHttpClient();
    }

    //      פונקציה המבצעת קריאת GET לשרת ומחזירה את נתוני הקונפיגורציה (צבעים והגדרות) כאובייקט JSON.
    public JSONObject fetchRenderConfig() throws Exception {
        Request request = new Request.Builder().url(CONFIG_URL).build();

        try (Response response = this.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("שגיאה בתגובת השרת: " + response.code());
            }
            String data = response.body().string();
            return new JSONObject(data);
        }
    }

    /**
     * פונקציה השולחת בקשת GET לשרת עם פרמטרים של רוחב וגובה,
     * וממירה את זרם הנתונים (ByteStream) חזרה לתמונה (BufferedImage).
     */
    public BufferedImage fetchMazeImage(int mazeWidth, int mazeHeight) throws Exception {
        HttpUrl url = HttpUrl.parse(MAZE_IMAGE_URL)
                .newBuilder()
                .addQueryParameter("width", String.valueOf(mazeWidth))
                .addQueryParameter("height", String.valueOf(mazeHeight))
                .build();

        Request request = new Request.Builder().url(url).build();

        try (Response response = this.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("שגיאה בקבלת תמונת המבוך: " + response.code());
            }

            if (response.body() == null) {
                throw new Exception("השרת החזיר תמונה ריקה");
            }

            // קורא את התמונה הישירות מזרם הנתונים שהגיע מהשרת
            return ImageIO.read(response.body().byteStream());
        }
    }
}