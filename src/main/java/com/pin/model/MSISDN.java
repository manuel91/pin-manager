package com.pin.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "MSISDN")
public class MSISDN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;

    @OneToMany(
            mappedBy = "msisdn",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private Set<PIN> pinSet;

}
