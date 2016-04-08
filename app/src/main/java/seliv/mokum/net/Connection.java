package seliv.mokum.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aselivanov on 2/5/2016.
 */
public class Connection {
    private String token;

    public Connection(String token) {
        this.token = token;
    }

    public JsonWithCode doGet(String urlString) throws IOException {
        HttpURLConnection urlConnection = initUrlConnection(urlString);
        return getJsonWithCode(urlConnection);
    }

    public JsonWithCode doPost(String urlString) throws IOException {
        return doPost(urlString, null);
    }

    public JsonWithCode doPost(String urlString, JSONObject jsonObject) throws IOException {
        HttpURLConnection urlConnection = initUrlConnection(urlString);
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        if (jsonObject != null) {
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
        }

        return getJsonWithCode(urlConnection);
    }

    public JsonWithCode doDelete(String urlString) throws IOException {
        HttpURLConnection urlConnection = initUrlConnection(urlString);
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty("X-API-Token", token);
        urlConnection.setRequestProperty("Accept", "application/json");

        return getJsonWithCode(urlConnection);
    }

    private HttpURLConnection initUrlConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(10000);
        urlConnection.setRequestProperty("X-API-Token", token);
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        return urlConnection;
    }

    private JsonWithCode getJsonWithCode(HttpURLConnection urlConnection) throws IOException {
        int responseCode = urlConnection.getResponseCode();
        InputStream is;
        try {
            urlConnection.connect();
            is = urlConnection.getInputStream();
        } catch (IOException ex) {
            is = urlConnection.getErrorStream();
        }

        String result;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            result = sb.toString();
        } catch (IOException e) {
            result = "{error:\"IOException: " + e.getMessage() + "\"}";
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            jsonObject = null;
        }
        return new JsonWithCode(jsonObject, responseCode);
    }
}
