package com.annular.SchoolYogaBackends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.QuestionDetails;

@Repository
public interface QuestionDetailsRepository extends JpaRepository<QuestionDetails, Integer>{

	List<QuestionDetails> findByYogaId(Integer id);

}
