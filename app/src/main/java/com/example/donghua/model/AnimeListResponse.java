package com.example.donghua.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AnimeListResponse {
    @SerializedName("results") // *** THIS IS THE KEY CHANGE ***
    private List<Anime> results; // Name of the field in your Java class

    @SerializedName("source") // You can include other fields if you need them
    private String source;

    @SerializedName("total") // You can include other fields if you need them
    private int total;

    // Tambahkan constructor kosong (penting untuk Retrofit dan Gson)
    public AnimeListResponse() {
    }

    public List<Anime> getResults() {
        return results;
    }

    public void setResults(List<Anime> results) {
        this.results = results;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}