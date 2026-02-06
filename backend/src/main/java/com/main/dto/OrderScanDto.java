package com.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderScanDto {
    @NotNull
    @JsonProperty("vendingPointId")
    private Long vendingPointId;
    @NotNull
    @JsonProperty("scanTaskNumberPages")
    private Long scanTaskNumberPages;
}
