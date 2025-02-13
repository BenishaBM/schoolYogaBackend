package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.model.MediaFileCategory;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.model.Yoga;
import com.annular.SchoolYogaBackends.repository.MediaFilesRepository;
import com.annular.SchoolYogaBackends.repository.YogaRepository;
import com.annular.SchoolYogaBackends.service.MediaFileService;
import com.annular.SchoolYogaBackends.service.UserService;
import com.annular.SchoolYogaBackends.service.YogaService;
import com.annular.SchoolYogaBackends.util.Utility;
import com.annular.SchoolYogaBackends.webModel.FileInputWebModel;
import com.annular.SchoolYogaBackends.webModel.YogaWebModel;

@Service
public class YogaServiceImpl implements YogaService {

	@Autowired
	YogaRepository yogaRepository;

	 public static final Logger logger = LoggerFactory.getLogger(YogaServiceImpl.class);
	
	@Autowired
	MediaFileService mediaFilesService;
	
    @Autowired
    UserService userService;

    @Override
    public YogaWebModel saveYogaWithFiles(YogaWebModel yogaWebModel) {
        try {
            // Retrieve the user from the database
            User userFromDB = userService.getUser(yogaWebModel.getUserId()).orElse(null);
            if (userFromDB == null) {
                logger.error("User not found for userId: {}", yogaWebModel.getUserId());
                return null;
            }
            logger.info("User found: {}", userFromDB.getUserName());

            // Create and save a new Yoga post
            Yoga posts = Yoga.builder()
                    .yogaId(UUID.randomUUID().toString())
                    .description(yogaWebModel.getDescription())
                    .status(true)
                    .createdOn(new Date())
                    .build();
            Yoga savedPost = yogaRepository.saveAndFlush(posts);

            // If files are provided, save them in the media_files table
            if (!Utility.isNullOrEmptyList(yogaWebModel.getFiles())) {
                FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
                        .category(MediaFileCategory.Yoga)
                        .categoryRefId(savedPost.getId())
                        .files(yogaWebModel.getFiles())
                        .build();
                mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);
            }

            // Transform the saved Yoga post into a YogaWebModel and return it
            List<YogaWebModel> responseList = this.transformPostsDataToYogaWebModel(List.of(savedPost));
            return responseList.isEmpty() ? null : responseList.get(0);
        } catch (Exception e) {
            logger.error("Error at saveYogaWithFiles() -> {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


	private List<YogaWebModel> transformPostsDataToYogaWebModel(List<Yoga> yogaList) {
	    if (yogaList == null || yogaList.isEmpty()) {
	        return Collections.emptyList();
	    }
	    return yogaList.stream()
	        .map(yoga -> YogaWebModel.builder()
	                .id(yoga.getId())
	                .yogaId(yoga.getYogaId())
	                .description(yoga.getDescription())
	                .status(yoga.getStatus())
	                .createdBy(yoga.getCreatedBy())
	                .createdOn(yoga.getCreatedOn())
	                .updatedBy(yoga.getUpdatedBy())
	                .updatedOn(yoga.getUpdatedOn())
	                .build())
	        .collect(Collectors.toList());
	}



}
