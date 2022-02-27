package com.elements.Booking.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private String successMessage;
    private String errorMessage;
    private List<ValidationErrorDTO> validationErrors;
}
