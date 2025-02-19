package com.annular.SchoolYogaBackends.service.serviceImpl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.model.ClassDetails;
import com.annular.SchoolYogaBackends.model.MediaFileCategory;
import com.annular.SchoolYogaBackends.model.QuestionDetails;
import com.annular.SchoolYogaBackends.model.StudentAnsReport;
import com.annular.SchoolYogaBackends.model.StudentTaskReports;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.model.Yoga;
import com.annular.SchoolYogaBackends.repository.ClassDetailsRepository;
import com.annular.SchoolYogaBackends.repository.QuestionDetailsRepository;
import com.annular.SchoolYogaBackends.repository.StudentTaskReportRepository;
import com.annular.SchoolYogaBackends.repository.UserRepository;
import com.annular.SchoolYogaBackends.repository.YogaRepository;
import com.annular.SchoolYogaBackends.service.AdminService;
import com.annular.SchoolYogaBackends.service.MediaFileService;
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;

@Service
public class AdminServiceImpl implements  AdminService {
	
	public static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	MediaFileService mediaFilesService;
	
	@Autowired
	ClassDetailsRepository classDetailsRepository;
	
	@Autowired
	YogaRepository yogaRepository;
	
	@Autowired
	QuestionDetailsRepository questionDetailsRepository;
	
	@Autowired
	StudentTaskReportRepository studentTaskReportsRepository;
	
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
	
	@Override
	public Map<String, Object> getAllStdIdAndDay(Integer stdId, String day, Integer userId) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        Yoga yoga = yogaRepository.findYogaByStdIdAndDay(stdId, day);
	        if (yoga == null) {
	            logger.warn("No yoga session found for student {} on day {}", stdId, day);
	            return Collections.emptyMap();
	        }

	        result.put("yogaId", yoga.getId());
	        result.put("day", yoga.getDay());
	        result.put("description", yoga.getDescription());

	        List<QuestionDetails> questionDetailsList = questionDetailsRepository.findByYogaId(yoga.getId());
	        List<Map<String, Object>> questionsList = new ArrayList<>();
	        
	        Map<Integer, Map<String, Object>> questionMap = new HashMap<>();

	        if (questionDetailsList != null && !questionDetailsList.isEmpty()) {
	            for (QuestionDetails question : questionDetailsList) {
	                Map<String, Object> questionData = new HashMap<>();
	                questionData.put("questionId", question.getQuestionDetailsId());
	                questionData.put("questionDetails", question.getQuestionDetails());
	                questionData.put("questionType", question.getQuestionType());
	                questionData.put("answerA", question.getAnswerA());
	                questionData.put("answerB", question.getAnswerB());
	                questionData.put("answerC", question.getAnswerC());
	                questionData.put("answerD", question.getAnswerD());
	                questionData.put("isActive", question.getQuestionDetailsIsActive());
	                questionMap.put(question.getQuestionDetailsId(), questionData);
	                questionsList.add(questionData);
	            }
	        }
	        result.put("questions", questionsList);

	        List<FileOutputWebModel> mediaFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Yoga, yoga.getId());
	        result.put("mediaFiles", mediaFiles != null ? mediaFiles : new ArrayList<>());

	        List<StudentTaskReports> taskReports = studentTaskReportsRepository.findByYogaIdAndUserIds(yoga.getId(), userId);
	        if (taskReports == null || taskReports.isEmpty()) {
	            logger.warn("No student task reports found for yoga session {} and user {}", yoga.getId(), userId);
	            result.put("taskReports", new ArrayList<>());
	            return result;
	        }

	        for (StudentTaskReports task : taskReports) {
	            for (StudentAnsReport ans : task.getStudentAnsReports()) {
	                int questionId = ans.getQuestionDetailsId();
	                Map<String, Object> questionData = questionMap.get(questionId);
	                if (questionData != null) {
	                    questionData.put("studentAnsReportId", ans.getStudentAnsReportId());
	                    questionData.put("answer", ans.getAns());
	                    questionData.put("isActive", ans.getStudentAnsReportIsActive());
	                }
	            }
	        }

	        result.put("taskReports", questionsList);
	        return result;

	    } catch (Exception e) {
	        logger.error("Error fetching yoga details for student {} on day {}: {}", stdId, day, e.getMessage(), e);
	        throw new ServiceException("Error fetching yoga details", e);
	    }
	}

}
