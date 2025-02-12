package com.annular.SchoolYogaBackends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

	@Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.categoryName) = LOWER(:categoryName) AND categoryIsActive = true")
	boolean existsByCategoryName(String categoryName);

	List<Category> findByCategoryIsActiveTrue();

	@Query("SELECT c FROM Category c WHERE c.categoryName = :categoryName AND categoryIsActive = true")
	Optional<Category> findByCategoryName(String categoryName);
	
}
