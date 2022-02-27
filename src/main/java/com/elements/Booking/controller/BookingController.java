package com.elements.Booking.controller;


import com.elements.Booking.controller.dto.BookingRequestDTO;
import com.elements.Booking.domain.Booking;
import com.elements.Booking.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@Slf4j
public class BookingController {

    @Autowired
    BookingService bookingService;


    @RequestMapping(value = "/booking", method = POST)
    public ResponseEntity saveBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {

        return bookingService.createBooking(bookingRequestDTO);

    }

    @RequestMapping(value = "/bookings", method = GET)
    public ResponseEntity getBookings() {
        Optional<List<Booking>> bookingList = bookingService.getBookings();

        if (bookingList.isEmpty() || bookingList.get().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bookingList.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "/booking/{id}", method = GET)
    public ResponseEntity getBookings(@PathVariable Long id) {

        return bookingService.getBookingById(id);

    }

    @RequestMapping(value = "/bookings/{email}", method = GET)
    public ResponseEntity getBookingsByEmail(@PathVariable String email) {

        return bookingService.getBookingsByEmail(email);

    }

    @RequestMapping(value = "/booking/{id}", method = PUT)
    public ResponseEntity cancelBooking(@PathVariable Long id) {

        return bookingService.cancelBooking(id);

    }

}
