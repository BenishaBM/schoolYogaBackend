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
import com.annular.SchoolYogaBackends.model.RefreshToken;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.repository.UserRepository;
import com.annular.SchoolYogaBackends.security.jwt.JwtGenerator;
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
	JwtGenerator jwtGenerator;
	
	@PostMapping("/ssoCheck")
	public ResponseEntity<?> ssoCheck(@RequestBody UserWebModel userWebModel) {
		Optional<User> data = userRepository.findByEmail(userWebModel.getEmailId());
		if (data.isPresent()) {
			try {
	            String idToken = userWebModel.getIdToken();
//	            String jwt = googleAuthService.verifyAndGenerateJwt(idToken);
	            RefreshToken refreshToken = userService.createRefreshToken(userWebModel);
	            String jwt = jwtGenerator.generateJwt(userWebModel.getAccessToken());
	            Map<String, Object> responseData = new HashMap<>();
	            responseData.put("jwt", jwt);
	            responseData.put("userId", data.get().getUserId());
	            responseData.put("token", refreshToken);
	            return ResponseEntity.ok(responseData);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	        }
		}
		return ResponseEntity.ok().body(new Response(-1, "User not found.Please register", ""));
	}


}
