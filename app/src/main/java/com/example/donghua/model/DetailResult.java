package com.example.donghua.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DetailResult {
    @SerializedName("diperbarui_pada")
    private String updatedAt;
    @SerializedName("diposting_oleh")
    private String postedBy;
    @SerializedName("ditambahkan")
    private String addedDate;
    @SerializedName("durasi")
    private String duration;
    @SerializedName("episode")
    private List<Episode> episodes;
    @SerializedName("genre")
    private List<String> genres;
    @SerializedName("name")
    private String name;
    @SerializedName("negara")
    private String country;
    @SerializedName("network")
    private String network;
    @SerializedName("rating")
    private String rating;
    @SerializedName("season")
    private String season;
    @SerializedName("sinopsis")
    private String synopsis;
    @SerializedName("status")
    private String status;
    @SerializedName("studio")
    private String studio; // Jika ini adalah ID, mungkin perlu tipe data int
    @SerializedName("subber")
    private String subber;
    @SerializedName("tanggal_rilis")
    private String releaseDate;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("tipe")
    private String type;

    public DetailResult() {
    }

    // --- Getters and Setters for all fields ---
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getSubber() {
        return subber;
    }

    public void setSubber(String subber) {
        this.subber = subber;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}