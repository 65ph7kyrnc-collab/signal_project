package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Base decorator that wraps another Alert while preserving the Alert interface.
 */
public abstract class AlertDecorator implements Alert {
    private final Alert wrappedAlert;

    protected AlertDecorator(Alert wrappedAlert) {
        if (wrappedAlert == null) {
            throw new IllegalArgumentException("wrappedAlert must not be null");
        }
        this.wrappedAlert = wrappedAlert;
    }

    protected Alert getWrappedAlert() {
        return wrappedAlert;
    }

    @Override
    public String getPatientId() {
        return wrappedAlert.getPatientId();
    }

    @Override
    public String getCondition() {
        return wrappedAlert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return wrappedAlert.getTimestamp();
    }

    @Override
    public String getAlertType() {
        return wrappedAlert.getAlertType();
    }

    @Override
    public String getPriority() {
        return wrappedAlert.getPriority();
    }

    @Override
    public boolean isRepeated() {
        return wrappedAlert.isRepeated();
    }

    @Override
    public long getRepeatIntervalMillis() {
        return wrappedAlert.getRepeatIntervalMillis();
    }
}