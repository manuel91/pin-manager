package com.pin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MSISDNResponse {

    @JsonProperty("MSISDN")
    private String phoneNumber;

    private Set<PINResponse> pinSet;

}
