package com.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderWithAddressDto {
    @NotNull
    @JsonProperty("orderId")
    private Long orderId;
    @NotNull
    @JsonProperty("accountId")
    private Long accountId;
    @NotEmpty
    @JsonProperty("orderAddress")
    private String orderAddress;
    @NotNull
    @JsonProperty("orderAmount")
    private BigDecimal orderAmount;
    @NotNull
    @JsonProperty("orderDatetime")
    private Date orderDatetime;
    @NotEmpty
    @JsonProperty("orderType")
    private String orderType;
    @NotEmpty
    @JsonProperty("orderStatus")
    private String orderStatus;
    @NotNull
    @JsonProperty("orderNum")
    private Long orderNum;
}
