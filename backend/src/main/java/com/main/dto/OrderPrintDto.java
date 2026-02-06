package com.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderPrintDto {
    @NotNull
    @JsonProperty("vendingPointId")
    private Long vendingPointId;
    @NotNull
    @JsonProperty("tasksPrint")
    private List<TaskPrintDto> files;
}
