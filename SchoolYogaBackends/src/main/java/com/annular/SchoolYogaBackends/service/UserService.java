package com.annular.SchoolYogaBackends.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.model.RefreshToken;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;
import com.annular.SchoolYogaBackends.webModel.UserWebModel;

public interface UserService {

	ResponseEntity<?> register(UserWebModel userWebModel);

	RefreshToken createRefreshToken(User user);

	ResponseEntity<?> getUserDetailsById(int userId);

	ResponseEntity<?> deleteUserDetails(Integer userId);

	ResponseEntity<?> updateUserDetails(UserWebModel userWebModel);

	ResponseEntity<?> getUserDetailsByUserType(String userType);

	FileOutputWebModel saveProfilePhoto(UserWebModel userWebModel);

	ResponseEntity<?> getAllAvatarImage();

	ResponseEntity<?> getSmileImage();

	Response verifyExpiration(RefreshToken refreshToken);
	
	Optional<User> getUser(Integer userId);

}
