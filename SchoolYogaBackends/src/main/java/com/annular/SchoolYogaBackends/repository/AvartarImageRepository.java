package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.AvatarImage;

@Repository
public interface AvartarImageRepository extends JpaRepository<AvatarImage, Integer>{

}
