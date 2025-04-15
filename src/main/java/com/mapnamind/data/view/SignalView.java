package com.mapnamind.data.view;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalView implements Serializable {

    @NotBlank
    private String topic;

    @NotNull
    @Pattern(regexp = "0|1", message = "view.data.property.notValidIsProperty")
    private String isProperty;

    private String interval;
    private String aggregation;
    private Integer sampleCount;
}
