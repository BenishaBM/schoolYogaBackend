package com.annular.SchoolYogaBackends.service;

import org.springframework.http.ResponseEntity;

import com.annular.SchoolYogaBackends.webModel.DayDetailsWebModel;

public interface DayDetailsService {

	ResponseEntity<?> saveDayDetails(DayDetailsWebModel dayDetailsWebModel);

	ResponseEntity<?> getAllDayDetails();

	ResponseEntity<?> deleteDayDetails(Integer dayDetailsId);

	ResponseEntity<?> getAllSchoolDetails();

	ResponseEntity<?> getAllClassDetails();

}
