package com.campusevent.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_event_category", columnList = "category"),
    @Index(name = "idx_event_date", columnList = "event_date"),
    @Index(name = "idx_event_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventCategory category;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_time", nullable = false, length = 50)
    private String eventTime;

    @Column(nullable = false, length = 150)
    private String venue;

    @Column(nullable = false, length = 150)
    private String organizer;

    @Column(nullable = false)
    private Integer capacity;

    @Builder.Default
    @Column(name = "registered_count", nullable = false)
    private Integer registeredCount = 0;

    @Column(name = "registration_deadline", nullable = false)
    private LocalDate registrationDeadline;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.registeredCount == null) {
            this.registeredCount = 0;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public int getAvailableSeats() {
        if (capacity == null || registeredCount == null) {
            return 0;
        }
        return Math.max(0, capacity - registeredCount);
    }
}
