package com.main.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReplenishDto {
    @NotNull
    @JsonProperty("replenishAmount")
    private BigDecimal replenishAmount;
}
