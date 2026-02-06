package com.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class TaskPrintDto {
    @NotNull
    @JsonProperty("name")
    private String name;
    @NotNull
    @JsonProperty("type")
    private String type;
    @NotNull
    @JsonProperty("blob")
    private byte[] blob;
    @NotNull
    @JsonProperty("typePrint")
    private String typePrint;
    @NotNull
    @JsonProperty("numberCopies")
    private Long numberCopies;
}
