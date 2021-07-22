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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="msisdn_id", referencedColumnName="id")
    private MSISDN msisdn;

    @Column(name = "pin_number")
    private String pinNumber;

    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime = LocalDateTime.now();

    @Column(name = "validation_attempts")
    private Integer validationAttempts = 0;

    @Column(name = "discarded")
    private Boolean discarded = false;

    @Column(name = "discarded_date_time")
    private LocalDateTime discardedDateTime;

}
