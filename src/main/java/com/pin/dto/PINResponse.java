package com.pin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PINResponse {

    @JsonProperty("PIN")
    private String pinNumber;

    private LocalDateTime creationDateTime;

    private Integer validationAttempts;

    private Boolean discarded;

    private LocalDateTime discardedDateTime;

}
