package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.StudentTaskReports;

@Repository
public interface StudentTaskReportRepository extends JpaRepository<StudentTaskReports, Integer>{

}
