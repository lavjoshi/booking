package com.elements.Booking.service;

import com.elements.Booking.controller.dto.BookingRequestDTO;
import com.elements.Booking.controller.dto.HikerDTO;
import com.elements.Booking.domain.Booking;
import com.elements.Booking.domain.Hiker;
import com.elements.Booking.domain.Trail;
import com.elements.Booking.repository.BookingRepositoryImpl;
import com.elements.Booking.utils.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    BookingService bookingService;
    @Mock
    BookingRepositoryImpl bookingRepository;
    @Mock
    TrailsService trailsService;

    @Mock
    ValidationUtil validationUtil;

    private List<Booking> bookingList;
    private Booking booking;
    private BookingRequestDTO bookingRequestDTO;
    private Trail trail;
    private Hiker hiker;
    private String email;


    @BeforeEach
    public void setUp() {
        email = "abc@test.com";
        booking = Booking.builder().id(1L)
                .email("email")
                .trail("test_trail")
                .status("confirmed")
                .build();
        bookingList = new ArrayList<>() {{
            add(booking);
        }};

        List<HikerDTO> hikerDTOList = new ArrayList<>();
        HikerDTO hikerDTO = HikerDTO.builder().name("name").age(20).ID("1234").contact(987654321L).build();
        hikerDTOList.add(hikerDTO);
        bookingRequestDTO = BookingRequestDTO.builder().trail("name")
                .email("a@a.com")
                .trail("test_trail")
                .totalHiker(hikerDTOList).build();
        hiker = Hiker.builder()
                .booking(booking)
                .age(10)
                .contact(9876543L)
                .name("test")
                .identificationNumber("test_id")
                .build();
        List<Hiker> hikerList = new ArrayList<>() {{
            add(hiker);
        }};
        booking.setHikerList(hikerList);
        trail = Trail.builder().startAt(120)
                .unitPrice(100.0)
                .minimumAge(18)
                .maximumAge(50)
                .name("test_trail")
                .id(1L)
                .build();

    }


    @Test
    void testGetBookingById() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        ResponseEntity response = bookingService.getBookingById(1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookingByIdWithInvalidId() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity response = bookingService.getBookingById(1L);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        verify(bookingRepository, times(1)).findById(1L);
    }


    @Test
    void testCreateBookingWithInvalidData() {
        //when trail is null
        BookingRequestDTO bookingRequest = BookingRequestDTO.builder().build();
        ResponseEntity response = bookingService.createBooking(bookingRequest);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        verify(bookingRepository, times(0)).save(booking);

        //when trail is not found
        bookingRequest.setTrail("trail");
        when(trailsService.getTrailByName("trail")).thenReturn(null);
        response = bookingService.createBooking(bookingRequest);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        verify(bookingRepository, times(0)).save(booking);

    }

    @Test
    void testCreateBooking() {

        when(trailsService.getTrailByName("test_trail")).thenReturn(trail);
        ResponseEntity response = bookingService.createBooking(bookingRequestDTO);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void cancelBooking() {
        //booking ID not found
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity response = bookingService.cancelBooking(1L);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        //booking already cancelled
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        booking.setStatus("cancelled");
        response = bookingService.cancelBooking(1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals("Booking is already cancelled!", response.getBody());

        booking.setStatus("confirmed");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.updateBookingStatusById("cancelled", 1L)).thenReturn(0);
        response = bookingService.cancelBooking(1L);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals("Failed to cancel booking", response.getBody());

        booking.setStatus("confirmed");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.updateBookingStatusById("cancelled", 1L)).thenReturn(1);
        response = bookingService.cancelBooking(1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals("Booking cancelled", response.getBody());
    }

    @Test
    void getBookingByEmail() {
        when(bookingRepository.findByEmail(email)).thenReturn(null);
        ResponseEntity response = bookingService.getBookingsByEmail(email);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        verify(bookingRepository, times(1)).findByEmail(email);


        when(bookingRepository.findByEmail(email)).thenReturn(new ArrayList<>());
        response = bookingService.getBookingsByEmail(email);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        verify(bookingRepository, times(2)).findByEmail(email);


        when(bookingRepository.findByEmail(email)).thenReturn(bookingList);
        response = bookingService.getBookingsByEmail(email);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        verify(bookingRepository, times(3)).findByEmail(email);
    }

    @Test
    void getBookings() {
        when(bookingRepository.findAll()).thenReturn(bookingList);
        Optional<List<Booking>> booking = bookingService.getBookings();
        assertFalse(booking.isEmpty());

    }
}