package com.annular.SchoolYogaBackends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.StudentTaskReports;

@Repository
public interface StudentTaskReportRepository extends JpaRepository<StudentTaskReports, Integer>{
	
	@Query("SELECT s FROM StudentTaskReports s WHERE s.yogaId = :yogaId AND s.userId = :userId")
    List<StudentTaskReports> findByYogaIdAndUserId(@Param("yogaId") Integer yogaId, @Param("userId") Integer userId);

	@Query("SELECT s FROM StudentTaskReports s WHERE s.yogaId = :id AND s.studentTaskReportIsActive = true")
	List<StudentTaskReports> findByYogaId(Integer id);

	@Query("SELECT s FROM StudentTaskReports s WHERE s.yogaId = :id AND s.userId = :userId")
	List<StudentTaskReports> findByYogaIdAndUserIds(Integer id, Integer userId);

	// Add this to your StudentTaskReportsRepository interface
	@Query("SELECT DISTINCT str FROM StudentTaskReports str " +
	       "LEFT JOIN FETCH str.studentAnsReports " +
	       "WHERE str.yogaId = :yogaId AND str.userId = :userId")
	List<StudentTaskReports> findByYogaIdAndUserIdWithAnswers(
	    @Param("yogaId") Integer yogaId, 
	    @Param("userId") Integer userId
	);

}
