package com.annular.SchoolYogaBackends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.MediaFiles;
import com.annular.SchoolYogaBackends.webModel.FileInputWebModel;

@Repository
public interface MediaFilesRepository extends JpaRepository<MediaFiles, Integer> {



}
