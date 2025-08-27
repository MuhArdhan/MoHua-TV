// app/src/main/java/com/example/donghua/presenter/EpisodeCardPresenter.java
package com.example.donghua.presenter;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.donghua.R;
import com.example.donghua.model.Episode;

public class EpisodeCardPresenter extends Presenter {

    private static final int CARD_WIDTH = 250;
    private static final int CARD_HEIGHT = 140;
    private static Drawable sDefaultThumbnail;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        if (sDefaultThumbnail == null) {
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

        // Keep this if you want only the image, or remove it if you want default behavior
        // cardView.setCardType(ImageCardView.CARD_TYPE_MAIN_ONLY);

        return new ViewHolder(cardView);
    }

    private void updateCardBackgroundColor(ImageCardView cardView, boolean selected) {
        int color = selected ? ContextCompat.getColor(cardView.getContext(), R.color.fastlane_background) :
                ContextCompat.getColor(cardView.getContext(), R.color.default_background);

        cardView.setBackgroundColor(color); // Sets the background color of the whole card

        // --- THE FIX IS HERE ---
        // Use the public method setInfoAreaBackgroundColor() instead of findViewById()
        cardView.setInfoAreaBackgroundColor(color); // This will set the background of the text area
        // --- End of FIX ---
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Episode episode = (Episode) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        if (episode.getThumbnail() != null) {
            cardView.setTitleText("Episode " + episode.getEpisodeNumber());
            cardView.setContentText(episode.getSubtitle());
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
            Glide.with(viewHolder.view.getContext())
                    .load(episode.getThumbnail())
                    .centerCrop()
                    .error(sDefaultThumbnail)
                    .into(cardView.getMainImageView());
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.getMainImageView().setImageDrawable(sDefaultThumbnail);
    }
}