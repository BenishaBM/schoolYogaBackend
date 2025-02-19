package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.StudentTaskReports;

@Repository
public interface StudentTaskReportRepository extends JpaRepository<StudentTaskReports, Integer>{

	@Query("SELECT s FROM StudentTaskReports s WHERE s.yogaId = :yogaId AND s.userId = :userId")
	StudentTaskReports findByYogaIdAndUserId(@Param("yogaId") Integer yogaId, @Param("userId") Integer userId);

}
