package com.elements.Booking.controller;

import com.elements.Booking.controller.dto.ResponseDTO;
import com.elements.Booking.controller.dto.TrailsDTO;
import com.elements.Booking.domain.Trail;
import com.elements.Booking.service.TrailsService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@Slf4j
public class TrailsController {

    @Autowired
    TrailsService trailsService;

    @RequestMapping(value = "/trails", method = GET)
    public ResponseEntity getTrails() {
        List<TrailsDTO> trailList = trailsService.getTrails();

        if (trailList == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trailList, HttpStatus.OK);
    }

    @RequestMapping(value = "/trails", method = POST)
    public ResponseEntity saveTrails(@RequestBody List<TrailsDTO> trailsDTOList) {
        return trailsService.saveTrails(trailsDTOList);
    }

    @RequestMapping(value = "/trail/{name}", method = GET)
    public ResponseEntity<TrailsDTO> getTrails(@PathVariable String name) {
        Optional<TrailsDTO> trailsDTO = trailsService.getTrailDTOByName(name);

        if (trailsDTO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trailsDTO.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "/trails", method = PUT)
    public ResponseEntity updateTrails(@RequestBody TrailsDTO trailsDTO) {

        return trailsService.updateTrail(trailsDTO);
    }

    @RequestMapping(value = "/trail/{name}", method = DELETE)
    public ResponseEntity<ResponseDTO> deleteTrails(@PathVariable String name) {

        return trailsService.deleteTrail(name);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ResponseDTO> handleException(ConstraintViolationException exception) {
        log.error("Constraint violation ", exception);
        return new ResponseEntity<ResponseDTO>(ResponseDTO.builder()
                .errorMessage(exception.getSQLException().getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

}
