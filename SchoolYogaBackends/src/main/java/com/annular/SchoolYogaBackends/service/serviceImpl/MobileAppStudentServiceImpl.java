package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.controller.MobileAppStudentController;
import com.annular.SchoolYogaBackends.model.MediaFileCategory;
import com.annular.SchoolYogaBackends.model.QuestionDetails;
import com.annular.SchoolYogaBackends.model.StudentAnsReport;
import com.annular.SchoolYogaBackends.model.StudentTaskReports;
import com.annular.SchoolYogaBackends.model.Yoga;
import com.annular.SchoolYogaBackends.repository.QuestionDetailsRepository;
import com.annular.SchoolYogaBackends.repository.StudentAnsReportRepository;
import com.annular.SchoolYogaBackends.repository.StudentTaskReportRepository;
import com.annular.SchoolYogaBackends.repository.YogaRepository;
import com.annular.SchoolYogaBackends.service.MediaFileService;
import com.annular.SchoolYogaBackends.service.MobileAppStudentService;
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;
import com.annular.SchoolYogaBackends.webModel.StudentAnswerWebModel;
import com.annular.SchoolYogaBackends.webModel.StudentReportWebModel;

@Service
public class MobileAppStudentServiceImpl implements MobileAppStudentService {

	public static final Logger logger = LoggerFactory.getLogger(MobileAppStudentServiceImpl.class);

	@Autowired
	YogaRepository yogaRepository;
	
	@Autowired
	MediaFileService mediaFilesService;
	
	@Autowired
	StudentTaskReportRepository studentTaskReportRepository;

	@Autowired
	QuestionDetailsRepository questionDetailsRepository;
	
	@Autowired
	StudentAnsReportRepository studentAnsReportRepository;

	@Override
	public Map<String, Object> getAllTaskDataByStdId(Integer stdId, Integer userId) {
	    Map<String, Object> responseMap = new LinkedHashMap<>();

	    try {
	        List<Yoga> yogaList = yogaRepository.findByClassDetailsId(stdId);

	        if (!yogaList.isEmpty()) {
	            List<Map<String, Object>> taskData = new ArrayList<>();

	            for (Yoga yoga : yogaList) {
	                Map<String, Object> yogaData = new HashMap<>();
	                yogaData.put("yogaId", yoga.getId());
	                yogaData.put("description", yoga.getDescription());
	                yogaData.put("status", yoga.getStatus());
	                yogaData.put("day", yoga.getDay());
	                yogaData.put("classDetailsId", yoga.getClassDetailsId());

	                System.out.println(">>>>>>> Fetching data for yogaId: " + yoga.getId());

	                // Fetch task report
	                StudentTaskReports taskReport = studentTaskReportRepository.findByYogaIdAndUserId(yoga.getId(), userId);
	                
	                if (taskReport != null) {
	                    yogaData.put("completedStatus", taskReport.getCompletedStatus() != null ? taskReport.getCompletedStatus() : false);
	                } else {
	                    yogaData.put("completedStatus", false);
	                }

	                taskData.add(yogaData);
	            }

	            responseMap.put("status", 1);
	            responseMap.put("message", "Success");
	            responseMap.put("data", taskData);
	        } else {
	            responseMap.put("status", 0);
	            responseMap.put("message", "No tasks found for the provided standard ID.");
	            responseMap.put("data", Collections.emptyList());
	        }
	    } catch (Exception e) {
	        responseMap.put("status", -1);
	        responseMap.put("message", "An error occurred while retrieving task data: " + e.getMessage());
	        responseMap.put("data", Collections.emptyList());

	        e.printStackTrace();
	    }

	    return responseMap;
	}

	@Override
	public Map<String, Object> getAllStdIdAndDay(Integer stdId, String day) {
	    try {
	        // Fetch Yoga entity by stdId and day
	        Optional<Yoga> optionalYoga = Optional.ofNullable(yogaRepository.findYogaByStdIdAndDay(stdId, day));

	        if (optionalYoga.isEmpty()) {
	            logger.warn("No yoga session found for student {} on day {}", stdId, day);
	            return Collections.emptyMap(); // Returning an empty response instead of null
	        }

	        Yoga yoga = optionalYoga.get();
	        Map<String, Object> result = new HashMap<>();
	        result.put("yogaId", yoga.getId());
	        result.put("day", yoga.getDay());
	        result.put("description", yoga.getDescription());

	        // Fetch related QuestionDetails
	        List<QuestionDetails> questionDetailsList = questionDetailsRepository.findByYogaId(yoga.getId());

	        // Transforming question details into a structured format
	        List<Map<String, Object>> questionsList = questionDetailsList.stream().map(question -> {
	            Map<String, Object> questionMap = new HashMap<>();
	            questionMap.put("questionId", question.getQuestionDetailsId());
	            questionMap.put("questionDetails", question.getQuestionDetails());
	            questionMap.put("questionType", question.getQuestionType());
	            questionMap.put("answerA", question.getAnswerA());
	            questionMap.put("answerB", question.getAnswerB());
	            questionMap.put("answerC", question.getAnswerC());
	            questionMap.put("answerD", question.getAnswerD());
	            questionMap.put("isActive", question.getQuestionDetailsIsActive());
	            return questionMap;
	        }).collect(Collectors.toList());

	        result.put("questions", questionsList);

	        // Fetch media files associated with the Yoga session
	        List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(
	                MediaFileCategory.Yoga, yoga.getId());

	        result.put("mediaFiles", postFiles); // Including media files in the response

	        return result;
	    } catch (Exception e) {
	        logger.error("Error fetching yoga details for student {} on day {}: {}", stdId, day, e.getMessage(), e);
	        throw new ServiceException("Error fetching yoga details", e);
	    }
	}

	@Override
	public StudentTaskReports saveStudentAns(StudentReportWebModel studentTaskReportWebModel) {
	    // Validate input data
	    validateStudentTaskReport(studentTaskReportWebModel);
	    
	    // Step 1: Create and save StudentTaskReport
	    StudentTaskReports savedTaskReport = createAndSaveTaskReport(studentTaskReportWebModel);
	    
	    // Step 2: Save Student Answers if provided
	    if (studentTaskReportWebModel.getStudentReports() != null && !studentTaskReportWebModel.getStudentReports().isEmpty()) {
	        saveStudentAnswers(studentTaskReportWebModel.getStudentReports(), savedTaskReport);
	    }
	    
	    return savedTaskReport;
	}

	private void validateStudentTaskReport(StudentReportWebModel model) {
	    if (model == null) {
	        throw new IllegalArgumentException("StudentTaskReportWebModel cannot be null");
	    }
	    
	    List<String> missingFields = new ArrayList<>();
	    if (model.getUserId() == null) missingFields.add("userId");
	    if (model.getYogaId() == null) missingFields.add("yogaId");
	    if (model.getCreatedBy() == null) missingFields.add("createdBy");
	    if (model.getClassDetailsId() == null) missingFields.add("classDetailsId");
	    
	    if (!missingFields.isEmpty()) {
	        throw new IllegalArgumentException("Missing required fields: " + String.join(", ", missingFields));
	    }

	    // Validate student reports if present
	    if (model.getStudentReports() != null) {
	        for (StudentAnswerWebModel answer : model.getStudentReports()) {
	            validateStudentAnswer(answer);
	        }
	    }
	}

	private void validateStudentAnswer(StudentAnswerWebModel answer) {
	    if (answer == null) {
	        throw new IllegalArgumentException("Student answer cannot be null");
	    }

	    List<String> missingFields = new ArrayList<>();
	    if (answer.getNewAnswer() == null) missingFields.add("newAnswer");
	    if (answer.getUpdatedBy() == null) missingFields.add("updatedBy");
	    if (answer.getCreatedBy() == null) missingFields.add("createdBy");
	    if (answer.getQuestionDetailsId() == null) missingFields.add("questionDetailsId");

	    if (!missingFields.isEmpty()) {
	        throw new IllegalArgumentException("Missing required fields in student answer: " + String.join(", ", missingFields));
	    }
	}

	private StudentTaskReports createAndSaveTaskReport(StudentReportWebModel model) {
	    StudentTaskReports newTaskReport = new StudentTaskReports();
	    newTaskReport.setYogaId(model.getYogaId());
	    newTaskReport.setUserId(model.getUserId());
	    newTaskReport.setStudentTaskReportIsActive(true);
	    newTaskReport.setCreatedBy(model.getCreatedBy());
	    newTaskReport.setClassDetailsId(model.getClassDetailsId());
	    newTaskReport.setCompletedStatus(model.getCompletedStatus());
	    newTaskReport.setStudentTaskReportCreatedOn(new Date());
	    
	    try {
	        return studentTaskReportRepository.save(newTaskReport);
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to save student task report: " + e.getMessage(), e);
	    }
	}

	private void saveStudentAnswers(List<StudentAnswerWebModel> answers, StudentTaskReports taskReport) {
	    List<StudentAnsReport> savedReports = new ArrayList<>();
	    
	    for (StudentAnswerWebModel answer : answers) {
	        try {
	            StudentAnsReport newReport = createStudentAnsReport(answer, taskReport);
	            savedReports.add(studentAnsReportRepository.save(newReport));
	        } catch (Exception e) {
	            logger.error("Failed to save student answer: " + e.getMessage(), e);
	            throw new RuntimeException("Failed to save student answer for question " + answer.getQuestionDetailsId(), e);
	        }
	    }
	}

	private StudentAnsReport createStudentAnsReport(StudentAnswerWebModel answer, StudentTaskReports taskReport) {
	    StudentAnsReport newReport = new StudentAnsReport();
	    newReport.setAns(answer.getNewAnswer());
	    newReport.setStudentTaskReportUpdatedBy(answer.getUpdatedBy());
	    newReport.setStudentTaskReportUpdatedOn(new Date());
	    newReport.setStudentAnsReportIsActive(true);
	    newReport.setCreatedBy(answer.getCreatedBy());
	    newReport.setStudentTaskReport(taskReport);
	    newReport.setQuestionDetailsId(answer.getQuestionDetailsId());  // Added this line
	    return newReport;
	}
}
