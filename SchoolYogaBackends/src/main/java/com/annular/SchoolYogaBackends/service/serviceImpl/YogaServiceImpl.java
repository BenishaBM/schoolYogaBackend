package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.model.ClassDetails;
import com.annular.SchoolYogaBackends.model.MediaFileCategory;
import com.annular.SchoolYogaBackends.model.QuestionDetails;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.model.Yoga;
import com.annular.SchoolYogaBackends.repository.ClassDetailsRepository;
import com.annular.SchoolYogaBackends.repository.QuestionDetailsRepository;
import com.annular.SchoolYogaBackends.repository.YogaRepository;
import com.annular.SchoolYogaBackends.service.MediaFileService;
import com.annular.SchoolYogaBackends.service.UserService;
import com.annular.SchoolYogaBackends.service.YogaService;
import com.annular.SchoolYogaBackends.util.Utility;
import com.annular.SchoolYogaBackends.webModel.FileInputWebModel;
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;
import com.annular.SchoolYogaBackends.webModel.QuestionWebModel;
import com.annular.SchoolYogaBackends.webModel.YogaWebModel;

@Service
public class YogaServiceImpl implements YogaService {

	@Autowired
	YogaRepository yogaRepository;

	public static final Logger logger = LoggerFactory.getLogger(YogaServiceImpl.class);

	@Autowired
	MediaFileService mediaFilesService;

	@Autowired
	UserService userService;
	
	@Autowired
	ClassDetailsRepository classDetailsRepository;
	
	@Autowired
	QuestionDetailsRepository questionDetailsRepository;
//
	@Override
	public YogaWebModel saveYogaWithFiles(YogaWebModel yogaWebModel) {
		try {
			// Retrieve the user from the database
			User userFromDB = userService.getUser(yogaWebModel.getUserId()).orElse(null);
			if (userFromDB == null) {
				logger.error("User not found for userId: {}", yogaWebModel.getUserId());
				return null;
			}
			logger.info("User found: {}", userFromDB.getUserName());
			
			// ✅ Check for duplicate entry before saving
	        boolean exists = yogaRepository.existsByDayAndClassDetailsId(yogaWebModel.getDay(), yogaWebModel.getClassDetailsId());
	        if (exists) {
	            logger.error("Yoga entry with day '{}' and classDetailsId '{}' already exists.", yogaWebModel.getDay(), yogaWebModel.getClassDetailsId());
	            throw new IllegalArgumentException("A Yoga entry with this day and classDetailsId already exists.");
	        }

			// Create and save a new Yoga post
			Yoga posts = Yoga.builder().yogaId(UUID.randomUUID().toString()).description(yogaWebModel.getDescription())
					.status(true)
					.classDetailsId(yogaWebModel.getClassDetailsId())
					.day(yogaWebModel.getDay())
					.createdOn(new Date()).build();
			Yoga savedPost = yogaRepository.saveAndFlush(posts);

			// If files are provided, save them in the media_files table
			if (!Utility.isNullOrEmptyList(yogaWebModel.getFiles())) {
				FileInputWebModel fileInputWebModel = FileInputWebModel.builder().category(MediaFileCategory.Yoga)
						.categoryRefId(savedPost.getId()).files(yogaWebModel.getFiles()).build();
				mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);
			}


			// Save associated questions if available
			if (!Utility.isNullOrEmptyList(yogaWebModel.getQuestions())) {
			    List<QuestionDetails> questionEntities = yogaWebModel.getQuestions().stream()
			        .map(question -> QuestionDetails.builder()
			                .yogaId(savedPost.getId()) 
			                .questionDetails(question.getQuestionDetails())
			                .questionType(question.getQuestionType())
			                .answerA(question.getAnswerA())
			                .answerB(question.getAnswerB())
			                .answerC(question.getAnswerC())
			                .answerD(question.getAnswerD())
			                .questionDetailsIsActive(true)

			                .questionDetailsCreatedOn(new Date()) 
			                .build())
			        .collect(Collectors.toList());

			    questionDetailsRepository.saveAll(questionEntities);
			}

	           
			// Transform the saved Yoga post into a YogaWebModel and return it
			List<YogaWebModel> responseList = this.transformPostsDataToYogaWebModel(List.of(savedPost));
			return responseList.isEmpty() ? null : responseList.get(0);
		} catch (Exception e) {
			logger.error("Error at saveYogaWithFiles() -> {}", e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private List<YogaWebModel> transformPostsDataToYogaWebModel(List<Yoga> yogaList) {
		try {
			if (Utility.isNullOrEmptyList(yogaList)) {
				return Collections.emptyList();
			}

			return yogaList.stream().filter(Objects::nonNull).map(yoga -> {
				// Optionally, fetch media files associated with the yoga post.
				List<FileOutputWebModel> postFiles = mediaFilesService
						.getMediaFilesByCategoryAndRefId(MediaFileCategory.Yoga, yoga.getId());
				
				// Fetch questions associated with the yoga post.
	            List<QuestionDetails> questions = questionDetailsRepository.findByYogaId(yoga.getId());

	            // Map QuestionDetails entities to YogaWebModel's question format
	            List<QuestionWebModel> questionWebModels = questions.stream().map(q -> 
	                QuestionWebModel.builder()
	                    .questionDetailsId(q.getQuestionDetailsId())
	                    .questionDetails(q.getQuestionDetails())
	                    .questionType(q.getQuestionType())
	                    .answerA(q.getAnswerA())
	                    .answerB(q.getAnswerB())
	                    .answerC(q.getAnswerC())
	                    .answerD(q.getAnswerD())
	                    .questionDetailsIsActive(q.getQuestionDetailsIsActive())
	                    .questionDetailsCreatedOn(q.getQuestionDetailsCreatedOn())
	                    .build()
	            ).collect(Collectors.toList());

	         // Fetch ClassDetails based on classDetailsId
	            String classLevel = null;
	            if (yoga.getClassDetailsId() != null) {
	                Optional<ClassDetails> classDetailsOptional = classDetailsRepository.findById(yoga.getClassDetailsId());
	                classLevel = classDetailsOptional.map(ClassDetails::getClassLevel).orElse(null);
	            }
				
				// Build and return the YogaWebModel.
				// If YogaWebModel has a field for files, you can pass postFiles into its
				// builder.
				return YogaWebModel.builder().id(yoga.getId()).yogaId(yoga.getYogaId())
						.description(yoga.getDescription())
						.classDetailsId(yoga.getClassDetailsId())
						 .classLevel(classLevel) // Replacing classDetailsId with classLevel
						.day(yoga.getDay())
						.status(yoga.getStatus()).createdBy(yoga.getCreatedBy())
						.createdOn(yoga.getCreatedOn()).updatedBy(yoga.getUpdatedBy()).updatedOn(yoga.getUpdatedOn())
						.postFiles(postFiles)
						.questionss(questionWebModels) // Ensure this field exists in YogaWebModel
//                                //.files(postFiles) // Uncomment if YogaWebModel has a files field
						.build();
			}).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error in transformPostsDataToYogaWebModel: {}", e.getMessage(), e);
			return Collections.emptyList();
		}
	}


	@Override
	public List<YogaWebModel> getAllUsersPosts() {
		try {
			List<Yoga> postList = yogaRepository.getAllActivePosts();
			if (postList == null || postList.isEmpty()) {
				return Collections.emptyList();
			}
			// Transform the posts into YogaWebModel and return
			return this.transformPostsDataToYogaWebModel(postList);
		} catch (Exception e) {
			logger.error("Error in getAllUsersPosts(): {}", e.getMessage(), e);
			return Collections.emptyList(); // or return null; if you prefer
		}
	}

	

	@Override
	public YogaWebModel getPostByYogaId(Integer id) {
	    Optional<Yoga> postOptional = yogaRepository.findByYogaId(id);
	    if (postOptional.isEmpty()) {
	        return null;
	    }
	    List<YogaWebModel> responseList = this.transformPostsDataToYogaWebModel(List.of(postOptional.get()));
	    return responseList.isEmpty() ? null : responseList.get(0);
	}

	@Override
	public boolean deleteYogaPostById(YogaWebModel yogaWebModel) {
	    try {
	        // Find the post by its ID
	        Optional<Yoga> postData = yogaRepository.findById(yogaWebModel.getId());
	        if (postData.isPresent()) {
	            Yoga post = postData.get();
	            post.setStatus(false);
	            // Delete associated media files using the correct variable name
	            mediaFilesService.deleteMediaFilesByCategoryAndRefIds(
	                    MediaFileCategory.Yoga,
	                    Collections.singletonList(post.getId())
	            
	                    
	            );
	            // Save the updated post
	            yogaRepository.save(post);
	            return true;
	        } else {
	            return false; // Post not found
	        }
	    } catch (Exception e) {
	        // Log the exception
	        e.printStackTrace();
	        return false;
	    }
	}


}
