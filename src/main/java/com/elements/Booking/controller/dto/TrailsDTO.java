package com.elements.Booking.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class TrailsDTO {
    private Long id;
    private String name;
    private String startAt;
    private String endAt;
    private Integer minimumAge;
    private Integer maximumAge;
    private Double unitPrice;
}
