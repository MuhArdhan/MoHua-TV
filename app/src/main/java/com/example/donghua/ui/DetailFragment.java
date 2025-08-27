// app/src/main/java/com/example/donghua/ui/DetailFragment.java
package com.example.donghua.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.DetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.palette.graphics.Palette;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.donghua.DetailActivity;
import com.example.donghua.PlayerActivity; // Pastikan ini diimpor
import com.example.donghua.R;
import com.example.donghua.model.Anime;
import com.example.donghua.model.DetailResponse;
import com.example.donghua.model.DetailResult;
import com.example.donghua.model.Episode;
import com.example.donghua.network.ApiService;
import com.example.donghua.network.RetrofitClient;
import com.example.donghua.presenter.AnimeCardPresenter;
import com.example.donghua.presenter.EpisodeCardPresenter; // Pastikan ini diimpor

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends DetailsSupportFragment {

    private static final String TAG = "DetailFragment";
    private static final int ACTION_PLAY = 1;
    private String mAnimeSlug;
    private DetailsOverviewRow mDetailsOverviewRow;
    private ArrayObjectAdapter mAdapter;
    private ApiService apiService;
    private FullWidthDetailsOverviewRowPresenter mDetailsPresenter;
    private DetailResult mDetailResult; // Menyimpan objek DetailResult

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Bundle args = getArguments();
        if (args != null) {
            mAnimeSlug = args.getString(DetailActivity.ANIME_SLUG);
        }

        if (mAnimeSlug == null) {
            Log.e(TAG, "Anime slug not provided!");
            getActivity().finish();
            return;
        }

        setupDetailsFragment();
        loadAnimeDetails();
    }

    private void setupDetailsFragment() {
        mDetailsPresenter = new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        mDetailsPresenter.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.default_background));

        ClassPresenterSelector selector = new ClassPresenterSelector();
        selector.addClassPresenter(DetailsOverviewRow.class, mDetailsPresenter);
        selector.addClassPresenter(ListRow.class, new ListRowPresenter());
        // Gunakan EpisodeCardPresenter untuk daftar episode
        selector.addClassPresenter(Episode.class, new EpisodeCardPresenter());

        mAdapter = new ArrayObjectAdapter(selector);
        setAdapter(mAdapter);
    }

    private void loadAnimeDetails() {
        apiService.getAnimeDetail(mAnimeSlug).enqueue(new Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    mDetailResult = response.body().getResult(); // Simpan detail result
                    Log.d(TAG, "Detail fetched for: " + mDetailResult.getName());
                    bindDetails(mDetailResult);
                } else {
                    Log.e(TAG, "Failed to get anime details: " + response.code() + " - " + response.message());
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {
                Log.e(TAG, "API call failed for details: " + t.getMessage(), t);
                getActivity().finish();
            }
        });
    }

    private void bindDetails(DetailResult detail) {
        mDetailsOverviewRow = new DetailsOverviewRow(detail);

        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        if (detail.getEpisodes() != null && !detail.getEpisodes().isEmpty()) {
            actionAdapter.add(new Action(ACTION_PLAY, "Putar Episode Pertama", "")); // Ubah teks menjadi lebih spesifik
        } else {
            actionAdapter.add(new Action(ACTION_PLAY, "Tidak Ada Episode", ""));
        }
        mDetailsOverviewRow.setActionsAdapter(actionAdapter);

        Glide.with(getContext())
                .asBitmap()
                .load(detail.getThumbnail())
                .centerCrop()
                .error(R.drawable.movie)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mDetailsOverviewRow.setImageBitmap(getContext(), resource);
                        Palette.from(resource).generate(palette -> {
                            int primaryColor = palette.getDominantColor(
                                    ContextCompat.getColor(getContext(), R.color.default_background));
                            mDetailsPresenter.setBackgroundColor(primaryColor);
                        });
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        mDetailsOverviewRow.setImageDrawable(errorDrawable);
                    }
                });

        mAdapter.add(mDetailsOverviewRow);

        if (detail.getEpisodes() != null && !detail.getEpisodes().isEmpty()) {
            HeaderItem episodeHeader = new HeaderItem(1, "Episodes");
            ArrayObjectAdapter episodeAdapter = new ArrayObjectAdapter(new EpisodeCardPresenter());
            Collections.reverse(detail.getEpisodes());
            episodeAdapter.addAll(0, detail.getEpisodes());
            mAdapter.add(new ListRow(episodeHeader, episodeAdapter));
        }

        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {
            if (item instanceof Action) {
                Action action = (Action) item;
                if (action.getId() == ACTION_PLAY) {
                    Log.d(TAG, "Tombol 'Putar Episode Pertama' diklik untuk: " + detail.getName());
                    if (mDetailResult != null && mDetailResult.getEpisodes() != null && !mDetailResult.getEpisodes().isEmpty()) {
                        Episode firstEpisode = mDetailResult.getEpisodes().get(0); // Ambil episode pertama (setelah dibalik urutan)
                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                        intent.putExtra(PlayerActivity.VIDEO_SLUG, firstEpisode.getSlug());
                        intent.putExtra(PlayerActivity.VIDEO_TITLE, firstEpisode.getSubtitle());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Tidak ada episode yang tersedia untuk diputar.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (item instanceof Episode) {
                Episode episode = (Episode) item;
                Log.d(TAG, "Episode diklik: " + episode.getSubtitle());
                // --- KODE PEMUTARAN VIDEO UNTUK SETIAP EPISODE ---
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(PlayerActivity.VIDEO_SLUG, episode.getSlug()); // Kirim slug episode
                intent.putExtra(PlayerActivity.VIDEO_TITLE, episode.getSubtitle()); // Kirim judul episode
                startActivity(intent);
                // --- Akhir Kode Pemutaran ---
            }
        });
    }

    private class DetailsDescriptionPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setFocusable(false);
            textView.setFocusableInTouchMode(false);
            textView.setTextColor(ContextCompat.getColor(parent.getContext(), android.R.color.white));
            textView.setMaxLines(Integer.MAX_VALUE);
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            TextView textView = (TextView) viewHolder.view;
            if (item instanceof DetailResult) {
                DetailResult detail = (DetailResult) item;
                StringBuilder description = new StringBuilder();
                description.append("Status: ").append(detail.getStatus()).append("\n");
                description.append("Tipe: ").append(detail.getType()).append("\n");
                description.append("Durasi: ").append(detail.getDuration()).append("\n");
                description.append("Rating: ").append(detail.getRating()).append("\n");
                description.append("Negara: ").append(detail.getCountry()).append("\n");
                description.append("Genre: ").append(String.join(", ", detail.getGenres())).append("\n");
                description.append("Synopsis:\n").append(detail.getSynopsis().isEmpty() ? "No synopsis available." : detail.getSynopsis());
                textView.setText(description.toString());
            }
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
            // Nothing to unbind
        }
    }
}