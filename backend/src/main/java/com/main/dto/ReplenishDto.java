package com.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReplenishDto {
    @NotNull
    @JsonProperty("replenishAmount")
    private BigDecimal replenishAmount;
}
