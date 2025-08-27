// app/src/main/java/com/example/donghua/PlayerActivity.java
package com.example.donghua;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.example.donghua.ui.VideoPlaybackFragment;

public class PlayerActivity extends FragmentActivity {

    public static final String VIDEO_SLUG = "video_slug";
    public static final String VIDEO_TITLE = "video_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player); // Layout ini perlu ada di res/layout

        if (savedInstanceState == null) {
            String videoSlug = getIntent().getStringExtra(VIDEO_SLUG);
            String videoTitle = getIntent().getStringExtra(VIDEO_TITLE);

            if (videoSlug != null) {
                VideoPlaybackFragment playbackFragment = new VideoPlaybackFragment();
                Bundle args = new Bundle();
                args.putString(VIDEO_SLUG, videoSlug);
                args.putString(VIDEO_TITLE, videoTitle);
                playbackFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.player_fragment_container, playbackFragment)
                        .commit();
            } else {
                finish(); // Tutup aktivitas jika slug tidak diberikan
            }
        }
    }
}