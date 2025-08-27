package com.example.donghua;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.example.donghua.ui.DetailFragment;

public class DetailActivity extends FragmentActivity {

    public static final String ANIME_SLUG = "anime_slug"; // Konstanta untuk key Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail); // Buat layout ini

        if (savedInstanceState == null) {
            String slug = getIntent().getStringExtra(ANIME_SLUG);
            if (slug != null) {
                DetailFragment detailFragment = new DetailFragment();
                Bundle args = new Bundle();
                args.putString(ANIME_SLUG, slug);
                detailFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_fragment_container, detailFragment)
                        .commitNow();
            } else {
                // Handle case where slug is not passed (e.g., finish activity or show error)
                finish();
            }
        }
    }
}