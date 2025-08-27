package com.example.donghua.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;
import androidx.leanback.widget.PresenterSelector;

import com.example.donghua.DetailActivity;
import com.example.donghua.HistoryActivity; // Import HistoryActivity
import com.example.donghua.SearchActivity;
import com.example.donghua.R;
import com.example.donghua.model.Anime;
import com.example.donghua.model.AnimeListResponse;
import com.example.donghua.model.ButtonModel; // Import ButtonModel
import com.example.donghua.network.ApiService;
import com.example.donghua.network.RetrofitClient;
import com.example.donghua.presenter.AnimeCardPresenter;
import com.example.donghua.presenter.ButtonPresenter; // Import ButtonPresenter

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

        // Gunakan PresenterSelector untuk memilih presenter yang tepat
        PresenterSelector presenterSelector = new PresenterSelector() {
            private final AnimeCardPresenter animeCardPresenter = new AnimeCardPresenter();
            private final ButtonPresenter buttonPresenter = new ButtonPresenter();

            @Override
            public Presenter getPresenter(Object item) {
                if (item instanceof Anime) {
                    return animeCardPresenter;
                } else if (item instanceof ButtonModel) {
                    return buttonPresenter;
                }
                return null;
            }
        };

        mAdapter = new ArrayObjectAdapter(presenterSelector);
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

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(DetailActivity.ANIME_SLUG, anime.getSlug());
                    startActivity(intent);
                } else if (item instanceof ButtonModel) {
                    Log.d(TAG, "History button clicked!");
                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                    startActivity(intent);
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

                        // Tambahkan tombol riwayat di awal daftar
                        mAdapter.add(new ButtonModel("Riwayat"));

                        // Tambahkan item anime
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