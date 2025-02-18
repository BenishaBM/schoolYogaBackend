package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
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
	public Map<String, Object> getAllTaskDataByStdId(Integer stdId) {
		Map<String, Object> responseMap = new LinkedHashMap<>(); // Preserve insertion order

		try {
			List<Yoga> yogaList = yogaRepository.findByClassDetailsId(stdId);

			if (!yogaList.isEmpty()) {
				List<Map<String, Object>> taskData = new ArrayList<>();

				for (Yoga yoga : yogaList) {
					Map<String, Object> yogaData = new HashMap<>();
					yogaData.put("yogaId", yoga.getYogaId());
					yogaData.put("description", yoga.getDescription());
					yogaData.put("status", yoga.getStatus());
					yogaData.put("day", yoga.getDay());
					yogaData.put("classDetailsId", yoga.getClassDetailsId());

					taskData.add(yogaData);
				}

				// Response in correct order
				responseMap.put("status", 1);
				responseMap.put("message", "Success");
				responseMap.put("data", taskData);

			} else {
				// No tasks found
				responseMap.put("status", 0);
				responseMap.put("message", "No tasks found for the provided standard ID.");
				responseMap.put("data", Collections.emptyList());
			}

		} catch (Exception e) {
			responseMap.put("status", -1);
			responseMap.put("message", "An error occurred while retrieving task data.");
			responseMap.put("data", Collections.emptyList());

			// logger.error("Error in getAllTaskDataByStdId for stdId {}: {}", stdId,
			// e.getMessage(), e);
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

//	@Override
//	public StudentTaskReports saveStudentAns(StudentReportWebModel studentTaskReportWebModel) {
//	    // Step 1: Create a new StudentTaskReport
//	    StudentTaskReports newTaskReport = new StudentTaskReports();
//	    newTaskReport.setYogaId(studentTaskReportWebModel.getYogaId());
//	    newTaskReport.setUserId(studentTaskReportWebModel.getUserId());
//	    newTaskReport.setStudentTaskReportIsActive(true);  // Mark as active
//	    newTaskReport.setCreatedBy(studentTaskReportWebModel.getCreatedBy());  // Set the creator ID
//	    newTaskReport.setClassDetailsId(studentTaskReportWebModel.getClassDetailsId());  // Set the associated class details ID
//	    newTaskReport.setCompletedStatus(false);  // Assuming task is incomplete by default
//	    
//	    // Save the new StudentTaskReport
//	    StudentTaskReports savedTaskReport = studentTaskReportRepository.save(newTaskReport);
//
//	    // Step 2: Iterate over the answers (StudentAnsReport)
//	    List<StudentAnsReport> savedReports = new ArrayList<>();
//	    for (StudentReportWebModel studentReportWebModel : studentTaskReportWebModel.getStudentReports()) {
//	        StudentAnsReport newReport = new StudentAnsReport();
//	        newReport.setAns(studentReportWebModel.getNewAnswer());  // Set the new answer
//	        newReport.setStudentTaskReportUpdatedBy(studentReportWebModel.getUpdatedBy());  // Set the "updated by" field
//	        newReport.setStudentTaskReportUpdatedOn(new Date());  // Set the "updated on" timestamp
//	        newReport.setStudentAnsReportIsActive(true);  // Mark as active, if necessary
//	        newReport.setCreatedBy(studentReportWebModel.getCreatedBy());  // Set the creator ID
//	        newReport.setQuestionDetailsId(studentReportWebModel.getQuestionDetailsId());  // Set the associated question details ID
//	        
//	        // Step 3: Set the relationship to the saved StudentTaskReport
//	        newReport.setStudentTaskReport(savedTaskReport);  // Link to the saved task report
//	        
//	        // Save the StudentAnsReport
//	        StudentAnsReport savedReport = studentAnsReportRepository.save(newReport);
//	        
//	        // Add the saved report to the list
//	        savedReports.add(savedReport);
//	    }
//
//	    // Step 4: Return the saved StudentTaskReport
//	    return savedTaskReport;
//	}


}
