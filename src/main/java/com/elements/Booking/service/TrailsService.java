package com.elements.Booking.service;

import com.elements.Booking.controller.dto.ResponseDTO;
import com.elements.Booking.controller.dto.TrailsDTO;
import com.elements.Booking.domain.Trail;
import com.elements.Booking.repository.TrailRepositoryImpl;
import com.elements.Booking.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrailsService {

    Logger logger = LoggerFactory.getLogger(TrailsService.class);

    @Autowired
    TrailRepositoryImpl trailRepository;

    @Autowired
    ValidationUtil validationUtil;

    /**
     * Fetches all existing trails from DB
     *
     * @return list of trails
     */
    public List<TrailsDTO> getTrails() {

        List<Trail> trailList = trailRepository.findAll();
        if (trailList == null || trailList.isEmpty()) {
            return null;
        }
        return trailList.stream().map(this::convertToTrailDTO).collect(Collectors.toList());
    }


    private String convertMinutesToTime(Integer time) {
        String minutes = Integer.toString(time % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (time / 60) + ":" + minutes;
    }

    /**
     * Validate and save a list of trail
     *
     * @param trailsDTOList trail list to be saved
     * @return ResponseEntity with appropriate status codes
     */
    public ResponseEntity saveTrails(List<TrailsDTO> trailsDTOList) {
        logger.info("Request to save trails " + trailsDTOList.toString());

        ResponseDTO error = validationUtil.validateTrails(trailsDTOList);
        if (error != null) {
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        List<Trail> trailList = new ArrayList<>();
        for (TrailsDTO trailsDTO : trailsDTOList) {
            trailList.add(buildTrailDomainFromDTO(trailsDTO));
        }
        trailRepository.saveAll(trailList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Fetched trail by its name
     *
     * @param name trail name
     * @return trail with given name
     */
    public Trail getTrailByName(String name) {
        return trailRepository.findByName(name);
    }

    /**
     * Fetches trail by name and convert to TrailDTO
     *
     * @param name name of trail
     * @return TrailDTO with converted time string
     */
    public Optional<TrailsDTO> getTrailDTOByName(String name) {
        Trail trail = getTrailByName(name);
        if (trail == null) {
            return Optional.empty();
        }
        return Optional.of(convertToTrailDTO(trail));
    }

    private Trail buildTrailDomainFromDTO(TrailsDTO trailsDTO) {
        long minutesStartAt = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, LocalTime.parse(trailsDTO.getStartAt()));
        long minutesEndAt = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, LocalTime.parse(trailsDTO.getEndAt()));
        return Trail.builder().name(trailsDTO.getName())
                .minimumAge(trailsDTO.getMinimumAge())
                .maximumAge(trailsDTO.getMaximumAge())
                .unitPrice(trailsDTO.getUnitPrice())
                .startAt((int) minutesStartAt)
                .endAt((int) minutesEndAt)
                .build();
    }

    private TrailsDTO convertToTrailDTO(Trail trail) {
        return TrailsDTO.builder().id(trail.getId())
                .name(trail.getName())
                .maximumAge(trail.getMaximumAge())
                .minimumAge(trail.getMinimumAge())
                .unitPrice(trail.getUnitPrice())
                .startAt(convertMinutesToTime(trail.getStartAt()))
                .endAt(convertMinutesToTime(trail.getEndAt()))
                .build();
    }


    public ResponseEntity updateTrail(TrailsDTO trailsDTO) {
        logger.info("Request to update trail " + trailsDTO.toString());
        ResponseDTO error = validationUtil.validateTrails(new ArrayList<>() {{
            add(trailsDTO);
        }});
        if (error != null) {
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        Trail trail = getTrailByName(trailsDTO.getName());
        if (trail == null) {
            return new ResponseEntity<>(ResponseDTO.builder().errorMessage("Trail '" + trailsDTO.getName() + "' not found!").build(), HttpStatus.BAD_REQUEST);
        }
        Trail updatedTrail = buildTrailDomainFromDTO(trailsDTO);
        updatedTrail.setId(trail.getId());
        updatedTrail = trailRepository.save(updatedTrail);
        return new ResponseEntity<>(convertToTrailDTO(updatedTrail), HttpStatus.OK);
    }


    public ResponseEntity<ResponseDTO> deleteTrail(String name) {
        Trail trail = getTrailByName(name);
        if (trail == null) {
            return new ResponseEntity<>(ResponseDTO.builder().errorMessage("Trail '" + name + "' not found!").build(), HttpStatus.BAD_REQUEST);
        }
        trailRepository.deleteById(trail.getId());
        return new ResponseEntity<>(ResponseDTO.builder().successMessage("Trail '" + name + "' deleted successfully!").build(), HttpStatus.OK);
    }
}
