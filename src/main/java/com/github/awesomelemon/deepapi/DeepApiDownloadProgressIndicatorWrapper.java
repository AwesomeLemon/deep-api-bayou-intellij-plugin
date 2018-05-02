package com.github.awesomelemon.deepapi;

import com.intellij.openapi.progress.ProgressIndicator;

public class DeepApiDownloadProgressIndicatorWrapper {
    ProgressIndicator progressIndicator;
    double fraction = 0.0;

    public DeepApiDownloadProgressIndicatorWrapper(ProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
        progressIndicator.setText("Downloading DeepAPI model");
        progressIndicator.setFraction(0.0);
    }

    public void setFraction(double fraction) {
        this.fraction = fraction;
        progressIndicator.setFraction(fraction);
    }
}
