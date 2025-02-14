package com.annular.SchoolYogaBackends.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.service.DayDetailsService;
import com.annular.SchoolYogaBackends.webModel.DayDetailsWebModel;

@RestController
@RequestMapping("/DayDetails")
public class DayDetailsController {
	
	public static final Logger logger = LoggerFactory.getLogger(DayDetailsController.class);
	
	@Autowired
	DayDetailsService dayDetailsService;
	

	@PostMapping("saveDayDetails")
	public ResponseEntity<?> saveDayDetails(@RequestBody DayDetailsWebModel dayDetailsWebModel) {
		try {
			logger.info("dayDetails controller start");
			return dayDetailsService.saveDayDetails(dayDetailsWebModel);
		} catch (Exception e) {
			logger.error("saveDayDetails Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}
	
	@GetMapping("getAllDayDetails")
	public ResponseEntity<?> getAllDayDetails() {
		try {
			logger.info("getAllDayDetails controller start");
			return dayDetailsService.getAllDayDetails();
		} catch (Exception e) {
			logger.error("getAllDayDetails Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}
	
	@DeleteMapping("deleteDayDetails")
	public ResponseEntity<?>deleteDayDetails(@RequestParam("dayDetailsId") Integer dayDetailsId) {
		try {
			logger.info("deleteDayDetails controller start");
			return dayDetailsService.deleteDayDetails(dayDetailsId);
		} catch (Exception e) {
			logger.error("deleteDayDetails Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}
	
	@GetMapping("getAllSchoolDetails")
	public ResponseEntity<?> getAllSchoolDetails() {
		try {
			logger.info("getAllSchoolDetails controller start");
			return dayDetailsService.getAllSchoolDetails();
		} catch (Exception e) {
			logger.error("getAllSchoolDetails Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}
	
	@GetMapping("getAllClassDetails")
	public ResponseEntity<?> getAllClassDetails() {
		try {
			logger.info("getAllClassDetails controller start");
			return dayDetailsService.getAllClassDetails();
		} catch (Exception e) {
			logger.error("getAllClassDetails Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}

}
