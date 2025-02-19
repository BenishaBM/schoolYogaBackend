package com.annular.SchoolYogaBackends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.Category;
import com.annular.SchoolYogaBackends.model.StudentCategoryDetails;
import com.annular.SchoolYogaBackends.model.User;

@Repository
public interface StudentCategoryDetailsRepository extends JpaRepository<StudentCategoryDetails, Integer>{

	StudentCategoryDetails findByUserAndCategoryAndStudentCategoryIsActive(User savedUser, Category category,
			boolean b);

	@Query("SELECT s FROM StudentCategoryDetails s WHERE s.user = :user AND studentCategoryIsActive = true")
	List<StudentCategoryDetails> findByUser(User user);

}
