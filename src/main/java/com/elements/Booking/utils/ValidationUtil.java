package com.elements.Booking.utils;

import com.elements.Booking.controller.dto.*;
import com.elements.Booking.domain.Trail;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ValidationUtil {

    public ResponseDTO validateTime(String time, String field, String trailName) {
        try {
            String value[] = time.split(":");
            if (value.length != 2) {
                return ResponseDTO.builder().errorMessage(trailName + ": " + field + " is invalid!").build();
            }
            int hour = Integer.parseInt(value[0]);
            if (hour < 0 || hour > 23) {
                return ResponseDTO.builder().errorMessage(trailName + ": " + field + " is invalid!").build();
            }
            int mins = Integer.parseInt(value[1]);
            if (mins < 0 || mins > 59) {
                return ResponseDTO.builder().errorMessage(trailName + ": " + field + " is invalid!").build();
            }
        } catch (Exception e) {
            return ResponseDTO.builder().errorMessage(trailName + ": " + field + " is invalid!").build();
        }
        return null;
    }

    public ResponseDTO validateTrails(List<TrailsDTO> trailsDTOList) {
        ResponseDTO responseDTO = null;
        for (TrailsDTO trailsDTO : trailsDTOList) {
            String trailName = "";
            if (trailsDTO.getName() == null || trailsDTO.getName().trim().length() == 0) {
                return ResponseDTO.builder().errorMessage("Trail name is required!").build();
            }
            trailName = trailsDTO.getName();

            if (trailsDTO.getMaximumAge() == null || trailsDTO.getMaximumAge() <= 0) {
                return ResponseDTO.builder().errorMessage(trailName + ": " + "MaximumAge is invalid!").build();
            }
            if (trailsDTO.getMinimumAge() == null || trailsDTO.getMinimumAge() <= 0) {
                return ResponseDTO.builder().errorMessage(trailName + ": " + "MinimumAge is invalid!").build();
            }
            if (trailsDTO.getStartAt() == null) {
                return ResponseDTO.builder().errorMessage(trailName + ": " + "StartAt is required!").build();
            }
            if (trailsDTO.getEndAt() == null) {
                return ResponseDTO.builder().errorMessage(trailName + ": " + "EndAt is required!").build();
            }
            if (trailsDTO.getEndAt().equals(trailsDTO.getStartAt())) {
                return ResponseDTO.builder().errorMessage(trailName + ": " + "StartAT EndAt cannot be same!").build();
            }
            responseDTO = validateTime(trailsDTO.getStartAt(), "StartAt", trailName);
            if (responseDTO != null) {
                return responseDTO;
            }
            responseDTO = validateTime(trailsDTO.getEndAt(), "EndAt", trailName);
            if (responseDTO != null) {
                return responseDTO;
            }
        }
        return null;
    }

    public ResponseDTO validateBooking(BookingRequestDTO bookingRequestDTO, Trail trail) {


        if (bookingRequestDTO.getEmail() == null || bookingRequestDTO.getEmail().isBlank() || !EmailValidator.getInstance().isValid(bookingRequestDTO.getEmail())) {
            return ResponseDTO.builder().errorMessage("Email is invalid!").build();
        }
        if (bookingRequestDTO.getTotalHiker() == null || bookingRequestDTO.getTotalHiker().isEmpty()) {
            return ResponseDTO.builder().errorMessage("Booking should have at least 1 hiker.").build();
        }

        List<HikerDTO> hikerDTOList = bookingRequestDTO.getTotalHiker();
        Set<String> ids = new HashSet<>();
        List<ValidationErrorDTO> validationErrorDTOS = hikerDTOList.stream().map(s -> {
            return validateHiker(s, trail.getMaximumAge(), trail.getMinimumAge(), ids);
        }).toList().stream().filter(Objects::nonNull).collect(ArrayList::new, List::addAll, List::addAll);
        if (!validationErrorDTOS.isEmpty()) {
            return ResponseDTO.builder().errorMessage("Invalid request!")
                    .validationErrors(validationErrorDTOS)
                    .build();
        }
        return null;
    }

    private List<ValidationErrorDTO> validateHiker(HikerDTO hikerDTO, Integer maximumAge, Integer minimumAge, Set<String> ids) {
        List<ValidationErrorDTO> validationErrorDTOS = new ArrayList<>();
        if (hikerDTO.getName() == null || hikerDTO.getName().isBlank()) {
            validationErrorDTOS.add(ValidationErrorDTO.builder().field("Name")
                    .errorMessage("Hiker Name is required!")
                    .build());
            return validationErrorDTOS;
        }
        if (hikerDTO.getContact() == null || hikerDTO.getContact().equals(0L)) {
            validationErrorDTOS.add(ValidationErrorDTO.builder().field("Contact")
                    .errorMessage("Hiker '" + hikerDTO.getName() + "' : Contact is required!")
                    .build());
        }
        if (hikerDTO.getID() == null || hikerDTO.getID().isBlank()) {
            validationErrorDTOS.add(ValidationErrorDTO.builder().field("ID")
                    .errorMessage("Hiker '" + hikerDTO.getName() + "' : ID is required!")
                    .build());
        }
        if (ids.contains(hikerDTO.getID())) {
            validationErrorDTOS.add(ValidationErrorDTO.builder().field("ID")
                    .errorMessage("Hiker '" + hikerDTO.getName() + "' : please provide unique ID number for all hikers!")
                    .build());
        }
        ids.add(hikerDTO.getID());
        if (hikerDTO.getAge() == null || hikerDTO.getAge() <= 0) {
            validationErrorDTOS.add(ValidationErrorDTO.builder().field("Age")
                    .errorMessage("Hiker '" + hikerDTO.getName() + "' : Age is invalid!")
                    .build());
        }
        if (hikerDTO.getAge() != null && (hikerDTO.getAge() < minimumAge || hikerDTO.getAge() > maximumAge)) {
            validationErrorDTOS.add(ValidationErrorDTO.builder().field("Age")
                    .errorMessage("Hiker '" + hikerDTO.getName() + "' : Age should be in limits (" + minimumAge + "-" + maximumAge + ")")
                    .build());
        }
        if (validationErrorDTOS.isEmpty()) {
            return null;
        }
        return validationErrorDTOS;

    }

}
