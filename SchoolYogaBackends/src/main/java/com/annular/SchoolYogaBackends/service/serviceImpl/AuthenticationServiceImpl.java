package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.model.RefreshToken;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.repository.RefreshTokenRepository;
import com.annular.SchoolYogaBackends.repository.UserRepository;
import com.annular.SchoolYogaBackends.service.AuthenticationService;
import com.annular.SchoolYogaBackends.webModel.UserWebModel;




@Service
public class AuthenticationServiceImpl implements AuthenticationService{
	
	public static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	RefreshTokenRepository refreshTokenRepository;
	
	@Override
	public RefreshToken createRefreshToken(UserWebModel userWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		RefreshToken refreashToken = new RefreshToken();
		try {
			logger.info("createRefreshToken method start");
			Optional<User> data = userRepository.findByEmail(userWebModel.getEmailId());
			if (data.isPresent()) {
				Optional<RefreshToken> refreshTokenData = refreshTokenRepository.findByUserId(data.get().getUserId());
				if (refreshTokenData.isPresent()) {
					refreshTokenRepository.delete(refreshTokenData.get());
				}
				refreashToken.setUserId(data.get().getUserId());
				refreashToken.setToken(UUID.randomUUID().toString());
				refreashToken.setExpiryToken(LocalTime.now().plusMinutes(45));
				refreashToken = refreshTokenRepository.save(refreashToken);
				response.put("refreashToken", refreashToken);
				logger.info("createRefreshToken method end");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return refreashToken;
	}



}
