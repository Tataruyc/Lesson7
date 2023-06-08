package ru.mirea.seyfetdinov.r.n.httpurlconnection;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.seyfetdinov.r.n.httpurlconnection.databinding.ActivityMainBinding;

class DownloadPageTask extends AsyncTask<String, Void, String> {

    private ActivityMainBinding binding;


    public DownloadPageTask(ActivityMainBinding binding){
        this.binding = binding;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        binding.InfoText.setText("Загружаем...");
    }
    @Override
    protected String doInBackground(String... urls) {
        try {
            return downloadIpInfo(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        binding.InfoText.setText(result);
        Log.d(MainActivity.class.getSimpleName(), result);
        try {
            JSONObject responseJson = new JSONObject(result);
            Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
            String city = responseJson.getString("city");
            String region = responseJson.getString("region");
            binding.ipInfoText.setText("Город: " + city + " , Регион: " + region);
            String loc = responseJson.getString("loc");
            String[] coordinates = loc.split(",");
            String latitude = coordinates[0];
            String longitude = coordinates[1];
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
            binding.weatherInfo.setText(weatherUrl);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }


    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read;
                byte[] buffer = new byte[1024];
                while ((read = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, read);
                }
                bos.close();
                data = bos.toString();
            } else {
                data = connection.getResponseMessage() + ". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }

}