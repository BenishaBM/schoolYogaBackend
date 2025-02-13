package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.Yoga;

@Repository
public interface YogaRepository extends JpaRepository<Yoga, Integer> {

}
