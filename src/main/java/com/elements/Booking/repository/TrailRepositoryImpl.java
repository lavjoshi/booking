package com.elements.Booking.repository;

import com.elements.Booking.domain.Trail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrailRepositoryImpl extends JpaRepository<Trail, Long> {

    List<Trail> findAll();

    Trail findByName(String name);
}
