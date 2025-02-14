package com.annular.SchoolYogaBackends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.DayDetails;


@Repository
public interface DayDetailsRepository extends JpaRepository<DayDetails,Integer>{

	@Query("SELECT d FROM DayDetails d WHERE d.days = :days AND d.dayDetailsIsActive = true")
	Optional<DayDetails> findByDays(@Param("days") String days);

	@Query("SELECT d FROM DayDetails d WHERE d.dayDetailsIsActive = true")
	List<DayDetails> findByDayDetailsIsActiveTrue();


}
