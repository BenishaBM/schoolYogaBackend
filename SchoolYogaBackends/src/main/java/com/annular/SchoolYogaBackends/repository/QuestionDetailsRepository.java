package com.annular.SchoolYogaBackends.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.QuestionDetails;

@Repository
public interface QuestionDetailsRepository extends JpaRepository<QuestionDetails, Integer>{

	List<QuestionDetails> findByYogaId(Integer id);

	@Transactional
	@Modifying
	@Query("UPDATE QuestionDetails q SET q.questionDetailsIsActive = false WHERE q.yogaId = :yogaId")
	void softDeleteByYogaId(@Param("yogaId")Integer yogaId);


}
