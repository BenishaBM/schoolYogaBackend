package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.SmileImage;

@Repository
public interface SmileImageRepository extends JpaRepository<SmileImage, Integer> {

}
