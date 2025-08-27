// app/src/main/java/com/example/donghua/ui/VideoPlaybackFragment.java
package com.example.donghua.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment; // Mengubah dari VideoSupportFragment ke Fragment biasa

import com.example.donghua.PlayerActivity;
import com.example.donghua.R;
import com.example.donghua.model.VideoSource;
import com.example.donghua.model.VideoSourceResponse;
import com.example.donghua.network.ApiService;
import com.example.donghua.network.RetrofitClient;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;

import java.util.List;

// Mengubah dari VideoSupportFragment ke Fragment biasa
public class VideoPlaybackFragment extends Fragment {

    private static final String TAG = "VideoPlaybackFragment";
    private String mVideoSlug;
    private String mVideoTitle;
    private ApiService apiService;

    // Untuk WebView
    private WebView mWebView;
    private FrameLayout mWebViewContainer; // Container yang akan menampung WebView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Karena kita tidak lagi menggunakan VideoSupportFragment, kita perlu menginflasi layout sendiri.
        // Asumsikan kita akan menggunakan activity_player.xml sebagai layout dasar fragment ini.
        // ATAU Anda bisa membuat layout baru khusus untuk fragment ini jika diinginkan.
        // Untuk saat ini, kita akan mengasumsikan activity_player.xml masih berfungsi sebagai kontainer utama
        // dan kita akan mencari webview_container di dalamnya.

        // Jika Anda ingin fragment ini punya layout sendiri (lebih disarankan):
        // return inflater.inflate(R.layout.fragment_video_playback, container, false);
        // Dimana fragment_video_playback.xml berisi FrameLayout dengan id webview_container

        // Jika Anda masih ingin mencari webview_container dari Activity's root view:
        return inflater.inflate(R.layout.activity_player, container, false); // Inflate layout activity_player
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cari FrameLayout untuk WebView dari root view fragment ini (atau Activity, tergantung desain Anda)
        mWebViewContainer = view.findViewById(R.id.webview_container); // Mencari di dalam view yang diinflate di onCreateView

        if (mWebViewContainer == null) {
            Log.e(TAG, "webview_container not found in the fragment's layout. Please ensure it exists.");
            Toast.makeText(getActivity(), "Kesalahan: Container WebView tidak ditemukan.", Toast.LENGTH_LONG).show();
            getActivity().finish();
            return;
        }

        mWebViewContainer.setVisibility(View.GONE); // Defaultnya sembunyikan

        // Inisialisasi WebView
        mWebView = new WebView(getContext());
        mWebView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false); // Memungkinkan autoplay
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Tambahkan WebChromeClient untuk mendukung fullscreen video dan dialog JavaScript
        mWebView.setWebChromeClient(new WebChromeClient() {
            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mCustomView = view;
                mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                mOriginalOrientation = getActivity().getRequestedOrientation();
                mCustomViewCallback = callback;

                // Tambahkan custom view ke dekor view agar bisa fullscreen
                ((FrameLayout)getActivity().getWindow().getDecorView()).addView(mCustomView,
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                getActivity().getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (mCustomView == null) {
                    return;
                }
                ((FrameLayout)getActivity().getWindow().getDecorView()).removeView(mCustomView);
                mCustomView = null;
                getActivity().getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
                getActivity().setRequestedOrientation(mOriginalOrientation);
                mCustomViewCallback.onCustomViewHidden();

                // Setelah fullscreen selesai, pastikan WebView tetap terlihat
                mWebViewContainer.setVisibility(View.VISIBLE);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "WebView finished loading: " + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "WebView Error: " + description + " at " + failingUrl);
                Toast.makeText(getContext(), "Gagal memuat halaman embed: " + description, Toast.LENGTH_LONG).show();
            }
        });

        // Tambahkan WebView ke container di activity_player.xml
        if (mWebView.getParent() == null) {
            mWebViewContainer.addView(mWebView);
        }

        loadVideoSource(mVideoSlug);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Bundle args = getArguments();
        if (args != null) {
            mVideoSlug = args.getString(PlayerActivity.VIDEO_SLUG);
            mVideoTitle = args.getString(PlayerActivity.VIDEO_TITLE);
        }

        if (mVideoSlug == null) {
            Toast.makeText(getActivity(), "Video slug not provided.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        // Tidak ada inisialisasi ExoPlayer di sini lagi
    }


    private void loadVideoSource(String slug) {
        apiService.getVideoSources(slug).enqueue(new Callback<VideoSourceResponse>() {
            @Override
            public void onResponse(Call<VideoSourceResponse> call, Response<VideoSourceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VideoSource> sources = response.body().getSources();
                    String error = response.body().getError();

                    if (error != null && !error.isEmpty()) {
                        Log.e(TAG, "Backend returned error: " + error);
                        Toast.makeText(getActivity(), "Error from server: " + error, Toast.LENGTH_LONG).show();
                        getActivity().finish();
                        return;
                    }

                    if (sources != null && !sources.isEmpty()) {
                        VideoSource playableSource = null;
                        for (VideoSource source : sources) {
                            if ("iframe_embed".equals(source.getType())) { // Hanya cari iframe_embed
                                playableSource = source;
                                break;
                            }
                            // Hapus logika pencarian .mp4/.m3u8 karena hanya WebView yang digunakan
                        }

                        if (playableSource != null) {
                            Log.d(TAG, "Loading embed video from URL: " + playableSource.getUrl());
                            loadEmbedVideo(playableSource.getUrl());
                        } else {
                            Log.w(TAG, "No playable embed video source found for slug: " + slug);
                            Toast.makeText(getActivity(), "Tidak ada sumber video embed yang dapat dimainkan ditemukan.", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }

                    } else {
                        Log.w(TAG, "No video sources array found or it's empty for slug: " + slug);
                        Toast.makeText(getActivity(), "Tidak ada sumber video ditemukan untuk episode ini.", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                } else {
                    Log.e(TAG, "Failed to get video sources: " + response.code() + " - " + response.message());
                    Toast.makeText(getActivity(), "Gagal mendapatkan sumber video dari API.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<VideoSourceResponse> call, Throwable t) {
                Log.e(TAG, "API call for video source failed: " + t.getMessage(), t);
                Toast.makeText(getActivity(), "Kesalahan jaringan saat mengambil sumber video.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    private void loadEmbedVideo(String embedUrl) {
        // Pastikan tampilan root fragment ini disembunyikan jika kita menggunakan container terpisah
        // atau pastikan WebViewContainer terlihat dan menutupi semua.
        // Jika fragment ini sendiri hanya berisi WebViewContainer, maka ini tidak perlu.
        // Karena di onCreateView kita menginflate activity_player,
        // maka getView() adalah FrameLayout yang sama dengan player_fragment_container.
        // Cukup pastikan webview_container VISIBLE.

        if (mWebViewContainer != null) {
            mWebViewContainer.setVisibility(View.VISIBLE); // Tampilkan container WebView
            mWebView.loadUrl(embedUrl);
            mWebView.requestFocus();
        } else {
            Log.e(TAG, "WebView container is null, cannot load embed video.");
            Toast.makeText(getActivity(), "Kesalahan: Container WebView tidak siap.", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.loadUrl("about:blank");
            mWebView.clearHistory();
            if (mWebView.getParent() != null) {
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            }
            mWebView.destroy();
            mWebView = null;
        }
        // Tidak ada pelepasan ExoPlayer
    }
}