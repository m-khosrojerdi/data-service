package com.mapnamind.data.view;

import com.mapnamind.common.annotation.Date;
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

    private String stringSignals;
    private List<SignalView> signals;
    private List<ValueView> values;

    @Date
    private String startDate;

    @Date
    private String endDate;

    private String exportUuid;

    public void addValue(ValueView value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        this.values.add(value);
    }

}
