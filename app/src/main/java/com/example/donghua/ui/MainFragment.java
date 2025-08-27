package com.example.donghua.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import com.example.donghua.DetailActivity; // Import DetailActivity
import com.example.donghua.SearchActivity;
import com.example.donghua.R;
import com.example.donghua.model.Anime;
import com.example.donghua.model.AnimeListResponse;
import com.example.donghua.network.ApiService;
import com.example.donghua.network.RetrofitClient;
import com.example.donghua.presenter.AnimeCardPresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends VerticalGridSupportFragment {
    private static final String TAG = "MainFragment";
    private ArrayObjectAdapter mAdapter;
    private static final int COLUMNS = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Donghua List");

        setSearchAffordanceColor(getResources().getColor(R.color.search_button_color));
        setOnSearchClickedListener(view -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        setupAdapter();
        setupEventListeners();
        loadAnimeData();
    }

    private void setupAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new AnimeCardPresenter());
        setAdapter(mAdapter);
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                      RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof Anime) {
                    Anime anime = (Anime) item;
                    Log.d(TAG, "Item clicked: " + anime.getTitle());

                    // --- NEW: Launch DetailActivity ---
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(DetailActivity.ANIME_SLUG, anime.getSlug()); // Kirim slug
                    startActivity(intent);
                    // --- End NEW ---
                }
            }
        });
    }

    private void loadAnimeData() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<AnimeListResponse> call = apiService.getAnimeList();

        call.enqueue(new Callback<AnimeListResponse>() {
            @Override
            public void onResponse(Call<AnimeListResponse> call, Response<AnimeListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Anime> animeList = response.body().getResults();
                    if (animeList != null && !animeList.isEmpty()) {
                        Log.d(TAG, "API Response successful! Number of items: " + animeList.size());
                        mAdapter.clear();
                        for (Anime anime : animeList) {
                            mAdapter.add(anime);
                        }
                    } else {
                        Log.w(TAG, "API Response successful, but 'results' list is null or empty.");
                    }
                } else {
                    Log.e(TAG, "Failed to get anime list: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AnimeListResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }
}