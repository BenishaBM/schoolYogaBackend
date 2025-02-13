package com.annular.SchoolYogaBackends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.Yoga;

@Repository
public interface YogaRepository extends JpaRepository<Yoga, Integer> {

	@Query("SELECT p FROM Yoga p WHERE p.status = true")
	List<Yoga> getAllActivePosts();

}
