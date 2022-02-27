package com.elements.Booking.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValidationErrorDTO {
    private String field;
    private String errorMessage;
}
