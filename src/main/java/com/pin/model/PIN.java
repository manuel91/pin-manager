package com.pin.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "PIN")
public class PIN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="msisdn_id", referencedColumnName="id")
    private MSISDN msisdn;

    private String pinNumber;

    private LocalDateTime creationDateTime = LocalDateTime.now();

    private Integer validationAttempts = 0;

    private Boolean discarded = false;

    private LocalDateTime discardedDateTime;

}
