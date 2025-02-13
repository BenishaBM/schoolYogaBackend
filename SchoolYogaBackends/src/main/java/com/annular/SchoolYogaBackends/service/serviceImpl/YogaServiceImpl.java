package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;
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
        try {
            if (Utility.isNullOrEmptyList(yogaList)) {
                return Collections.emptyList();
            }
            
            return yogaList.stream()
                    .filter(Objects::nonNull)
                    .map(yoga -> {
                        // Optionally, fetch media files associated with the yoga post.
                        List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Yoga, yoga.getId());
                        
                        // Build and return the YogaWebModel.
                        // If YogaWebModel has a field for files, you can pass postFiles into its builder.
                        return YogaWebModel.builder()
                                .id(yoga.getId())
                                .yogaId(yoga.getYogaId())
                                .description(yoga.getDescription())
                                .status(yoga.getStatus())
                                .createdBy(yoga.getCreatedBy())
                                .createdOn(yoga.getCreatedOn())
                                .updatedBy(yoga.getUpdatedBy())
                                .updatedOn(yoga.getUpdatedOn())
                                .postFiles(postFiles)
//                                //.files(postFiles) // Uncomment if YogaWebModel has a files field
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error in transformPostsDataToYogaWebModel: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }



	@Override
	public List<YogaWebModel> getAllUsersPosts() {
	    try {
	        List<Yoga> postList = yogaRepository.getAllActivePosts();
	        if (postList == null || postList.isEmpty()) {
	            return Collections.emptyList();
	        }
	        // Transform the posts into YogaWebModel and return
	        return this.transformPostsDataToYogaWebModel(postList);
	    } catch (Exception e) {
	        logger.error("Error in getAllUsersPosts(): {}", e.getMessage(), e);
	        return Collections.emptyList(); // or return null; if you prefer
	    }
	}






}
