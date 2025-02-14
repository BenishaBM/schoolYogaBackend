package com.annular.SchoolYogaBackends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.SchoolDetails;

@Repository
public interface SchoolDetailsRepository extends JpaRepository<SchoolDetails,Integer> {

	@Query("SELECT d FROM SchoolDetails d WHERE d.schoolDetailsIsActive = true")
	List<SchoolDetails> findBySchoolDetailsIsActiveTrue();

}
