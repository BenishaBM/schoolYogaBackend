package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.Category;
import com.annular.SchoolYogaBackends.model.StudentCategoryDetails;
import com.annular.SchoolYogaBackends.model.User;

@Repository
public interface StudentCategoryDetailsRepository extends JpaRepository<StudentCategoryDetails, Integer>{

	StudentCategoryDetails findByUserAndCategoryAndStudentCategoryIsActive(User savedUser, Category category,
			boolean b);

}
