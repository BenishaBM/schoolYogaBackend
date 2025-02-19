package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.StudentMediaReport;

@Repository
public interface StudentMediaFileRepository extends JpaRepository<StudentMediaReport, Integer>{

}
