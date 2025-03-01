package com.annular.SchoolYogaBackends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.model.Yoga;

@Repository
public interface YogaRepository extends JpaRepository<Yoga, Integer> {

	@Query("SELECT p FROM Yoga p WHERE p.status = true")
	List<Yoga> getAllActivePosts();

	@Query("SELECT p FROM Yoga p WHERE p.status = true")
	Optional<Yoga> getByYogaId(Integer id);

	@Query("SELECT p FROM Yoga p WHERE p.id = :id AND p.status = true")
	Optional<Yoga> findByYogaId(Integer id);
	
	@Query("SELECT COUNT(y) > 1 FROM Yoga y WHERE y.day = :day AND y.classDetailsId = :classDetailsId AND y.status = true")
	boolean existsByDayAndClassDetailsId(@Param("day") String day, @Param("classDetailsId") Integer classDetailsId);

	
	@Query("SELECT COUNT(y) > 0 FROM Yoga y WHERE y.day = :day AND y.classDetailsId = :classDetailsId AND y.status = true")
	boolean existsByDayAndClassDetailsIds(@Param("day") String day, @Param("classDetailsId") Integer classDetailsId);

	@Query("SELECT p FROM Yoga p WHERE p.classDetailsId = :stdId AND p.status = true")
	List<Yoga> findByClassDetailsId(Integer stdId);

	@Query("SELECT p FROM Yoga p WHERE p.classDetailsId = :stdId AND p.day = :day AND p.status = true")
	Yoga findYogaByStdIdAndDay(Integer stdId, String day);

	@Query("SELECT p FROM Yoga p WHERE p.classDetailsId = :std AND p.status = true")
	List<Yoga> findByStdId(Integer std);


	


}
