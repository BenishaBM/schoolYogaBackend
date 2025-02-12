package com.annular.SchoolYogaBackends.service.serviceImpl;

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
import com.annular.SchoolYogaBackends.model.RefreshToken;
import com.annular.SchoolYogaBackends.model.SmileImage;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.repository.AvartarImageRepository;
import com.annular.SchoolYogaBackends.repository.RefreshTokenRepository;
import com.annular.SchoolYogaBackends.repository.SmileImageRepository;
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
	private RefreshTokenRepository refreshTokenRepository;

	@Override
	public ResponseEntity<?> register(UserWebModel userWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("Register method start");

			// Check if user already exists
			Optional<User> existingUser = userRepository.findByEmailId(userWebModel.getEmailId());
			if (existingUser.isPresent()) {
				response.put("message", "User with this email already exists");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// Create new user entity
			User newUser = User.builder().emailId(userWebModel.getEmailId())
					.password(passwordEncoder.encode(userWebModel.getPassword())) // Encrypt password
					.userType(userWebModel.getUserType()).userIsActive(true) // Default active
					.schoolName(userWebModel.getSchoolName()).rollNo(userWebModel.getRollNo())
					.std(userWebModel.getStd()).profilePic(userWebModel.getProfilePic())
					.createdBy(userWebModel.getCreatedBy()).userName(userWebModel.getUserName()).build();

			// Save user
			User savedUser = userRepository.save(newUser);

			return ResponseEntity.ok(new Response(0, "success", "User registered successfully"));


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

	    return userRepository.findById(userId)
	        .map(user -> {
	            HashMap<String, Object> responseMap = new HashMap<>();
	            responseMap.put("userDetails", user);

	            logger.info("User details retrieved successfully for userId: {}", userId);
	            return ResponseEntity.ok(new Response(1, "User details retrieved successfully", responseMap));
	        })
	        .orElseGet(() -> {
	            logger.warn("User not found with userId: {}", userId);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(0, "Fail", "User not found"));
	        });
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

	    Optional<User> optionalUser = userRepository.findById(userWebModel.getUserId());

	    if (optionalUser.isPresent()) {
	        User user = optionalUser.get();

	        // Updating user details
	        user.setUserName(userWebModel.getUserName());
	        user.setSchoolName(userWebModel.getSchoolName());
	        user.setRollNo(userWebModel.getRollNo());
	        user.setEmailId(userWebModel.getEmailId());
	        user.setUserType(userWebModel.getUserType());
	        user.setUserUpdatedBy(userWebModel.getUserUpdatedBy()); // Assuming this field exists


	        // Save the updated user details
	        userRepository.save(user);

	        logger.info("User details updated successfully for userId: {}", userWebModel.getUserId());

	        HashMap<String, Object> responseMap = new HashMap<>();
	        responseMap.put("updatedUser", user);

	        return ResponseEntity.ok(new Response(1, "User details updated successfully", responseMap));
	    } else {
	        logger.warn("User not found with userId: {}", userWebModel.getUserId());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(0, "Fail", "User not found"));
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



}
