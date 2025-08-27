package com.example.donghua.network;

import com.example.donghua.model.AnimeListResponse; // Import the new wrapper class
import com.example.donghua.model.DetailResponse;
import com.example.donghua.model.VideoSourceResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("anime")
    Call<AnimeListResponse> getAnimeList(); // Change the return type here

    @GET("search/{query}") // Define the search endpoint with a dynamic query
    Call<AnimeListResponse> searchAnime(@Path("query") String query);

    @GET("info/{slug}")
    Call<DetailResponse> getAnimeDetail(@Path("slug") String slug);

    @GET("video-source/{slug}") // Endpoint sesuai dengan backend Anda
    Call<VideoSourceResponse> getVideoSources(@Path("slug") String slug);
}
