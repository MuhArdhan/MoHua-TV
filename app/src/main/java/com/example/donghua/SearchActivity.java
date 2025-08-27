package com.example.donghua;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.example.donghua.ui.SearchFragment;

public class SearchActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search); // Buat layout ini

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.search_fragment_container, new SearchFragment())
                    .commitNow();
        }
    }
}