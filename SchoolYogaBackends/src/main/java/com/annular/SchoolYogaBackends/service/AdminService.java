package com.annular.SchoolYogaBackends.service;

import java.util.Map;

public interface AdminService {

	Map<String, Object> getAllStudentDataBySchoolId(Integer schoolId);

	Map<String, Object> getAllStdIdAndDay(Integer stdId, String day, Integer userId);

}
