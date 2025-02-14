package com.annular.SchoolYogaBackends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.ClassDetails;

@Repository
public interface ClassDetailsRepository extends JpaRepository<ClassDetails, Integer> {

	@Query("SELECT d FROM ClassDetails d WHERE d.classDetailsIsActive = true")
	List<ClassDetails> findByclassDetailsIsActiveTrue();

}
