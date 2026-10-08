package com.campusevent.management.repository;

import com.campusevent.management.entity.Registration;
import com.campusevent.management.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    @Query("SELECT r FROM Registration r JOIN FETCH r.event JOIN FETCH r.student WHERE r.student.id = :studentId ORDER BY r.registrationDate DESC, r.id DESC")
    List<Registration> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    List<Registration> findByStudentId(Long studentId);

    List<Registration> findByStudentIdAndStatus(Long studentId, RegistrationStatus status);

    @Query("SELECT r FROM Registration r JOIN FETCH r.student WHERE r.event.id = :eventId ORDER BY r.registrationDate ASC, r.id ASC")
    List<Registration> findByEventIdWithStudent(@Param("eventId") Long eventId);

    List<Registration> findByEventId(Long eventId);

    List<Registration> findByEventIdAndStatus(Long eventId, RegistrationStatus status);

    Optional<Registration> findByStudentIdAndEventId(Long studentId, Long eventId);

    boolean existsByStudentIdAndEventIdAndStatus(Long studentId, Long eventId, RegistrationStatus status);

    long countByEventIdAndStatus(Long eventId, RegistrationStatus status);

    long countByStudentIdAndStatus(Long studentId, RegistrationStatus status);

    long countByStatus(RegistrationStatus status);
}
