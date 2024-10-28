package com.mapnamind.data.view;

import com.mapnamind.common.annotation.DateFormat;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignalSelectorView implements Serializable {

    @NotEmpty
    private List<SignalView> signals;

    @DateFormat
    private String startDate;

    @DateFormat
    private String endDate;

}
