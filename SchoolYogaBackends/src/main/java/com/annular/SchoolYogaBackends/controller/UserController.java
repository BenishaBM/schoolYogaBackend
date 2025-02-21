package com.annular.SchoolYogaBackends.controller;

import java.time.LocalTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.UserStatusConfig;
import com.annular.SchoolYogaBackends.model.AvatarImage;
import com.annular.SchoolYogaBackends.model.RefreshToken;
import com.annular.SchoolYogaBackends.model.SmileImage;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.repository.AvartarImageRepository;
import com.annular.SchoolYogaBackends.repository.RefreshTokenRepository;
import com.annular.SchoolYogaBackends.repository.SmileImageRepository;
import com.annular.SchoolYogaBackends.repository.UserRepository;
import com.annular.SchoolYogaBackends.security.UserDetailsImpl;
import com.annular.SchoolYogaBackends.security.jwt.JwtResponse;
import com.annular.SchoolYogaBackends.security.jwt.JwtUtils;
import com.annular.SchoolYogaBackends.service.UserService;
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;
import com.annular.SchoolYogaBackends.webModel.UserWebModel;

@RestController
@RequestMapping("/user")
public class UserController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserStatusConfig loginConstants;

	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	AvartarImageRepository avatarImageRepository;
	
	@Autowired
	SmileImageRepository smileImageRepository;

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@PostMapping("register")
	public ResponseEntity<?> userRegister(@RequestBody UserWebModel userWebModel) {
		try {
			logger.info("User register controller start");
			return userService.register(userWebModel);
		} catch (Exception e) {
			logger.error("userRegister Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}

	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody UserWebModel userWebModel) {
		try {
			Optional<User> checkUser = userRepository.findByEmailIds(userWebModel.getEmailId());

			if (checkUser.isPresent()) {
				User user = checkUser.get();
				
				 
				 Optional<AvatarImage> db = avatarImageRepository.findById(user.getProfilePic());
			        Optional<SmileImage> dbs = smileImageRepository.findById(user.getSmilePic());
			        
			        // Fetching profile and smile picture paths
			        String profilePicUrls = user.getProfilePic() != null ? 
			            "https://schoolyogabackend.s3.ap-south-1.amazonaws.com/" + db.get().getPath() : null;

			        String smilePicUrl = user.getSmilePic() != null ? 
			            "https://schoolyogabackend.s3.ap-south-1.amazonaws.com/" + dbs.get().getPath() : null;
				// Authenticate user with email and password
				Authentication authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(userWebModel.getEmailId(), userWebModel.getPassword()));

				SecurityContextHolder.getContext().setAuthentication(authentication);

				// Generate refresh token
				RefreshToken refreshToken = userService.createRefreshToken(user);

				// Generate JWT token
				String jwt = jwtUtils.generateJwtToken(authentication);
				UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

				logger.info("Login successful for user: {}", user.getEmailId());

				// Return response with JWT and refresh token
				return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), 1, // Assuming this is a status or
																						// role value
						refreshToken.getToken(), userDetails.getUserType(), user.getStd(),user.getUserName(),profilePicUrls,smilePicUrl));
			} else {
				return ResponseEntity.badRequest().body(new Response(-1, "Fail", "Invalid email or password"));
			}
		} catch (BadCredentialsException e) {
			logger.error("Login failed: Invalid credentials");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new Response(-1, "Fail", "Invalid email or password"));
		} catch (Exception e) {
			logger.error("Error at login() -> {}", e.getMessage(), e);
			return ResponseEntity.internalServerError()
					.body(new Response(-1, "Fail", "An error occurred during login"));
		}
	}
	
	@GetMapping("getUserDetailsById")
	public ResponseEntity<?> getUserDetailsById(@RequestParam("userId") int userId) {
	    try {
	        logger.info("getUserDetailsById request for userId: {}", userId);
	        return userService.getUserDetailsById(userId);
	    } catch (Exception e) {
	        logger.error("getUserDetailsById Method Exception: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new Response(-1, "Fail", e.getMessage()));
	    }
	}
	
	@DeleteMapping("deleteUserDetails")
	public ResponseEntity<?> deleteUserDetails(@RequestParam("userId") Integer userId) {
	    try {
	        logger.info("Delete user details request for userId: {}", userId);
	        return userService.deleteUserDetails(userId);
	    } catch (Exception e) {
	        logger.error("deleteUserDetails Method Exception: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new Response(-1, "Fail", e.getMessage()));
	    }
	}
	@PostMapping("refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody UserWebModel userWebModel) {
		
        Optional<RefreshToken> data = refreshTokenRepository.findByToken(userWebModel.getToken());
        
        if (data.isPresent()) {
            Response token = userService.verifyExpiration(data.get());
            Optional<User> userData = userRepository.findById(data.get().getUserId());
            String jwt = jwtUtils.generateJwtTokenForRefreshToken(userData.get());
            RefreshToken refreshToken = data.get();
            refreshToken.setExpiryToken(LocalTime.now().plusMinutes(17));
            refreshTokenRepository.save(refreshToken);
            return ResponseEntity.ok(new JwtResponse(jwt,userData.get().getUserId(),
                
                   
                    1,
                    token.getData().toString(),
                    userData.get().getUserType(),1,userData.get().getUserName(),"",""
                   ));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Fail", "Refresh Token Failed"));
    }

	
	@PostMapping("updateUserDetails")
	public ResponseEntity<?> updateUserDetails(@RequestBody UserWebModel userWebModel) {
		try {
			logger.info("User register controller start");
			return userService.updateUserDetails(userWebModel);
		} catch (Exception e) {
			logger.error("updateUserDetails Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "Fail", e.getMessage()));
		}
		
	}
	
	@GetMapping("getUserDetailsByUserType")
	public ResponseEntity<?> getUserDetailsByUserType(@RequestParam("userType") String userType) {
	    try {
	        logger.info("getUserDetailsByUserType request for userType: {}", userType);
	        return userService.getUserDetailsByUserType(userType);
	    } catch (Exception e) {
	        logger.error("getUserDetailsByUserType Method Exception: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new Response(-1, "Fail", e.getMessage()));
	    }
	}
    @PostMapping(path = "/saveProfilePhoto", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response saveProfilePhoto(@ModelAttribute UserWebModel userWebModel) {
        FileOutputWebModel profilePic = userService.saveProfilePhoto(userWebModel);
        if (profilePic != null) {
            return new Response(1, "Profile pic saved Successfully...", profilePic);
        } else {
            return new Response(-1, "User not found...", null);
        }
    }
    
	@GetMapping("getAllAvatarImage")
	public ResponseEntity<?> getAllAvatarImage() {
	    try {
	        logger.info("getAllAvatarImage request for userId: {}");
	        return userService.getAllAvatarImage();
	    } catch (Exception e) {
	        logger.error("getAllAvatarImage Method Exception: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new Response(-1, "Fail", e.getMessage()));
	    }
	}
	
	@GetMapping("getSmileImage")
	public ResponseEntity<?> getSmileImage() {
	    try {
	        logger.info("getSmileImage request for userId: {}");
	        return userService.getSmileImage();
	    } catch (Exception e) {
	        logger.error("getSmileImage Method Exception: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new Response(-1, "Fail", e.getMessage()));
	    }
	}
    
	
}
