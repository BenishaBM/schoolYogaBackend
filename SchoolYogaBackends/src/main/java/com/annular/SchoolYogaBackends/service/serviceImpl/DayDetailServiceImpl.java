package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.model.Category;
import com.annular.SchoolYogaBackends.model.ClassDetails;
import com.annular.SchoolYogaBackends.model.DayDetails;
import com.annular.SchoolYogaBackends.model.SchoolDetails;
import com.annular.SchoolYogaBackends.repository.ClassDetailsRepository;
import com.annular.SchoolYogaBackends.repository.DayDetailsRepository;
import com.annular.SchoolYogaBackends.repository.SchoolDetailsRepository;
import com.annular.SchoolYogaBackends.service.DayDetailsService;
import com.annular.SchoolYogaBackends.webModel.DayDetailsWebModel;

@Service
public class DayDetailServiceImpl implements DayDetailsService{
	
	@Autowired
	DayDetailsRepository dayDetailsRepository;
	
	@Autowired
	SchoolDetailsRepository schoolDetailsRepository;
	
	@Autowired
	ClassDetailsRepository classDetailsRepository;
	
	public static final Logger logger = LoggerFactory.getLogger(DayDetailServiceImpl.class);

	@Override
	public ResponseEntity<?> saveDayDetails(DayDetailsWebModel dayDetailsWebModel) {
	    try {
	        // Check if DayDetails with the same 'days' value already exists.
	        Optional<DayDetails> existingDayDetails = dayDetailsRepository.findByDays(dayDetailsWebModel.getDays());
	        if (existingDayDetails.isPresent()) {
	            // Return a duplicate message response
	            return ResponseEntity.status(HttpStatus.CONFLICT)
	                                 .body(new Response(0, "Duplicate", "DayDetails already exist"));
	        }
	        
	        // Convert the DayDetailsWebModel to a DayDetails entity.
	        DayDetails dayDetails = DayDetails.builder()
	                .days(dayDetailsWebModel.getDays())
	                .dayDetailsIsActive(true)
	                .createdBy(dayDetailsWebModel.getCreatedBy())
	                .build();

	        // Save the entity using your repository
	        DayDetails savedDayDetails = dayDetailsRepository.save(dayDetails);
	        return ResponseEntity.ok(new Response(1, "Success", "DayDetails saved successfully"));
	      
	    } catch (Exception e) {
	        logger.error("Error saving day details: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Failed to save day details");
	    }
	}

	@Override
	public ResponseEntity<?> getAllDayDetails() {
		 try {
		        logger.info("Fetching all getAllDayDetails");

		        // Fetch all categories from the database
		        List<DayDetails> categories = dayDetailsRepository.findByDayDetailsIsActiveTrue();

		        // Check if categories list is empty
		        if (categories.isEmpty()) {
		            return ResponseEntity.ok(new Response(1, "No getAllDayDetails found", categories));
		        }

		        logger.info("getAllDayDetails retrieved successfully");
		        return ResponseEntity.ok(new Response(1, "getAllDayDetails retrieved successfully", categories));

		    } catch (Exception e) {
		        logger.error("Error retrieving getAllDayDetails: {}", e.getMessage(), e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new Response(-1, "Fail", e.getMessage()));
		    }
	}

	@Override
	public ResponseEntity<?> deleteDayDetails(Integer dayDetailsId) {
		 try {
		        logger.info("deleteCategory controller start");

		        if (dayDetailsId == null) {
		            return ResponseEntity.badRequest().body(new Response(0, "fail", "dayDetailsId is required"));
		        }

		        Optional<DayDetails> existingCategoryOpt = dayDetailsRepository.findById(dayDetailsId);
		        if (!existingCategoryOpt.isPresent()) {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(0, "fail", "DayDetails not found"));
		        }

		        DayDetails existingCategory = existingCategoryOpt.get();
		        existingCategory.setDayDetailsIsActive(false); // Soft delete by setting inactive
		        dayDetailsRepository.save(existingCategory);

		        return ResponseEntity.ok(new Response(1, "success", "dayDetails soft deleted successfully"));

		    } catch (Exception e) {
		        logger.error("dayDetails Method Exception: {}", e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new Response(-1, "Fail", "Internal Server Error"));
		    }
	}

	@Override
	public ResponseEntity<?> getAllSchoolDetails() {
		 try {
		        logger.info("Fetching all getAllSchoolDetails");

		        // Fetch all categories from the database
		        List<SchoolDetails> categories = schoolDetailsRepository.findBySchoolDetailsIsActiveTrue();

		        // Check if categories list is empty
		        if (categories.isEmpty()) {
		            return ResponseEntity.ok(new Response(1, "No getAllSchoolDetails found", categories));
		        }

		        logger.info("getAllSchoolDetails retrieved successfully");
		        return ResponseEntity.ok(new Response(1, "getAllSchoolDetails retrieved successfully", categories));

		    } catch (Exception e) {
		        logger.error("Error retrieving getAllSchoolDetails: {}", e.getMessage(), e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new Response(-1, "Fail", e.getMessage()));
		    }
	}

	@Override
	public ResponseEntity<?> getAllClassDetails() {
		 try {
		        logger.info("Fetching all getAllClassDetails");

		        // Fetch all categories from the database
		        List<ClassDetails> categories = classDetailsRepository.findByclassDetailsIsActiveTrue();

		        // Check if categories list is empty
		        if (categories.isEmpty()) {
		            return ResponseEntity.ok(new Response(1, "No getAllSchoolDetails found", categories));
		        }

		        logger.info("getAllClassDetails retrieved successfully");
		        return ResponseEntity.ok(new Response(1, "getAllClassDetails retrieved successfully", categories));

		    } catch (Exception e) {
		        logger.error("Error retrieving getAllClassDetails {}", e.getMessage(), e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new Response(-1, "Fail", e.getMessage()));
		    }
	}


}
