package com.elements.Booking.utils;

import com.elements.Booking.controller.dto.BookingRequestDTO;
import com.elements.Booking.controller.dto.HikerDTO;
import com.elements.Booking.controller.dto.ResponseDTO;
import com.elements.Booking.controller.dto.TrailsDTO;
import com.elements.Booking.domain.Trail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidationUtilTest {

    @InjectMocks
    ValidationUtil validationUtil;

    private Trail trail;
    private TrailsDTO trailsDTO;
    private List<TrailsDTO> trailsDTOList;
    private ResponseDTO responseDTO;
    private BookingRequestDTO bookingRequestDTO;
    private List<HikerDTO> hikerDTOList;
    private HikerDTO hikerDTO;

    @BeforeEach
    public void setUp() {
        trail = Trail.builder().id(1L)
                .name("test")
                .maximumAge(50)
                .minimumAge(20)
                .unitPrice(100.0)
                .startAt(720)
                .endAt(840)
                .build();
        trailsDTO = TrailsDTO.builder().name("test")
                .maximumAge(50)
                .minimumAge(10)
                .unitPrice(100.0)
                .startAt("12:00")
                .endAt("14:00")
                .build();
        trailsDTOList = new ArrayList<>() {{
            add(trailsDTO);
        }};
        responseDTO = ResponseDTO.builder().errorMessage("Invalid request!").build();
        hikerDTOList = new ArrayList<>();
        hikerDTO = HikerDTO.builder().name("name").age(40).ID("MMMTEST").contact(987654321L).build();

        hikerDTOList.add(hikerDTO);
        bookingRequestDTO = BookingRequestDTO.builder().trail("name")
                .email("a@a.com")
                .trail("test_trail")
                .totalHiker(hikerDTOList).build();
    }

    @Test
    public void validateTrails() {
        TrailsDTO invalidData = TrailsDTO.builder().build();
        ResponseDTO responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("Trail name is required!", responseDTO.getErrorMessage());

        invalidData.setName("test");
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: MaximumAge is invalid!", responseDTO.getErrorMessage());

        invalidData.setMinimumAge(10);
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: MaximumAge is invalid!", responseDTO.getErrorMessage());

        invalidData.setMaximumAge(0);
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: MaximumAge is invalid!", responseDTO.getErrorMessage());

        invalidData.setMaximumAge(10);
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: StartAt is required!", responseDTO.getErrorMessage());

        invalidData.setStartAt("10:10");
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: EndAt is required!", responseDTO.getErrorMessage());

        invalidData.setStartAt("10:");
        invalidData.setEndAt("10:10");
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: StartAt is invalid!", responseDTO.getErrorMessage());

        invalidData.setStartAt("10:ab");
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: StartAt is invalid!", responseDTO.getErrorMessage());

        invalidData.setStartAt("70:70");
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: StartAt is invalid!", responseDTO.getErrorMessage());

        invalidData.setStartAt("22:70");
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: StartAt is invalid!", responseDTO.getErrorMessage());

        invalidData.setStartAt("10:10");
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: StartAT EndAt cannot be same!", responseDTO.getErrorMessage());

        invalidData.setMinimumAge(0);
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: MinimumAge is invalid!", responseDTO.getErrorMessage());

        invalidData.setStartAt("10:10");
        invalidData.setEndAt("10:");
        invalidData.setMinimumAge(10);
        responseDTO = validationUtil.validateTrails(new ArrayList<>() {{
            add(invalidData);
        }});
        assertNotNull(responseDTO);
        assertEquals("test: EndAt is invalid!", responseDTO.getErrorMessage());

        responseDTO = validationUtil.validateTrails(trailsDTOList);
        assertNull(responseDTO);

    }

    @Test
    public void validateBooking() {
        BookingRequestDTO requestDTO = BookingRequestDTO.builder().build();
        ResponseDTO responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Email is invalid!", responseDTO.getErrorMessage());

        requestDTO.setEmail("abc");
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Email is invalid!", responseDTO.getErrorMessage());

        requestDTO.setEmail("abc@abc.com");
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Booking should have at least 1 hiker.", responseDTO.getErrorMessage());

        requestDTO.setTotalHiker(new ArrayList<>());
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Booking should have at least 1 hiker.", responseDTO.getErrorMessage());

        HikerDTO invalidHiker = HikerDTO.builder().build();

        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        invalidHiker.setName("   ");
        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        invalidHiker.setName("test");
        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        invalidHiker.setContact(0L);
        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        invalidHiker.setContact(9876543L);
        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        invalidHiker.setID("MMMTEST");
        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        invalidHiker.setAge(0);
        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        invalidHiker.setAge(19);
        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
            add(hikerDTO);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        invalidHiker.setAge(60);
        requestDTO.setTotalHiker(new ArrayList<>() {{
            add(invalidHiker);
        }});
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNotNull(responseDTO);
        assertEquals("Invalid request!", responseDTO.getErrorMessage());

        requestDTO.setTotalHiker(hikerDTOList);
        responseDTO = validationUtil.validateBooking(requestDTO, trail);
        assertNull(responseDTO);

    }
}