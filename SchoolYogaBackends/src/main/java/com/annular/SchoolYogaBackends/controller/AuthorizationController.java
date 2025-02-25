package com.annular.SchoolYogaBackends.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.model.AvatarImage;
import com.annular.SchoolYogaBackends.model.RefreshToken;
import com.annular.SchoolYogaBackends.model.SmileImage;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.repository.AvartarImageRepository;
import com.annular.SchoolYogaBackends.repository.SmileImageRepository;
import com.annular.SchoolYogaBackends.repository.UserRepository;
import com.annular.SchoolYogaBackends.security.jwt.JwtGenerator;
import com.annular.SchoolYogaBackends.security.jwt.JwtResponse;
import com.annular.SchoolYogaBackends.service.AuthenticationService;
import com.annular.SchoolYogaBackends.webModel.UserWebModel;





@RestController
@RequestMapping("/auth")
public class AuthorizationController {
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	AuthenticationService userService;
	
	@Autowired
	AvartarImageRepository avatarImageRepository;
	
	@Autowired
	SmileImageRepository smileImageRepository;

	
	@Autowired
	JwtGenerator jwtGenerator;
	
	@PostMapping("/ssoCheck")
	public ResponseEntity<?> ssoCheck(@RequestBody UserWebModel userWebModel) {
	    Optional<User> checkUser = userRepository.findByEmail(userWebModel.getEmailId());
	    
	    if (checkUser.isPresent()) {
	        try {
	            User user = checkUser.get();

	            // Fetch profile picture URL if available
	            String profilePicUrl = Optional.ofNullable(user.getProfilePic())
	                .flatMap(avatarImageRepository::findById)
	                .map(AvatarImage::getPath)
	                .map(path -> "https://schoolyogabackend.s3.ap-south-1.amazonaws.com/" + path)
	                .orElse(null);

	            // Fetch smile picture URL if available
	            String smilePicUrl = Optional.ofNullable(user.getSmilePic())
	                .flatMap(smileImageRepository::findById)
	                .map(SmileImage::getPath)
	                .map(path -> "https://schoolyogabackend.s3.ap-south-1.amazonaws.com/" + path)
	                .orElse(null);

	            // Generate JWT token
	            String jwt = jwtGenerator.generateJwt(userWebModel.getAccessToken());

	            // Create refresh token
	            RefreshToken refreshToken = userService.createRefreshToken(userWebModel);

	            // Return response with JWT and refresh token
	            return ResponseEntity.ok(new JwtResponse(
	                jwt, 
	                user.getUserId(), 
	                1, // Assuming this represents a status or role value
	                refreshToken.getToken(), 
	                user.getUserType(), 
	                user.getStd(), 
	                user.getUserName(), 
	                profilePicUrl, 
	                smilePicUrl
	            ));

	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	        }
	    }
	    
	    return ResponseEntity.ok().body(new Response(-1, "User not found. Please register", ""));
	}



}
