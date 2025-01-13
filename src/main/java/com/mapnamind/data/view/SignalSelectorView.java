package com.mapnamind.data.view;

import com.mapnamind.common.annotation.Date;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    private List<SignalView> signals;

    private List<ValueView> values;

    @Date
    private String startDate;

    @Date
    private String endDate;

    public void addValue(ValueView value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        this.values.add(value);
    }

}
