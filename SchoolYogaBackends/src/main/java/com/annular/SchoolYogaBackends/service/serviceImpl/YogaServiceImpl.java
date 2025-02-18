package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.model.ClassDetails;
import com.annular.SchoolYogaBackends.model.MediaFileCategory;
import com.annular.SchoolYogaBackends.model.MediaFiles;
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
import com.annular.SchoolYogaBackends.webModel.QuestionInputModel;
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
			boolean exists = yogaRepository.existsByDayAndClassDetailsIds(yogaWebModel.getDay(),
					yogaWebModel.getClassDetailsId());
			if (exists) {
				logger.error("Yoga entry with day '{}' and classDetailsId '{}' already exists.", yogaWebModel.getDay(),
						yogaWebModel.getClassDetailsId());
				throw new IllegalArgumentException("A Yoga entry with this day and classDetailsId already exists.");
			}

			// Create and save a new Yoga post
			Yoga posts = Yoga.builder().yogaId(UUID.randomUUID().toString()).description(yogaWebModel.getDescription())
					.status(true).user(userFromDB).classDetailsId(yogaWebModel.getClassDetailsId())
					.day(yogaWebModel.getDay()).createdOn(new Date()).build();
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
						.map(question -> QuestionDetails.builder().yogaId(savedPost.getId())
								.questionDetails(question.getQuestionDetails()).questionType(question.getQuestionType())
								.answerA(question.getAnswerA()).answerB(question.getAnswerB())
								.answerC(question.getAnswerC()).answerD(question.getAnswerD())
								.questionDetailsIsActive(true)

								.questionDetailsCreatedOn(new Date()).build())
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
				List<QuestionWebModel> questionWebModels = questions.stream()
						.map(q -> QuestionWebModel.builder().questionDetailsId(q.getQuestionDetailsId())
								.questionDetails(q.getQuestionDetails()).questionType(q.getQuestionType())
								.answerA(q.getAnswerA()).answerB(q.getAnswerB()).answerC(q.getAnswerC())
								.answerD(q.getAnswerD()).questionDetailsIsActive(q.getQuestionDetailsIsActive())
								.questionDetailsCreatedOn(q.getQuestionDetailsCreatedOn()).build())
						.collect(Collectors.toList());

				// Fetch ClassDetails based on classDetailsId
				String classLevel = null;
				if (yoga.getClassDetailsId() != null) {
					Optional<ClassDetails> classDetailsOptional = classDetailsRepository
							.findById(yoga.getClassDetailsId());
					classLevel = classDetailsOptional.map(ClassDetails::getClassLevel).orElse(null);
				}

				// Build and return the YogaWebModel.
				// If YogaWebModel has a field for files, you can pass postFiles into its
				// builder.
				return YogaWebModel.builder().id(yoga.getId()).yogaId(yoga.getYogaId())
						.description(yoga.getDescription()).classDetailsId(yoga.getClassDetailsId())
						.classLevel(classLevel) // Replacing classDetailsId with classLevel
						.userId(yoga.getUser().getUserId())
						.day(yoga.getDay()).status(yoga.getStatus()).createdBy(yoga.getCreatedBy())
						.createdOn(yoga.getCreatedOn()).updatedBy(yoga.getUpdatedBy()).updatedOn(yoga.getUpdatedOn())
						.postFiles(postFiles).questionss(questionWebModels) // Ensure this field exists in YogaWebModel
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

	@Transactional
	@Override
	public boolean deleteYogaPostById(YogaWebModel yogaWebModel) {
		try {
			// Find the post by its ID
			Optional<Yoga> postData = yogaRepository.findById(yogaWebModel.getId());
			if (postData.isPresent()) {
				Yoga post = postData.get();
				post.setStatus(false);
				// Delete associated media files using the correct variable name
//	            mediaFilesService.deleteMediaFilesByCategoryAndRefIds(
//	                    MediaFileCategory.Yoga,
//	                    Collections.singletonList(post.getId())
//	            
//	                    
//	            );
				// Delete associated media files
				mediaFilesService.deleteMediaFilesByUserIdAndCategoryAndRefIds(post.getUser().getUserId(),
						MediaFileCategory.Yoga, yogaWebModel.getMediaFilesIds());
				// Save the updated post
				yogaRepository.save(post);
				questionDetailsRepository.softDeleteByYogaId(post.getId());
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

	@Override
	public YogaWebModel updateYogaWithFiles(YogaWebModel yogaWebModel) {
		try {
			// Retrieve the user from the database
			User userFromDB = userService.getUser(yogaWebModel.getUserId()).orElse(null);
			if (userFromDB == null) {
				logger.error("User not found for userId: {}", yogaWebModel.getUserId());
				return null;
			}
			// Check if the yoga post exists
			Yoga existingYoga = yogaRepository.findById(yogaWebModel.getId()).orElse(null);
			if (existingYoga == null) {
				logger.error("Yoga post not found for yogaId: {}", yogaWebModel.getYogaId());
				throw new IllegalArgumentException("Yoga post not found.");
			}

			// ✅ Check for duplicate entry before updating (excluding the current post)
			boolean exists = yogaRepository.existsByDayAndClassDetailsId(yogaWebModel.getDay(),
					yogaWebModel.getClassDetailsId());
			if (exists) {
				logger.error("Yoga entry with day '{}' and classDetailsId '{}' already exists.", yogaWebModel.getDay(),
						yogaWebModel.getClassDetailsId());
				throw new IllegalArgumentException("A Yoga entry with this day and classDetailsId already exists.");
			}

			// Update existing Yoga post
			existingYoga.setDescription(yogaWebModel.getDescription());
			existingYoga.setClassDetailsId(yogaWebModel.getClassDetailsId());
			existingYoga.setDay(yogaWebModel.getDay());
			existingYoga.setUpdatedOn(new Date()); // Assuming there's an `updatedOn` field

			Yoga updatedYoga = yogaRepository.saveAndFlush(existingYoga);

			// ✅ Delete specified files only
			if (!Utility.isNullOrEmptyList(yogaWebModel.getFileIds())) {
				mediaFilesService.deleteFilesByIds(yogaWebModel.getFileIds());
			}

			// ✅ Add new files if provided
			if (!Utility.isNullOrEmptyList(yogaWebModel.getFiles())) {
				FileInputWebModel fileInputWebModel = FileInputWebModel.builder().category(MediaFileCategory.Yoga)
						.categoryRefId(existingYoga.getId()).files(yogaWebModel.getFiles()).build();
				mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);
			}

			if (!Utility.isNullOrEmptyList(yogaWebModel.getQuestions())) {
				List<QuestionDetails> questionEntities = new ArrayList<>();

				for (QuestionInputModel question : yogaWebModel.getQuestions()) { // Use correct type
					if (question.getQuestionDetailsId() != null) {
						// ✅ Update existing question
						QuestionDetails existingQuestion = questionDetailsRepository
								.findById(question.getQuestionDetailsId()).orElse(null);
						if (existingQuestion != null) {
							existingQuestion.setQuestionDetails(question.getQuestionDetails());
							existingQuestion.setQuestionType(question.getQuestionType());
							existingQuestion.setAnswerA(question.getAnswerA());
							existingQuestion.setAnswerB(question.getAnswerB());
							existingQuestion.setAnswerC(question.getAnswerC());
							existingQuestion.setAnswerD(question.getAnswerD());
							existingQuestion.setQuestionDetailsIsActive(true);
							existingQuestion.setQuestionDetailsbyUpdatedOn(new Date());
							questionEntities.add(existingQuestion);
						}
					} else {
						// ✅ Add new question
						QuestionDetails newQuestion = QuestionDetails.builder().yogaId(existingYoga.getId())
								.questionDetails(question.getQuestionDetails()).questionType(question.getQuestionType())
								.answerA(question.getAnswerA()).answerB(question.getAnswerB())
								.answerC(question.getAnswerC()).answerD(question.getAnswerD())
								.questionDetailsIsActive(true).questionDetailsCreatedOn(new Date()).build();
						questionEntities.add(newQuestion);
					}
				}

				// Save all updates and new additions
				questionDetailsRepository.saveAll(questionEntities);
			}

			// ✅ Transform and return updated Yoga post
			List<YogaWebModel> responseList = this.transformPostsDataToYogaWebModel(List.of(updatedYoga));
			return responseList.isEmpty() ? null : responseList.get(0);

		} catch (IllegalArgumentException e) {
			logger.error("Validation error at updateYogaWithFiles() -> {}", e.getMessage());
			throw e; // Rethrow for proper handling in the controller
		} catch (Exception e) {
			logger.error("Error at updateYogaWithFiles() -> {}", e.getMessage(), e);
			throw new RuntimeException("An error occurred while updating Yoga post.");
		}
	}

	@Override
	public boolean deleteQuestionById(YogaWebModel yogaWebModel) {
		Optional<QuestionDetails> db = questionDetailsRepository.findById(yogaWebModel.getQuestionDetailsId());
		if (db.isPresent()) {
			QuestionDetails question = db.get();
			question.setQuestionDetailsIsActive(false);
			questionDetailsRepository.save(question); // Persist the change
			return true; // Return true since deletion (soft delete) was successful
		}
		return false; // Return false if the record was not found
	}
	@Override
	public boolean deleteMediaFilesById(YogaWebModel yogaWebModel) {
	    return mediaFilesService.deleteMediaFilesByUserIdAndCategoryAndRefIds(
	            MediaFileCategory.Yoga, yogaWebModel.getMediaFilesIds()
	    ); 
	}



}
