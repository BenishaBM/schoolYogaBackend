package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.StudentAnsReport;

@Repository
public interface StudentAnsReportRepository extends JpaRepository<StudentAnsReport, Integer>{

}
