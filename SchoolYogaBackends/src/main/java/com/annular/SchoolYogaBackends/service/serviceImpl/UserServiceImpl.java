package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.model.AvatarImage;
import com.annular.SchoolYogaBackends.model.Category;
import com.annular.SchoolYogaBackends.model.ClassDetails;
import com.annular.SchoolYogaBackends.model.RefreshToken;
import com.annular.SchoolYogaBackends.model.SchoolDetails;
import com.annular.SchoolYogaBackends.model.SmileImage;
import com.annular.SchoolYogaBackends.model.StudentCategoryDetails;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.repository.AvartarImageRepository;
import com.annular.SchoolYogaBackends.repository.CategoryRepository;
import com.annular.SchoolYogaBackends.repository.ClassDetailsRepository;
import com.annular.SchoolYogaBackends.repository.RefreshTokenRepository;
import com.annular.SchoolYogaBackends.repository.SchoolDetailsRepository;
import com.annular.SchoolYogaBackends.repository.SmileImageRepository;
import com.annular.SchoolYogaBackends.repository.StudentCategoryDetailsRepository;
import com.annular.SchoolYogaBackends.repository.UserRepository;
//import com.annular.SchoolYogaBackends.service.MediaFileService;
import com.annular.SchoolYogaBackends.service.UserService;
import com.annular.SchoolYogaBackends.util.S3Util;
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;
import com.annular.SchoolYogaBackends.webModel.UserWebModel;




@Service
public class UserServiceImpl implements UserService {

	public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AvartarImageRepository avatarImageRepository;
	
	@Autowired
	SmileImageRepository smileImageRepository;
	
    @Autowired
    S3Util s3Util;

//    @Autowired
//    MediaFileService mediaFilesService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	SchoolDetailsRepository schoolDetailsRepository;
	
	@Autowired
	ClassDetailsRepository classDetailsRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	StudentCategoryDetailsRepository studentCategoryDetailsRepository;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Override
	public ResponseEntity<?> register(UserWebModel userWebModel) {
	    HashMap<String, Object> response = new HashMap<>();
	    try {
	        logger.info("Register method start");
	        // Check if user already exists
	        Optional<User> existingUser = userRepository.findByEmailIdAndUserType(userWebModel.getEmailId(), userWebModel.getUserType());
	        if (existingUser.isPresent()) {
	            response.put("message", "User with this email already exists");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }

	        // Create new user entity
	        User newUser = User.builder()
	                .emailId(userWebModel.getEmailId())
	                .password(passwordEncoder.encode(userWebModel.getPassword()))
	                .userType(userWebModel.getUserType())
	                .userIsActive(true)
	                .age(userWebModel.getAge())
	                .frdName(userWebModel.getFrdName())
	                .frdDescription(userWebModel.getFrdDescription())
	                .schoolName(userWebModel.getSchoolName())
	                .rollNo(userWebModel.getRollNo())
	                .std(userWebModel.getStd())
	                .profilePic(userWebModel.getProfilePic())
	                .smilePic(userWebModel.getSmilePic())
	                .createdBy(userWebModel.getCreatedBy())
	                .userName(userWebModel.getUserName())
	                .empId(userWebModel.getEmpId())
	                .build();

	        // Save user
	        User savedUser = userRepository.save(newUser);

	        // Handle multiple categories: only add a category if a valid categoryId is passed
	        if (userWebModel.getCategoryNames() != null && !userWebModel.getCategoryNames().isEmpty()) {
	            List<HashMap<String, Object>> categories = userWebModel.getCategoryNames();
	            for (HashMap<String, Object> categoryMap : categories) {
	                // Process only if "categoryId" is provided
	                if (categoryMap.containsKey("categoryId")) {
	                    Integer categoryId = Integer.valueOf(categoryMap.get("categoryId").toString());
	                    Category category = categoryRepository.findById(categoryId).orElse(null);
	                    
	                    if (category != null) {
	                        // Create StudentCategoryDetails linking the saved user to the existing category
	                        StudentCategoryDetails studentCategoryDetails = StudentCategoryDetails.builder()
	                                .studentCategoryIsActive(true)
	                                .studentCategoryCreatedBy(savedUser.getUserId())
	                                .category(category)
	                                .user(savedUser)
	                                .build();
	                        studentCategoryDetailsRepository.save(studentCategoryDetails);
	                    }
	                }
	            }
	        }

	        return ResponseEntity.ok(new Response(1, "success", "User registered successfully"));
	    } catch (Exception e) {
	        logger.error("Error registering user: " + e.getMessage(), e);
	        response.put("message", "Registration failed");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}


	@Override
	public RefreshToken createRefreshToken(User user) {
		try {
			logger.info("createRefreshToken method start");

			// Find the user by username and userType
			Optional<User> checkUser = userRepository.findByEmailId(user.getEmailId());

			// Check if the user is present
			if (checkUser.isPresent()) {
				User users = checkUser.get(); // Get the actual user

				// Create and set refresh token details
				RefreshToken refreshToken = new RefreshToken();
				refreshToken.setUserId(users.getUserId()); // Set userId from the found user
				refreshToken.setToken(UUID.randomUUID().toString()); // Generate a random token
				// refreshToken.setExpiryToken(LocalTime.now().plusMinutes(45)); // Uncomment if
				// expiry is needed

				// Save the refresh token to the repository
				refreshToken = refreshTokenRepository.save(refreshToken);

				logger.info("createRefreshToken method end");
				return refreshToken;
			} else {
				logger.warn("User not found for username: " + user.getEmailId());
				return null; // Return null if user is not found
			}
		} catch (Exception e) {
			logger.error("Error in createRefreshToken method: ", e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ResponseEntity<Response> getUserDetailsById(int userId) {
	    logger.info("Fetching user details for userId: {}", userId);

	    try {
	        Optional<User> userOptional = userRepository.findById(userId);

	        if (userOptional.isEmpty()) {
	            logger.warn("User not found with userId: {}", userId);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(new Response(0, "Fail", Map.of("error", "User not found")));
	        }

	        User user = userOptional.get();
	        Map<String, Object> responseMap = new HashMap<>();
	        Map<String, Object> userDetailsMap = new HashMap<>();

	        // Fetching school name from SchoolDetails table
	        String schoolName = null;
	        if (user.getSchoolName() != null) {
	            Optional<SchoolDetails> schoolOptional = schoolDetailsRepository.findById(user.getSchoolName());
	            schoolName = schoolOptional.map(SchoolDetails::getSchoolDetailsName).orElse(null);
	        }

	        // Fetching class level from ClassDetails table
	        String classLevel = null;
	        if (user.getStd() != null) {
	            Optional<ClassDetails> classOptional = classDetailsRepository.findById(user.getStd());
	            classLevel = classOptional.map(ClassDetails::getClassLevel).orElse(null);
	        }

	        // Fetching profile and smile picture paths
	        String profilePicUrl = user.getProfilePic() != null ? 
	            "https://schoolyogabackend.s3.ap-south-1.amazonaws.com/" + user.getProfilePic() : null;

	        String smilePicUrl = user.getSmilePic() != null ? 
	            "https://schoolyogabackend.s3.ap-south-1.amazonaws.com/" + user.getSmilePic() : null;

	        // Constructing response using HashMap
	        userDetailsMap.put("emailId", user.getEmailId());
	        userDetailsMap.put("userType", user.getUserType());
	        userDetailsMap.put("userIsActive", user.getUserIsActive());
	        userDetailsMap.put("createdBy", user.getCreatedBy());
	        userDetailsMap.put("userCreatedOn", user.getUserCreatedOn());
	        userDetailsMap.put("userUpdatedBy", user.getUserUpdatedBy());
	        userDetailsMap.put("userUpdatedOn", user.getUserUpdatedOn());
	        userDetailsMap.put("userName", user.getUserName());
	        userDetailsMap.put("gender", user.getGender());
	        userDetailsMap.put("rollNo", user.getRollNo());
	        userDetailsMap.put("schoolName", schoolName);
	        userDetailsMap.put("std", classLevel);
	        userDetailsMap.put("profilePic", profilePicUrl);
	        userDetailsMap.put("smilePic", smilePicUrl);
	        userDetailsMap.put("frdName", user.getFrdName());
	        userDetailsMap.put("age", user.getAge());
	        userDetailsMap.put("frdDescription", user.getFrdDescription());
	        userDetailsMap.put("empId", user.getEmpId());

	        // Fetching StudentCategoryDetails
	        List<StudentCategoryDetails> studentCategoryDetailsList = studentCategoryDetailsRepository.findByUser(user);
	        List<Map<String, Object>> categoryDetailsList = new ArrayList<>();

	        for (StudentCategoryDetails studentCategory : studentCategoryDetailsList) {
	            Map<String, Object> categoryDetails = new HashMap<>();
	            Category category = studentCategory.getCategory();

	            categoryDetails.put("categoryId", category.getCategoryId());
	            categoryDetails.put("categoryName", category.getCategoryName());  // Assuming Category has a name
	            categoryDetails.put("studentCategoryIsActive", studentCategory.getStudentCategoryIsActive());
	            categoryDetails.put("studentCategoryCreatedOn", studentCategory.getStudentCategoryCreatedOn());
	            categoryDetails.put("studentCategoryUpdatedOn", studentCategory.getStudentCategoryUpdatedOn());

	            categoryDetailsList.add(categoryDetails);
	        }

	        userDetailsMap.put("studentCategories", categoryDetailsList);
	        responseMap.put("userDetails", userDetailsMap);

	        logger.info("User details retrieved successfully for userId: {}", userId);
	        return ResponseEntity.ok(new Response(1, "User details retrieved successfully", responseMap));

	    } catch (Exception e) {
	        logger.error("Error fetching user details for userId {}: {}", userId, e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(new Response(0, "Error", Map.of("error", "Internal Server Error")));
	    }
	}


	@Override
	public ResponseEntity<?> deleteUserDetails(Integer userId) {
	    if (userId == null) {
	        return ResponseEntity.badRequest().body(new Response(-1, "Fail", "User ID must not be null"));
	    }
	    
	    Optional<User> userOptional = userRepository.findById(userId);
	    if (userOptional.isPresent()) {
	        User user = userOptional.get();
	        user.setUserIsActive(false); // Set user as inactive
	        userRepository.save(user); // Save changes to the database
	        return ResponseEntity.ok(new Response(1, "success", "delete details successfully"));
	    } else {
	    	return ResponseEntity.badRequest().body(new Response(-1, "Fail", "User not found"));
	    }
	}

	@Override
	public ResponseEntity<Response> updateUserDetails(UserWebModel userWebModel) {
	    logger.info("Updating user details for userId: {}", userWebModel.getUserId());
	    try {
	        Optional<User> optionalUser = userRepository.findById(userWebModel.getUserId());
	        if (optionalUser.isPresent()) {
	            User user = optionalUser.get();
	            
	         // Check and update email if provided and different from current value
	            if (userWebModel.getEmailId() != null && !userWebModel.getEmailId().equals(user.getEmailId())) {
	                Optional<User> emailUser = userRepository.findByEmailIdAndUserType(userWebModel.getEmailId(), user.getUserType());
	                if (emailUser.isPresent() && !emailUser.get().getUserId().equals(user.getUserId())) {
	                    logger.warn("Email already exists: {}", userWebModel.getEmailId());
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body(new Response(0, "Fail", "Email already exists"));
	                }
	                user.setEmailId(userWebModel.getEmailId());
	            }

	            
	            // Update other fields only if new data is provided (i.e., not null)
	            if (userWebModel.getUserName() != null) {
	                user.setUserName(userWebModel.getUserName());
	            }
	            if (userWebModel.getSchoolName() != null) {
	                user.setSchoolName(userWebModel.getSchoolName());
	            }
	            if (userWebModel.getRollNo() != null) {
	                user.setRollNo(userWebModel.getRollNo());
	            }
	            if (userWebModel.getEmpId() != null) {
	                user.setEmpId(userWebModel.getEmpId());
	            }
	            if (userWebModel.getFrdName() != null) {
	                user.setFrdName(userWebModel.getFrdName());
	            }
	            if (userWebModel.getFrdDescription() != null) {
	                user.setFrdDescription(userWebModel.getFrdDescription());
	            }
	            if (userWebModel.getProfilePic() != null) {
	                user.setProfilePic(userWebModel.getProfilePic());
	            }
	            if (userWebModel.getSmilePic() != null) {
	                user.setSmilePic(userWebModel.getSmilePic());
	            }
	            if (userWebModel.getStd() != null) {
	                user.setStd(userWebModel.getStd());
	            }
	            if (userWebModel.getUserUpdatedBy() != null) {
	                user.setUserUpdatedBy(userWebModel.getUserUpdatedBy());
	            }
	            
	            // Save the updated user details
	            User savedUser = userRepository.save(user);

	            // Handle category updates:
	            // For each provided category, if an active StudentCategoryDetails record already exists,
	            // deactivate it (set active flag to false) and then add a new record.
	            if (userWebModel.getCategoryNames() != null && !userWebModel.getCategoryNames().isEmpty()) {
	                List<HashMap<String, Object>> categories = userWebModel.getCategoryNames();
	                for (HashMap<String, Object> categoryMap : categories) {
	                    // Process only if "categoryId" is provided
	                    if (categoryMap.containsKey("categoryId")) {
	                        Integer categoryId = Integer.valueOf(categoryMap.get("categoryId").toString());
	                        Category category = categoryRepository.findById(categoryId).orElse(null);
	                        
	                        if (category != null) {
	                            // Check if an active link already exists for this user and category
	                            StudentCategoryDetails existingLink = studentCategoryDetailsRepository
	                                .findByUserAndCategoryAndStudentCategoryIsActive(savedUser, category, true);
	                            
	                            if (existingLink != null) {
	                                // Deactivate the existing link
	                                existingLink.setStudentCategoryIsActive(false);
	                                studentCategoryDetailsRepository.save(existingLink);
	                            }
	                            
	                            // Add the new link
	                            StudentCategoryDetails newLink = StudentCategoryDetails.builder()
	                                .studentCategoryIsActive(true)
	                                .studentCategoryCreatedBy(savedUser.getUserId())
	                                .category(category)
	                                .user(savedUser)
	                                .build();
	                            studentCategoryDetailsRepository.save(newLink);
	                        }
	                    }
	                    // If no categoryId is provided, do nothing.
	                }
	            }

	            logger.info("User details updated successfully for userId: {}", userWebModel.getUserId());
	            return ResponseEntity.ok(new Response(1, "Success", "User details updated successfully"));
	        } else {
	            logger.warn("User not found with userId: {}", userWebModel.getUserId());
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                               .body(new Response(0, "Fail", "User not found"));
	        }
	    } catch (Exception e) {
	        logger.error("Error updating user details: " + e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                           .body(new Response(0, "Fail", "Error updating user details"));
	    }
	}


	@Override
	public ResponseEntity<Response> getUserDetailsByUserType(String userType) {
	    logger.info("Fetching user details for userType: {}", userType);

	    List<User> usersList = userRepository.findByUserType(userType);

	    if (!usersList.isEmpty()) {
	        HashMap<String, Object> responseMap = new HashMap<>();
	        responseMap.put("users", usersList);

	        logger.info("Users retrieved successfully for userType: {}", userType);
	        return ResponseEntity.ok(new Response(1, "Users retrieved successfully", responseMap));
	    } else {
	        logger.warn("No users found for userType: {}", userType);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(new Response(0, "Fail", "No users found for the given userType"));
	    }
	}

	 @Override
	    public FileOutputWebModel saveProfilePhoto(UserWebModel userWebModel) {
//	        Optional<User> user;
//	        try {
//	            user = userRepository.getUserByUserId(userWebModel.getUserId());
//	            if (user.isPresent()) {
//	                // Find and delete old profile pic
//	                FileOutputWebModel fileOutputWebModel = this.getProfilePic(userWebModel);
//	                if (fileOutputWebModel != null) {
//	                    logger.info("Existing profile pic data [{}]", fileOutputWebModel);
//	                    List<Integer> profilePicIdsList = Collections.singletonList(fileOutputWebModel.getCategoryRefId());
//	                    mediaFilesService.deleteMediaFilesByCategoryAndRefIds(MediaFileCategory.ProfilePic, profilePicIdsList);
//	                }
//
//	                // Save/Update profile pic
//	                userWebModel.getProfilePhoto().setCategory(MediaFileCategory.ProfilePic);
//	                userWebModel.getProfilePhoto().setCategoryRefId(user.get().getUserId());
//	                List<FileOutputWebModel> savedFileList = mediaFilesService.saveMediaFiles(userWebModel.getProfilePhoto(), user.get());
//	                return (!Utility.isNullOrEmptyList(savedFileList)) ? savedFileList.get(0) : null;
//	            }
//	        } catch (Exception e) {
//	            logger.error("Error occurred at saveProfilePhoto() -> [{}]", e.getMessage());
//	            e.printStackTrace();
//	            return null;
//	        }
	        return null;
	    }

	 @Override
	 public ResponseEntity<?> getAllAvatarImage() {
	     logger.info("Fetching avatar images for getAllAvatarImage");

	     List<AvatarImage> avatarImages = avatarImageRepository.findAll();

	     if (!avatarImages.isEmpty()) {
	         // Base URL to be concatenated
	         String baseUrl = "https://schoolyogabackend.s3.ap-south-1.amazonaws.com/";

	         // Extract only id and concatenated path
	         List<Map<String, Object>> imageList = avatarImages.stream().map(image -> {
	             Map<String, Object> imageData = new HashMap<>();
	             imageData.put("id", image.getId());
	             imageData.put("path", baseUrl + image.getPath()); // Concatenate base URL with stored path
	             return imageData;
	         }).collect(Collectors.toList()); // Use collect instead of toList()

	         // Prepare response
	         Map<String, Object> responseMap = new HashMap<>();
	         responseMap.put("images", imageList);

	         logger.info("Avatar images retrieved successfully");
	         return ResponseEntity.ok(new Response(1, "Avatar images retrieved successfully", responseMap));
	     } else {
	         logger.warn("No avatar images found");
	         return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                 .body(new Response(0, "Fail", "No avatar images found"));
	     }
	 }

	 @Override
	 public ResponseEntity<?> getSmileImage() {
	     logger.info("Fetching avatar images for getAllAvatarImage");

	     List<SmileImage> avatarImages = smileImageRepository.findAll();

	     if (!avatarImages.isEmpty()) {
	         // Base URL to be concatenated
	         String baseUrl = "https://schoolyogabackend.s3.ap-south-1.amazonaws.com/";

	         // Extract only id and concatenated path
	         List<Map<String, Object>> imageList = avatarImages.stream().map(image -> {
	             Map<String, Object> imageData = new HashMap<>();
	             imageData.put("id", image.getId());
	             imageData.put("path", baseUrl + image.getPath()); // Concatenate base URL with stored path
	             return imageData;
	         }).collect(Collectors.toList()); // Use collect instead of toList()

	         // Prepare response
	         Map<String, Object> responseMap = new HashMap<>();
	         responseMap.put("images", imageList);

	         logger.info("SmileImage images retrieved successfully");
	         return ResponseEntity.ok(new Response(1, "SmileImage retrieved successfully", responseMap));
	     } else {
	         logger.warn("No avatar images found");
	         return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                 .body(new Response(0, "Fail", "No avatar images found"));
	     }
	 }

		@Override
		public Response verifyExpiration(RefreshToken refreshToken) {
			// TODO Auto-generated method stub
		    return new Response(-1, "Fail", "RefreshToken expired");
		}


	    @Override
	    public Optional<User> getUser(Integer userId) {
	        User user = null;
	        Optional<?> dbUser = userRepository.getUserByUserId(userId);
	        if (dbUser.isPresent())
	            user = (User) dbUser.get();
	        return Optional.ofNullable(user);
	    }



}
