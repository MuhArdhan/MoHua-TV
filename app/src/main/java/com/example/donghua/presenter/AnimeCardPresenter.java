package com.example.donghua.presenter;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.donghua.R; // Pastikan ini sesuai dengan package Anda
import com.example.donghua.model.Anime; // Pastikan ini sesuai dengan package Anda

public class AnimeCardPresenter extends Presenter {

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;
    private static Drawable sDefaultThumbnail;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        if (sDefaultThumbnail == null) {
            // Pastikan Anda memiliki drawable 'movie' atau 'placeholder_image' di res/drawable
            sDefaultThumbnail = ContextCompat.getDrawable(parent.getContext(), R.drawable.movie);
        }

        ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }

    private void updateCardBackgroundColor(ImageCardView cardView, boolean selected) {
        // Pastikan Anda memiliki warna ini di res/values/colors.xml
        int color = selected ? ContextCompat.getColor(cardView.getContext(), R.color.fastlane_background) :
                ContextCompat.getColor(cardView.getContext(), R.color.default_background);
        cardView.setBackgroundColor(color);
        // Jika Anda memiliki info_field di layout ImageCardView Anda, sesuaikan warnanya
        // cardView.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Anime anime = (Anime) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        if (anime.getThumbnail() != null) {
            cardView.setTitleText(anime.getTitle());
            cardView.setContentText(anime.getHeadline());
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
            Glide.with(viewHolder.view.getContext())
                    .load(anime.getThumbnail())
                    .centerCrop()
                    .error(sDefaultThumbnail) // Gambar placeholder jika gagal load
                    .into(cardView.getMainImageView()); // Menggunakan getMainImageView()
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        // Bersihkan gambar saat tidak digunakan dengan mengakses ImageView di dalam CardView
        cardView.getMainImageView().setImageDrawable(sDefaultThumbnail); // PERUBAHAN DI SINI
    }
}
