package com.mapnamind.data.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SignalSelectorView implements Serializable {

    private List<SignalView> signals;
    private List<ValueView> values;
    private String startDate;
    private String endDate;

    public List<SignalView> getSignals() {
        return signals;
    }

    public void setSignals(List<SignalView> signals) {
        this.signals = signals;
    }

    public List<ValueView> getValues() {
        return values;
    }

    public void setValues(List<ValueView> values) {
        this.values = values;
    }

    public void addValue(ValueView value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        this.values.add(value);
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
