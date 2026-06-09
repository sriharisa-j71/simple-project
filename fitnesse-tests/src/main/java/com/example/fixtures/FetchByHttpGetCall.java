package com.example.fixtures;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FetchByHttpGetCall {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public int statusCode() {
        var request = new Request.Builder().url(url).get().build();
        try (var response = client.newCall(request).execute()) {
            return response.code();
        } catch (IOException e) {
            throw new RuntimeException("HTTP GET failed: " + url, e);
        }
    }

    public String responseBody() {
        var request = new Request.Builder().url(url).get().build();
        try (var response = client.newCall(request).execute()) {
            if (response.body() == null) return "";
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("HTTP GET failed: " + url, e);
        }
    }
}
