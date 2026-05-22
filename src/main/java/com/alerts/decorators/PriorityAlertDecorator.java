package com.alerts.decorators;

import com.alerts.Alert;

/** Adds an urgent priority label to an alert without changing the wrapped alert class. */
public class PriorityAlertDecorator extends AlertDecorator {
    private final String priority;

    public PriorityAlertDecorator(Alert wrappedAlert) {
        this(wrappedAlert, "HIGH");
    }

    public PriorityAlertDecorator(Alert wrappedAlert, String priority) {
        super(wrappedAlert);
        this.priority = priority;
    }

    @Override
    public String getPriority() {
        return priority;
    }
}