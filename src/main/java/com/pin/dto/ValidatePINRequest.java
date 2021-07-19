package com.pin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidatePINRequest {

    @JsonProperty("MSISDN")
    private String phoneNumber;

    @JsonProperty("PIN")
    private String pinNumber;

}
