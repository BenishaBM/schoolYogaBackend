package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.Optional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.UserStatusConfig;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.repository.UserRepository;
import com.annular.SchoolYogaBackends.security.UserDetailsImpl;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	public static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	private static final String CARET = "^";

	@Autowired
	UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("I am from loadUserByUsername() !!! ");
		logger.info("UserName :- " + username);
		logger.info("UserType from LoginConstants :- " );
//		System.out.println("login check --- > admin : "+loginConstants.isAdmin()+"  Driver :"+loginConstants.isDriver());
		String userName = "";
		if (username != null && username.contains(CARET)) {
			userName = username.substring(0, username.indexOf(CARET));
//			userType = username.substring(username.indexOf(CARET) + 1);
			logger.info("userName : " + userName + "  userType: " );
		} 
		 else {
				userName = username;
			
			}
		User user = userRepo.findByEmail(userName)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with emailId: " + username));
		logger.info("User from DB --> " + user.getUserId() + user.getEmailId() );
		return UserDetailsImpl.build(user);
	}
}