package com.annular.SchoolYogaBackends.service.serviceImpl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.model.ClassDetails;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.repository.ClassDetailsRepository;
import com.annular.SchoolYogaBackends.repository.UserRepository;
import com.annular.SchoolYogaBackends.service.AdminService;

@Service
public class AdminServiceImpl implements  AdminService {
	
	public static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	ClassDetailsRepository classDetailsRepository;
	
	@Override
	public Map<String, Object> getAllStudentDataBySchoolId(Integer schoolId) {
	    Map<String, Object> responseMap = new HashMap<>();
	    
	    try {
	        // Fetch users based on schoolId (modify this according to your repository)
	        List<User> userList = userRepo.findBySchoolId(schoolId); // Assuming findBySchoolId returns a List<User>
	        
	        if (userList != null && !userList.isEmpty()) {
	            // Prepare a list to hold the data for the response
	            List<Map<String, Object>> responseData = new ArrayList<>();
	            
	            // Iterate over the user list
	            for (User user : userList) {
	                Map<String, Object> userData = new HashMap<>();
	                
	                // Fetching class level from ClassDetails table
	                String classLevel = null;
	                if (user.getStd() != null) {
	                    Optional<ClassDetails> classOptional = classDetailsRepository.findById(user.getStd());
	                    classLevel = classOptional.map(ClassDetails::getClassLevel).orElse("N/A"); // Default to "N/A" if not found
	                }
	                
	                // Populate the response map with user data
	                userData.put("userId", user.getUserId());
	                userData.put("userName", user.getUserName());
	                userData.put("emailId", user.getEmailId());
	                userData.put("rollNumber", user.getRollNo());
	                userData.put("std", classLevel); // Class level is added here
	                userData.put("schoolName", user.getSchoolName() != null ? user.getSchoolName() : "Unknown School"); // Handle null schoolName
	                
	                responseData.add(userData);
	            }
	            
	            // Add the data and success message to the response map
	            responseMap.put("status", 1);
	            responseMap.put("message", "Success");
	            responseMap.put("data", responseData);
	        } else {
	            // No users found for the provided schoolId
	            responseMap.put("status", 0);
	            responseMap.put("message", "No students found for the provided schoolId.");
	            responseMap.put("data", null);
	        }
	    } catch (Exception e) {
	        // Log the exception for debugging
	        logger.error("Error occurred while fetching student data for schoolId: {} -> {}", schoolId, e.getMessage(), e);
	        
	        // Return failure response with error message
	        responseMap.put("status", -1);
	        responseMap.put("message", "An error occurred while retrieving student data.");
	        responseMap.put("data", null);
	    }
	    
	    return responseMap; // Return the HashMap containing response
	}


}
