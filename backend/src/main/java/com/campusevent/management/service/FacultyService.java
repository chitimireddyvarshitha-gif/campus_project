package com.campusevent.management.service;

import com.campusevent.management.dto.request.CreateEventRequest;
import com.campusevent.management.dto.request.UpdateEventRequest;
import com.campusevent.management.dto.response.EventResponse;
import com.campusevent.management.dto.response.FacultyDashboardResponse;
import com.campusevent.management.dto.response.RegisteredStudentResponse;

import java.util.List;

public interface FacultyService {

    FacultyDashboardResponse getFacultyDashboard();

    List<EventResponse> getFacultyEvents();

    EventResponse createFacultyEvent(CreateEventRequest request);

    EventResponse updateFacultyEvent(Long eventId, UpdateEventRequest request);

    void deleteFacultyEvent(Long eventId);

    List<RegisteredStudentResponse> getRegisteredStudentsForEvent(Long eventId);
}
