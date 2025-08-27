// app/src/main/java/com/example/donghua/model/VideoSource.java
package com.example.donghua.model;

import com.google.gson.annotations.SerializedName;

public class VideoSource {
    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;
    @SerializedName("type")
    private String type;

    public VideoSource() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}