package com.example.donghua.model;

import com.google.gson.annotations.SerializedName;

public class Episode {
    @SerializedName("date")
    private String date;
    @SerializedName("episode")
    private String episodeNumber; // Mengubah nama field agar tidak konflik dengan keyword
    @SerializedName("slug")
    private String slug;
    @SerializedName("subtitle")
    private String subtitle;
    @SerializedName("thumbnail")
    private String thumbnail;

    public Episode() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}