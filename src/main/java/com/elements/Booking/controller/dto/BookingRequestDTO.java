package com.elements.Booking.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
@Setter
public class BookingRequestDTO {

    private String trail;
    private String email;
    private List<HikerDTO> totalHiker;


}
