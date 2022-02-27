package com.elements.Booking.service;

import com.elements.Booking.controller.dto.BookingRequestDTO;
import com.elements.Booking.controller.dto.ResponseDTO;
import com.elements.Booking.controller.dto.HikerDTO;
import com.elements.Booking.domain.Booking;
import com.elements.Booking.domain.Hiker;
import com.elements.Booking.domain.Trail;
import com.elements.Booking.repository.BookingRepositoryImpl;
import com.elements.Booking.utils.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookingService {


    @Autowired
    BookingRepositoryImpl bookingRepository;

    @Autowired
    TrailsService trailsService;
    @Autowired
    ValidationUtil validationUtil;


    /**
     * Get all bookings
     *
     * @return BookingList
     */
    public Optional<List<Booking>> getBookings() {
        return Optional.ofNullable(bookingRepository.findAll());
    }

    /**
     * Fetch a booking by id
     *
     * @param id booking id to be fetched
     * @return ResponseEntity with appropriate status codes
     */
    public ResponseEntity getBookingById(Long id) {
        log.debug("Request to get booking with id " + id);

        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(booking.get(), HttpStatus.OK);
    }

    /**
     * Validate and save a booking request.
     *
     * @param bookingRequestDTO booking request
     * @return ResponseEntity with appropriate status codes
     */
    public ResponseEntity createBooking(BookingRequestDTO bookingRequestDTO) {
        log.debug("Request to create booking " + bookingRequestDTO.toString());

        if (bookingRequestDTO.getTrail() == null || bookingRequestDTO.getTrail().isBlank()) {
            return new ResponseEntity<>(ResponseDTO.builder().errorMessage("Trail is required!").build(), HttpStatus.BAD_REQUEST);
        }
        Trail trail = trailsService.getTrailByName(bookingRequestDTO.getTrail());
        if (trail == null) {
            return new ResponseEntity<>(ResponseDTO.builder().errorMessage("Trail '" + bookingRequestDTO.getTrail() + "' not found!").build(), HttpStatus.BAD_REQUEST);
        }
        ResponseDTO responseDTO = validationUtil.validateBooking(bookingRequestDTO, trail);
        if (responseDTO != null) {
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        double amount = trail.getUnitPrice() * bookingRequestDTO.getTotalHiker().size();
        Booking booking = Booking.builder().trail(trail.getName())
                .totalAmount(amount)
                .email(bookingRequestDTO.getEmail())
                .status("confirmed")
                .createdAt(LocalDateTime.now())
                .build();
        List<Hiker> hikerList = new ArrayList<>();
        for (HikerDTO hikerDTO : bookingRequestDTO.getTotalHiker()) {
            hikerList.add(Hiker.builder().age(hikerDTO.getAge())
                    .identificationNumber(hikerDTO.getID())
                    .contact(hikerDTO.getContact())
                    .name(hikerDTO.getName())
                    .booking(booking)
                    .build());
        }
        // set hikerList to link a booking to its hikers to support one to many relationship
        booking.setHikerList(hikerList);

        booking = bookingRepository.save(booking);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    /**
     * Cancels an existing booking by updating the status field in DB
     *
     * @param id booking id to cancel
     * @return ResponseEntity with appropriate status codes
     */
    @Transactional
    public ResponseEntity cancelBooking(Long id) {
        log.debug("Request to cancel booking with id " + id);
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (bookingOptional.get().getStatus().equals("cancelled")) {
            return new ResponseEntity<>("Booking is already cancelled!", HttpStatus.OK);
        }
        int update = bookingRepository.updateBookingStatusById("cancelled", bookingOptional.get().getId());
        if (update > 0) {
            return new ResponseEntity<>("Booking cancelled", HttpStatus.OK);
        }
        return new ResponseEntity("Failed to cancel booking", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Return bookings by email
     * @param email emailId of user
     * @return ResponseEntity with appropriate status codes
     */
    public ResponseEntity getBookingsByEmail(String email) {
        log.debug("Request to get booking with email " + email);

        Optional<List<Booking>> booking = Optional.ofNullable(bookingRepository.findByEmail(email));
        if (booking.isEmpty() || booking.get().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(booking.get(), HttpStatus.OK);
    }
}
