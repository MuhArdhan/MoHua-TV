package com.example.donghua.presenter;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.leanback.widget.Presenter;
import com.example.donghua.R;
import com.example.donghua.model.ButtonModel;

public class ButtonPresenter extends Presenter {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView view = (TextView) getLayoutInflater().inflate(R.layout.button_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ButtonModel buttonModel = (ButtonModel) item;
        TextView textView = (TextView) viewHolder.view;
        textView.setText(buttonModel.getTitle());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        // Kosong
    }
}