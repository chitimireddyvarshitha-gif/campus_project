package com.campusevent.management.repository;

import com.campusevent.management.entity.Event;
import com.campusevent.management.entity.EventCategory;
import com.campusevent.management.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory(EventCategory category);

    List<Event> findByStatus(EventStatus status);

    long countByStatus(EventStatus status);

    @Query("SELECT e FROM Event e WHERE " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:venue IS NULL OR LOWER(e.venue) LIKE LOWER(CONCAT('%', :venue, '%'))) AND " +
           "(:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(e.organizer) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(e.venue) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY e.eventDate ASC, e.createdAt DESC")
    List<Event> searchAndFilterEvents(
        @Param("search") String search,
        @Param("category") EventCategory category,
        @Param("venue") String venue,
        @Param("status") EventStatus status
    );

    @Query("SELECT e FROM Event e WHERE e.eventDate >= :today AND e.status != 'CANCELLED' ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("today") LocalDate today);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventDate >= :today AND e.status != 'CANCELLED'")
    long countUpcomingEvents(@Param("today") LocalDate today);
}
