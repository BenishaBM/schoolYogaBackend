package com.annular.SchoolYogaBackends.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.model.StudentAnsReport;
import com.annular.SchoolYogaBackends.model.StudentTaskReports;
import com.annular.SchoolYogaBackends.service.MobileAppStudentService;
import com.annular.SchoolYogaBackends.webModel.StudentReportWebModel;

import software.amazon.awssdk.services.ssm.model.ResourceNotFoundException;

@RestController
@RequestMapping("/admin/user")
public class MobileAppStudentController {

	public static final Logger logger = LoggerFactory.getLogger(MobileAppStudentController.class);

	@Autowired
	MobileAppStudentService mobileAppService;

	@GetMapping("/getAllTaskDataByStdId")
	public ResponseEntity<Map<String, Object>> getAllTaskDataByStdId(@RequestParam("stdId") Integer stdId,@RequestParam("userId") Integer userId) {
		try {
			// Fetch data from service layer
			Map<String, Object> dbData = mobileAppService.getAllTaskDataByStdId(stdId,userId);

			// Return response with appropriate HTTP status
			return ResponseEntity.ok(dbData); // 200 OK

		} catch (Exception e) {
			logger.error("Error occurred while fetching student data for stdId: {} -> {}", stdId, e.getMessage(), e);

			// Return structured error response
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("status", -1);
			errorResponse.put("message", "An error occurred while retrieving student data.");
			errorResponse.put("data", Collections.emptyList());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/getAllStdIdAndDay")
	public Response getAllStdIdAndDay(@RequestParam("stdId") Integer stdId, @RequestParam("day") String day) {
		try {
			// Fetch data from the service layer
			Map<String, Object> dbData = mobileAppService.getAllStdIdAndDay(stdId, day);

			// If data is found, return success response
			return new Response(1, "Success", dbData);

		} catch (Exception e) {
			// Improved logging with schoolId for better tracing
			logger.error("Error occurred while fetching student data for schoolId: {} -> {}", stdId, e.getMessage(), e);

			// Return failure response with error message
			return new Response(0, "fail", "An error occurred while retrieving student data.");
		}
	}

	@PostMapping("saveStudentAns")
	public ResponseEntity<?> savetudentAns(@RequestBody StudentReportWebModel studentReportWebModel) {

		try {
			// Call the service method to update the answer
			StudentTaskReports updatedReport = mobileAppService.saveStudentAns(studentReportWebModel);
			return ResponseEntity.ok(updatedReport); // Return the updated report as response
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Resource not found error
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Internal server error
		}
	}
	
	@DeleteMapping("deleteByStudentCategoryId")
	public ResponseEntity<?> deleteByStudentCategoryId(@RequestParam("id") Integer id) {
	    try {
	        logger.info("deleteByStudentCategoryId request for userId: {}", id);
	        return mobileAppService.deleteByStudentCategoryId(id);
	    } catch (Exception e) {
	        logger.error("deleteByStudentCategoryId Method Exception: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new Response(-1, "Fail", e.getMessage()));
	    }
	}
}
