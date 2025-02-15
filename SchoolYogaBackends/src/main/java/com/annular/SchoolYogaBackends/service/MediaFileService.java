package com.annular.SchoolYogaBackends.service;

import java.util.List;

import com.annular.SchoolYogaBackends.model.MediaFileCategory;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.webModel.FileInputWebModel;
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;

public interface MediaFileService {
	
	
	 List<FileOutputWebModel> saveMediaFiles(FileInputWebModel fileInputWebModel, User user);

	List<FileOutputWebModel> getMediaFilesByCategoryAndRefId(MediaFileCategory yoga, Integer id);

	void deleteMediaFilesByCategoryAndRefIds(MediaFileCategory category, List<Integer> idList);

	void deleteFilesByIds(List<Integer> fileIds);


}
