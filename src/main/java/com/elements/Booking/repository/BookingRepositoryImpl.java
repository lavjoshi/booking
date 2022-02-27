package com.elements.Booking.repository;

import com.elements.Booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepositoryImpl extends JpaRepository<Booking, Long> {

    List<Booking> findAll();

    @Modifying
    @Query(value = "update booking set status = ? where id = ?", nativeQuery = true)
    int updateBookingStatusById(String status, Long id);

    List<Booking> findByEmail(String email);
}
