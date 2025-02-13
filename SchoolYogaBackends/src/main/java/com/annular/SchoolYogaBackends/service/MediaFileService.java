package com.annular.SchoolYogaBackends.service;

import java.util.List;

import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.webModel.FileInputWebModel;
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;

public interface MediaFileService {
	
	
	 List<FileOutputWebModel> saveMediaFiles(FileInputWebModel fileInputWebModel, User user);


}
