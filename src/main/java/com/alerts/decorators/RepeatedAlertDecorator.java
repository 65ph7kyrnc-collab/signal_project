package com.alerts.decorators;

import com.alerts.Alert;

/** Adds repeated/re-check behaviour metadata to an alert. */
public class RepeatedAlertDecorator extends AlertDecorator {
    private final long repeatIntervalMillis;

    public RepeatedAlertDecorator(Alert wrappedAlert, long repeatIntervalMillis) {
        super(wrappedAlert);
        if (repeatIntervalMillis <= 0) {
            throw new IllegalArgumentException("repeatIntervalMillis must be positive");
        }
        this.repeatIntervalMillis = repeatIntervalMillis;
    }

    @Override
    public boolean isRepeated() {
        return true;
    }

    @Override
    public long getRepeatIntervalMillis() {
        return repeatIntervalMillis;
    }
}