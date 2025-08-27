// app/src/main/java/com/example/donghua/model/VideoSourceResponse.java
package com.example.donghua.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideoSourceResponse {
    @SerializedName("sources")
    private List<VideoSource> sources;
    @SerializedName("error")
    private String error;

    public VideoSourceResponse() {
    }

    public List<VideoSource> getSources() {
        return sources;
    }

    public void setSources(List<VideoSource> sources) {
        this.sources = sources;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}