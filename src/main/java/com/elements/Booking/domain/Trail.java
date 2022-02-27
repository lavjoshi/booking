package com.elements.Booking.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private Integer startAt;
    @Column
    private Integer endAt;
    @Column
    private Integer minimumAge;
    @Column
    private Integer maximumAge;
    @Column
    private Double unitPrice;


}
