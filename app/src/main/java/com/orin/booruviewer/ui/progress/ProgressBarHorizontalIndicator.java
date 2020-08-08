package com.orin.booruviewer.ui.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.github.piasy.biv.indicator.ProgressIndicator;
import com.github.piasy.biv.view.BigImageView;
import com.orin.booruviewer.R;

public class ProgressBarHorizontalIndicator implements ProgressIndicator {
    private ProgressBar progressBar;

    @Override
    public View getView(BigImageView parent) {
        progressBar = (ProgressBar) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progressbar, parent, false);
        progressBar.setMax(100);
        return progressBar;
    }

    @Override
    public void onStart() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgress(int progress) {
        if (progress < 0 || progress > 100 || progressBar == null) {
            return;
        }
        progressBar.setProgress(progress);
    }

    @Override
    public void onFinish() {
        progressBar.setVisibility(View.GONE);
    }
}
