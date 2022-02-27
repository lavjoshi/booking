package com.elements.Booking.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class HikerDTO {
    private String name;
    private Integer age;
    private Long contact;
    private String ID;
}
