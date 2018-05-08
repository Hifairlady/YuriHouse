package com.edgar.yurihouse.Utils;

import android.os.Handler;
import android.os.Message;

import com.edgar.yurihouse.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonGetter extends Thread {

    private String urlString;
    private Handler handler;

    public JsonGetter(String urlString, Handler handler) {
        this.urlString = urlString;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        Message message = handler.obtainMessage();

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            message.what = R.integer.get_data_success;
            message.obj = builder.toString();
            handler.sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
            message.what = R.integer.get_data_failed;
            message.obj = "Failed: IOException";
            handler.sendMessage(message);

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
            message.what = R.integer.get_data_failed;
            message.obj = "Failed: Illegal Url";
            handler.sendMessage(message);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                message.what = R.integer.get_data_failed;
                message.obj = "Failed: IOException";
                handler.sendMessage(message);
            }
        }

    }

}
