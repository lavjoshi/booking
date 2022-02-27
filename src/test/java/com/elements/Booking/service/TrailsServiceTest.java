package com.elements.Booking.service;

import com.elements.Booking.controller.dto.ResponseDTO;
import com.elements.Booking.controller.dto.TrailsDTO;
import com.elements.Booking.domain.Trail;
import com.elements.Booking.repository.TrailRepositoryImpl;
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
class TrailsServiceTest {

    @InjectMocks
    TrailsService trailsService;
    @Mock
    TrailRepositoryImpl trailRepository;
    @Mock
    ValidationUtil validationUtil;


    private Trail trail;
    private List<Trail> trailList;
    private TrailsDTO trailsDTO;
    private List<TrailsDTO> trailsDTOList;
    private ResponseDTO responseDTO;

    @BeforeEach
    public void setUp() {
        trail = Trail.builder().id(1L)
                .name("test")
                .maximumAge(50)
                .minimumAge(10)
                .unitPrice(100.0)
                .startAt(720)
                .endAt(840)
                .build();
        trailList = new ArrayList<>() {{
            add(trail);
        }};
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


    }

    @Test
    public void getTrails() {

        when(trailRepository.findAll()).thenReturn(null);
        List<TrailsDTO> trailsDTOList = trailsService.getTrails();
        assertNull(trailsDTOList);
        verify(trailRepository, times(1)).findAll();

        when(trailRepository.findAll()).thenReturn(trailList);
        List<TrailsDTO> trails = trailsService.getTrails();
        assertNotNull(trails);
        verify(trailRepository, times(2)).findAll();

    }

    @Test
    public void saveTrails() {
        when(validationUtil.validateTrails(trailsDTOList)).thenReturn(null);
        ResponseEntity response = trailsService.saveTrails(trailsDTOList);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        when(validationUtil.validateTrails(trailsDTOList)).thenReturn(responseDTO);
        response = trailsService.saveTrails(trailsDTOList);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getTrailByName() {
        when(trailRepository.findByName("test")).thenReturn(null);
        Trail trailResult = trailsService.getTrailByName("test");
        assertNull(trailResult);

        when(trailRepository.findByName("test")).thenReturn(trail);
        trailResult = trailsService.getTrailByName("test");
        assertNotNull(trailResult);

    }

    @Test
    public void getTrailDTOByName() {
        when(trailRepository.findByName("test")).thenReturn(null);
        Optional<TrailsDTO> optionalTrailsDTO = trailsService.getTrailDTOByName("test");
        assertTrue(optionalTrailsDTO.isEmpty());

        when(trailRepository.findByName("test")).thenReturn(trail);
        optionalTrailsDTO = trailsService.getTrailDTOByName("test");
        assertFalse(optionalTrailsDTO.isEmpty());
    }

    @Test
    public void updateTrail() {

        when(validationUtil.validateTrails(trailsDTOList)).thenReturn(responseDTO);
        ResponseEntity response = trailsService.updateTrail(trailsDTO);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        when(validationUtil.validateTrails(trailsDTOList)).thenReturn(null);
        when(trailRepository.findByName("test")).thenReturn(null);
        response = trailsService.updateTrail(trailsDTO);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

    }

    @Test
    public void deleteById() {
        when(trailRepository.findByName("test")).thenReturn(null);
        ResponseEntity response = trailsService.deleteTrail("test");
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        when(trailRepository.findByName("test")).thenReturn(trail);
        response = trailsService.deleteTrail("test");
        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }
}