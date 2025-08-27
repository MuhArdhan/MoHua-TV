package com.example.donghua.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

import androidx.leanback.app.SearchSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.example.donghua.DetailActivity; // Import DetailActivity
import com.example.donghua.model.Anime;
import com.example.donghua.model.AnimeListResponse;
import com.example.donghua.network.ApiService;
import com.example.donghua.network.RetrofitClient;
import com.example.donghua.presenter.AnimeCardPresenter;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends SearchSupportFragment implements SearchSupportFragment.SearchResultProvider {

    private static final String TAG = "SearchFragment";
    private static final int REQUEST_SPEECH = 1;
    private ArrayObjectAdapter mRowsAdapter;
    private ApiService apiService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setSearchResultProvider(this);

        setTitle("Donghua");

        setupItemClickListeners();
    }

    private void setupItemClickListeners() {
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                      RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof Anime) {
                    Anime anime = (Anime) item;
                    Log.d(TAG, "Search result clicked: " + anime.getTitle());

                    // --- NEW: Launch DetailActivity ---
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(DetailActivity.ANIME_SLUG, anime.getSlug()); // Kirim slug
                    startActivity(intent);
                    // --- End NEW ---
                }
            }
        });
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        Log.d(TAG, "onQueryTextChange: " + newQuery);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit: " + query);
        loadSearchResults(query);
        return true;
    }

    private void loadSearchResults(String query) {
        if (query == null || query.isEmpty()) {
            mRowsAdapter.clear();
            return;
        }

        apiService.searchAnime(query).enqueue(new Callback<AnimeListResponse>() {
            @Override
            public void onResponse(Call<AnimeListResponse> call, Response<AnimeListResponse> response) {
                mRowsAdapter.clear();

                if (response.isSuccessful() && response.body() != null) {
                    List<Anime> searchResults = response.body().getResults();
                    if (searchResults != null && !searchResults.isEmpty()) {
                        Log.d(TAG, "Search results found: " + searchResults.size());

                        HeaderItem header = new HeaderItem(0, "Search Results for \"" + query + "\"");
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new AnimeCardPresenter());
                        listRowAdapter.addAll(0, searchResults);

                        mRowsAdapter.add(new ListRow(header, listRowAdapter));
                    } else {
                        Log.w(TAG, "No search results for query: " + query);
                        HeaderItem header = new HeaderItem(0, "No results found for \"" + query + "\"");
                        mRowsAdapter.add(new ListRow(header, new ArrayObjectAdapter(new AnimeCardPresenter())));
                    }
                } else {
                    Log.e(TAG, "Search API failed: " + response.code() + " - " + response.message());
                    HeaderItem header = new HeaderItem(0, "Error loading search results.");
                    mRowsAdapter.add(new ListRow(header, new ArrayObjectAdapter(new AnimeCardPresenter())));
                }
            }

            @Override
            public void onFailure(Call<AnimeListResponse> call, Throwable t) {
                Log.e(TAG, "Search API call failed: " + t.getMessage(), t);
                HeaderItem header = new HeaderItem(0, "Network error. Please try again.");
                mRowsAdapter.add(new ListRow(header, new ArrayObjectAdapter(new AnimeCardPresenter())));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SPEECH && resultCode == Activity.RESULT_OK && null != data) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String searchText = results.get(0);
            setSearchQuery(searchText, true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}