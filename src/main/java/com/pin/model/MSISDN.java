package com.pin.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "MSISDN")
public class MSISDN {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(
            mappedBy = "msisdn",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<PIN> pinList;

}
