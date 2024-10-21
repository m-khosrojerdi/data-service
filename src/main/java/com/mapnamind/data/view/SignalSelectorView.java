package com.mapnamind.data.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignalSelectorView implements Serializable {

    private List<SignalView> signals;
    private List<ValueView> values;
    private String startDate;
    private String endDate;

    public void addValue(ValueView value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        this.values.add(value);
    }
}
