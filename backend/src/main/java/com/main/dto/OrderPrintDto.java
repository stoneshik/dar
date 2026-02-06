package com.main.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderPrintDto {
    @NotNull
    @JsonProperty("vendingPointId")
    private Long vendingPointId;
    @NotNull
    @JsonProperty("tasksPrint")
    private List<TaskPrintDto> files;
}
