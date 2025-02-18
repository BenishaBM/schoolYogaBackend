package com.annular.SchoolYogaBackends.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.service.AdminService;

@RestController
@RequestMapping("/admin/user")
public class AdminController {
	
	@Autowired
	AdminService adminService;
	
	public static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@GetMapping("/getAllStudentDataBySchoolId")
	public Response getAllStudentDataBySchoolId(@RequestParam("schoolId") Integer schoolId) {
	    try {
	        // Fetch data from the service layer
	        Map<String, Object> dbData = adminService.getAllStudentDataBySchoolId(schoolId);
	        
	        // If data is found, return success response
	        return new Response(1, "Success", dbData);
	        
	    } catch (Exception e) {
	        // Improved logging with schoolId for better tracing
	        logger.error("Error occurred while fetching student data for schoolId: {} -> {}", schoolId, e.getMessage(), e);
	        
	        // Return failure response with error message
	        return new Response(0, "fail", "An error occurred while retrieving student data.");
	    }
	}



}
