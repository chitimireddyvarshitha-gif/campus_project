package com.campusevent.management.service;

import com.campusevent.management.dto.request.UpdateProfileRequest;
import com.campusevent.management.dto.response.StudentDashboardResponse;
import com.campusevent.management.dto.response.StudentProfileResponse;
import com.campusevent.management.dto.response.UserResponse;

public interface StudentService {

    StudentDashboardResponse getStudentDashboard(Long studentId);

    StudentProfileResponse getStudentProfile(Long studentId);

    UserResponse updateStudentProfile(Long studentId, UpdateProfileRequest request);
}
