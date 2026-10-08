package com.campusevent.management.service;

import com.campusevent.management.dto.response.RegisteredStudentResponse;
import com.campusevent.management.dto.response.RegistrationResponse;

import java.util.List;

public interface RegistrationService {

    RegistrationResponse registerStudentForEvent(Long studentId, Long eventId);

    List<RegistrationResponse> getStudentRegistrations(Long studentId);

    void cancelRegistration(Long registrationId, Long studentId, boolean isFaculty);

    List<RegisteredStudentResponse> getRegisteredStudentsForEvent(Long eventId);
}
