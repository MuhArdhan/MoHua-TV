package com.example.donghua.model;

import com.google.gson.annotations.SerializedName;

public class DetailResponse {
    @SerializedName("result")
    private DetailResult result;
    @SerializedName("source")
    private String source;

    public DetailResponse() {
    }

    public DetailResult getResult() {
        return result;
    }

    public void setResult(DetailResult result) {
        this.result = result;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}