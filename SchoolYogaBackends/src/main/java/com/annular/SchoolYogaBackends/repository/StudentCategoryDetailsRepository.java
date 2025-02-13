package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.StudentCategoryDetails;

@Repository
public interface StudentCategoryDetailsRepository extends JpaRepository<StudentCategoryDetails, Integer>{

}
