package com.pin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MSISDNRequest {

    @JsonProperty("MSISDN")
    private String phoneNumber;

}
