package com.annular.SchoolYogaBackends.service;

import java.util.List;

import com.annular.SchoolYogaBackends.webModel.YogaWebModel;

public interface YogaService {

	YogaWebModel saveYogaWithFiles(YogaWebModel inputFileData);

	List<YogaWebModel> getAllUsersPosts();

	YogaWebModel getPostByYogaId(Integer id);

	boolean deleteYogaPostById(YogaWebModel yogaWebModel);

	YogaWebModel updateYogaWithFiles(YogaWebModel yogaWebModel);

}
