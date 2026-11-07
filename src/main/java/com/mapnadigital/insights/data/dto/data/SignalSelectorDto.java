package com.mapnadigital.insights.data.dto.data;

import com.mapnadigital.insights.common.annotation.Date;
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
public class SignalSelectorDto implements Serializable {

    private String stringSignals;
    private List<SignalDto> signals;
    private List<ValueDto> values;

    @Date
    private String startDate;

    @Date
    private String endDate;

    private String exportUuid;

    public void addValue(ValueDto value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        this.values.add(value);
    }

}
