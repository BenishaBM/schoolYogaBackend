package com.annular.SchoolYogaBackends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmailId(String email);

	@Query("SELECT u FROM User u WHERE u.emailId = :emailId AND u.userIsActive = true")
	Optional<User> findByEmailIds(String emailId);

	@Query("SELECT u FROM User u WHERE u.userType = :userType AND u.userIsActive = true")
	List<User> findByUserType(String userType);

	@Query("select u from User u where u.userId=:userId and u.userIsActive=true")
	Optional<User> getUserByUserId(Integer userId);

}
