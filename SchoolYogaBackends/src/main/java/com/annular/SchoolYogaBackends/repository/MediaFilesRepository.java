package com.annular.SchoolYogaBackends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.SchoolYogaBackends.model.MediaFileCategory;
import com.annular.SchoolYogaBackends.model.MediaFiles;
import com.annular.SchoolYogaBackends.webModel.FileInputWebModel;

@Repository
public interface MediaFilesRepository extends JpaRepository<MediaFiles, Integer> {

	@Query("Select m from MediaFiles m where m.category=:category and m.categoryRefId=:refId and m.status=true")
	List<MediaFiles> getMediaFilesByCategoryAndRefId(MediaFileCategory category, Integer refId);

	@Query("Select m from MediaFiles m where m.category=:category and m.categoryRefId IN (:idList) and m.status=true")
	List<MediaFiles> getMediaFilesByCategoryAndRefIds(MediaFileCategory category, List<Integer> idList);



}
