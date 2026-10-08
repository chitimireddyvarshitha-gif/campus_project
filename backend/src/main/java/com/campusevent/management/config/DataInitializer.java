package com.campusevent.management.config;

import com.campusevent.management.entity.*;
import com.campusevent.management.repository.EventRepository;
import com.campusevent.management.repository.RegistrationRepository;
import com.campusevent.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Seeding initial collegiate users into MySQL database...");
            seedUsers();
        }

        if (eventRepository.count() == 0) {
            log.info("Seeding initial campus events into MySQL database...");
            seedEvents();
        }

        if (registrationRepository.count() == 0) {
            log.info("Seeding initial student registrations into MySQL database...");
            seedRegistrations();
        }
    }

    private void seedUsers() {
        User student = User.builder()
                .name("Varshitha Chitimireddy")
                .email("varshitha.c@campus.edu")
                .password(passwordEncoder.encode("CampusPass@2026"))
                .phone("+1 (555) 234-8901")
                .role(Role.STUDENT)
                .department("Computer Science & Engineering")
                .year("3rd Year (Junior)")
                .avatar("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300")
                .build();

        User faculty = User.builder()
                .name("Dr. Robert Vance")
                .email("r.vance@campus.edu")
                .password(passwordEncoder.encode("FacultyPass@2026"))
                .phone("+1 (555) 472-9102")
                .role(Role.FACULTY)
                .department("School of Computing & Engineering")
                .designation("Associate Dean of Student Activities & Event Coordinator")
                .avatar("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300")
                .build();

        User student2 = User.builder()
                .name("Aravind Swaminathan")
                .email("a.swami@campus.edu")
                .password(passwordEncoder.encode("StudentPass@2026"))
                .phone("+1 (555) 345-6789")
                .role(Role.STUDENT)
                .department("Information Science")
                .year("2nd Year (Sophomore)")
                .build();

        User student3 = User.builder()
                .name("Elena Rostova")
                .email("e.rostova@campus.edu")
                .password(passwordEncoder.encode("StudentPass@2026"))
                .phone("+1 (555) 987-1234")
                .role(Role.STUDENT)
                .department("Electrical Engineering")
                .year("4th Year (Senior)")
                .build();

        userRepository.saveAll(Arrays.asList(student, faculty, student2, student3));
    }

    private void seedEvents() {
        List<Event> events = Arrays.asList(
                Event.builder()
                        .title("Campus Hackathon 2026")
                        .description("A 24-hour inter-collegiate coding marathon where students collaborate in multidisciplinary teams to engineer functional prototypes addressing campus sustainability, accessibility, and autonomous student services.")
                        .category(EventCategory.TECHNICAL)
                        .eventDate(LocalDate.of(2026, 10, 14))
                        .eventTime("09:00 AM")
                        .venue("Alan Turing Innovation Hub, Block C")
                        .organizer("Department of Computer Science & Engineering")
                        .capacity(200)
                        .registeredCount(178)
                        .registrationDeadline(LocalDate.of(2026, 10, 10))
                        .imageUrl("/src/assets/images/event_campus_hackathon_1790559795018.jpg")
                        .status(EventStatus.UPCOMING)
                        .build(),

                Event.builder()
                        .title("Cultural Fest 2026 — Symphonia")
                        .description("The premier annual cultural extravaganza celebrating music, performing arts, theatrical drama, and culinary traditions. Includes battle of the bands and classical dance showcases.")
                        .category(EventCategory.CULTURAL)
                        .eventDate(LocalDate.of(2026, 10, 28))
                        .eventTime("05:00 PM")
                        .venue("University Grand Auditorium & Open Amphitheater")
                        .organizer("Campus Student Cultural Council")
                        .capacity(800)
                        .registeredCount(742)
                        .registrationDeadline(LocalDate.of(2026, 10, 25))
                        .imageUrl("/src/assets/images/event_cultural_fest_1790559807104.jpg")
                        .status(EventStatus.UPCOMING)
                        .build(),

                Event.builder()
                        .title("AI & Future Technologies Summit")
                        .description("Keynote addresses, technical breakout tracks, and live demonstrations featuring neural network architectures, robotic vision, and ethics in automated decision-making.")
                        .category(EventCategory.TECHNICAL)
                        .eventDate(LocalDate.of(2026, 11, 5))
                        .eventTime("10:00 AM")
                        .venue("Sir C.V. Raman Memorial Hall")
                        .organizer("Center for Artificial Intelligence & Robotics")
                        .capacity(350)
                        .registeredCount(350)
                        .registrationDeadline(LocalDate.of(2026, 11, 1))
                        .imageUrl("/src/assets/images/event_tech_summit_1790559818879.jpg")
                        .status(EventStatus.UPCOMING)
                        .build(),

                Event.builder()
                        .title("Inter-College Cricket Tournament")
                        .description("The annual inter-collegiate cricket tournament featuring 16 varsity teams competing across five days in T20 format for the Chancellor Championship Shield.")
                        .category(EventCategory.SPORTS)
                        .eventDate(LocalDate.of(2026, 11, 12))
                        .eventTime("08:30 AM")
                        .venue("University Athletics Oval & Sports Complex")
                        .organizer("Directorate of Physical Education & Athletics")
                        .capacity(500)
                        .registeredCount(310)
                        .registrationDeadline(LocalDate.of(2026, 11, 8))
                        .imageUrl("/src/assets/images/event_sports_tournament_1790559831246.jpg")
                        .status(EventStatus.UPCOMING)
                        .build(),

                Event.builder()
                        .title("Research Symposium on Sustainable Energy")
                        .description("Faculty-guided symposium on renewable power generation, grid stabilization, and next-generation battery chemistries with poster sessions.")
                        .category(EventCategory.ACADEMIC)
                        .eventDate(LocalDate.of(2026, 11, 20))
                        .eventTime("09:30 AM")
                        .venue("Science Research & Conference Complex")
                        .organizer("Academic Research & Development Council")
                        .capacity(180)
                        .registeredCount(95)
                        .registrationDeadline(LocalDate.of(2026, 11, 16))
                        .imageUrl("/src/assets/images/hero_campus_portal_1790559781450.jpg")
                        .status(EventStatus.UPCOMING)
                        .build(),

                Event.builder()
                        .title("Career Development & Industry Networking Workshop")
                        .description("Structured corporate engagement masterclass covering resume synthesis, technical interview simulation, and salary negotiation strategies.")
                        .category(EventCategory.ACADEMIC)
                        .eventDate(LocalDate.of(2026, 12, 2))
                        .eventTime("02:00 PM")
                        .venue("Placement & Career Advisory Center")
                        .organizer("Office of Career Advancement")
                        .capacity(250)
                        .registeredCount(250)
                        .registrationDeadline(LocalDate.of(2026, 11, 28))
                        .imageUrl("/src/assets/images/event_tech_summit_1790559818879.jpg")
                        .status(EventStatus.UPCOMING)
                        .build(),

                Event.builder()
                        .title("Annual Photography Contest & Exhibition")
                        .description("Juried photography exhibition showcasing student perspectives on urban architecture, documentary storytelling, and experimental photography.")
                        .category(EventCategory.CULTURAL)
                        .eventDate(LocalDate.of(2026, 12, 10))
                        .eventTime("11:00 AM")
                        .venue("Fine Arts Gallery, Humanities Wing")
                        .organizer("Fine Arts Guild & Shutterbugs Society")
                        .capacity(150)
                        .registeredCount(88)
                        .registrationDeadline(LocalDate.of(2026, 12, 5))
                        .imageUrl("/src/assets/images/event_cultural_fest_1790559807104.jpg")
                        .status(EventStatus.UPCOMING)
                        .build(),

                Event.builder()
                        .title("Collegiate Basketball Championship Finals")
                        .description("Varsity conference championship matchup between East Campus Panthers and North Hall Royals with commemorative fan pass.")
                        .category(EventCategory.SPORTS)
                        .eventDate(LocalDate.of(2026, 12, 15))
                        .eventTime("04:00 PM")
                        .venue("John Wooden Indoor Sports Pavilion")
                        .organizer("Campus Recreation & Intramurals")
                        .capacity(400)
                        .registeredCount(290)
                        .registrationDeadline(LocalDate.of(2026, 12, 12))
                        .imageUrl("/src/assets/images/event_sports_tournament_1790559831246.jpg")
                        .status(EventStatus.UPCOMING)
                        .build()
        );

        eventRepository.saveAll(events);
    }

    private void seedRegistrations() {
        userRepository.findByEmail("varshitha.c@campus.edu").ifPresent(student -> {
            List<Event> events = eventRepository.findAll();
            if (events.size() >= 2) {
                Registration reg1 = Registration.builder()
                        .student(student)
                        .event(events.get(0))
                        .registrationDate(LocalDate.of(2026, 9, 20))
                        .status(RegistrationStatus.REGISTERED)
                        .build();

                Registration reg2 = Registration.builder()
                        .student(student)
                        .event(events.get(1))
                        .registrationDate(LocalDate.of(2026, 9, 22))
                        .status(RegistrationStatus.REGISTERED)
                        .build();

                registrationRepository.saveAll(Arrays.asList(reg1, reg2));
            }
        });
    }
}
