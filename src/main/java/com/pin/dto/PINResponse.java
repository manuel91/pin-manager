package com.pin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PINResponse {

    @JsonProperty("PIN")
    private String pinNumber;

    private LocalDate creationDate = LocalDate.now();

    private Integer validationAttempts = 0;

    private Boolean discarded = false;

    private LocalDate discardedDate;

}
