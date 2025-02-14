package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.annular.SchoolYogaBackends.model.MediaFileCategory;
import com.annular.SchoolYogaBackends.model.MediaFiles;
import com.annular.SchoolYogaBackends.model.User;
import com.annular.SchoolYogaBackends.repository.MediaFilesRepository;
import com.annular.SchoolYogaBackends.service.MediaFileService;
import com.annular.SchoolYogaBackends.util.FileUtil;
import com.annular.SchoolYogaBackends.util.S3Util;
import com.annular.SchoolYogaBackends.util.Utility;
import com.annular.SchoolYogaBackends.webModel.FileInputWebModel;
import com.annular.SchoolYogaBackends.webModel.FileOutputWebModel;

@Service
public class MediaFilesServiceImpl implements MediaFileService {

	public static final Logger logger = LoggerFactory.getLogger(MediaFilesServiceImpl.class);

	@Autowired
	MediaFilesRepository mediaFilesRepository;
	
	@Autowired
	FileUtil fileUtil;

	@Autowired
	S3Util s3Util;
	 @Override
	    public List<FileOutputWebModel> saveMediaFiles(FileInputWebModel fileInputWebModel, User user) {
	        List<FileOutputWebModel> fileOutputWebModelList = new ArrayList<>();
	        try {
	            // 1. Save first in MySQL
	            Map<MediaFiles, MultipartFile> mediaFilesMap = this.prepareMultipleMediaFilesData(fileInputWebModel, user);
	            logger.info("Saved MediaFiles rows list size :- [{}]", mediaFilesMap.size());

	            // 2. Upload into S3
	            mediaFilesMap.forEach((mediaFile, inputFile) -> {
	                mediaFilesRepository.saveAndFlush(mediaFile);
	                try {
	                    File file = File.createTempFile(mediaFile.getFileId(), null);
	                    FileUtil.convertMultiPartFileToFile(inputFile, file);
	                    String response = fileUtil.uploadFile(file, mediaFile.getFilePath() + mediaFile.getFileType());
	                    if (response != null && response.equalsIgnoreCase("File Uploaded")) {
	                        file.delete(); // deleting temp file
	                        fileOutputWebModelList.add(this.transformData(mediaFile)); // Reading the saved file details
	                    }
	                } catch (IOException e) {
	                    logger.error("Error at saveMediaFiles()...", e);
	                }
	            });
	            fileOutputWebModelList.sort(Comparator.comparing(FileOutputWebModel::getId));
	        } catch (Exception e) {
	            logger.error("Error at saveMediaFiles()...", e);
	            e.printStackTrace();
	        }
	        return fileOutputWebModelList;
	    }


	    private Map<MediaFiles, MultipartFile> prepareMultipleMediaFilesData(FileInputWebModel fileInput, User user) {
	        Map<MediaFiles, MultipartFile> mediaFilesMap = new HashMap<>();
	        try {
	            if (!Utility.isNullOrEmptyList(fileInput.getFiles())) {
	                fileInput.getFiles().stream()
	                        .filter(Objects::nonNull)
	                        .forEach(file -> {
	                            MediaFiles mediaFiles = new MediaFiles();
	                            mediaFiles.setUser(user);
	                            mediaFiles.setCategory(fileInput.getCategory());
	                            mediaFiles.setCategoryRefId(fileInput.getCategoryRefId());
	                            mediaFiles.setDescription(fileInput.getDescription());
	                            mediaFiles.setFileId(UUID.randomUUID().toString());
	                            mediaFiles.setFileName(file.getOriginalFilename());
	                            mediaFiles.setFilePath(FileUtil.generateFilePath(mediaFiles.getUser(), fileInput.getCategory().toString(), mediaFiles.getFileId()));
	                            mediaFiles.setFileType(!Utility.isNullOrBlankWithTrim(file.getOriginalFilename()) ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")) : "");
	                            mediaFiles.setFileSize(file.getSize());
	                            mediaFiles.setStatus(true);
	                            mediaFiles.setCreatedBy(user.getUserId());
	                            mediaFiles.setCreatedOn(new Date());
	                            mediaFilesRepository.save(mediaFiles);

	                            mediaFilesMap.put(mediaFiles, file);
	                        });
	            }
	        } catch (Exception e) {
	            logger.error("Error occurred at prepareMultipleMediaFilesData() -> {}", e.getMessage());
	            e.printStackTrace();
	        }
	        return mediaFilesMap;
	    }
	    
	    private FileOutputWebModel transformData(MediaFiles mediaFile) {
	        FileOutputWebModel fileOutputWebModel = null;
	        try {
	            fileOutputWebModel = new FileOutputWebModel();

	            fileOutputWebModel.setId(mediaFile.getId());

	            fileOutputWebModel.setUserId(mediaFile.getUser().getUserId());
	            fileOutputWebModel.setCategory(mediaFile.getCategory().toString());
	            fileOutputWebModel.setCategoryRefId(mediaFile.getCategoryRefId());

	            fileOutputWebModel.setFileId(mediaFile.getFileId());
	            fileOutputWebModel.setFileName(mediaFile.getFileName());
	            fileOutputWebModel.setFileType(mediaFile.getFileType());
	            fileOutputWebModel.setFileSize(mediaFile.getFileSize());
	            fileOutputWebModel.setFilePath(s3Util.generateS3FilePath(mediaFile.getFilePath() + mediaFile.getFileType()));
	            fileOutputWebModel.setDescription(mediaFile.getDescription());

	            fileOutputWebModel.setCreatedBy(mediaFile.getCreatedBy());
	            fileOutputWebModel.setCreatedOn(mediaFile.getCreatedOn());
	            fileOutputWebModel.setUpdatedBy(mediaFile.getUpdatedBy());
	            fileOutputWebModel.setUpdatedOn(mediaFile.getUpdatedOn());
	            
	         
	            // Convert Date to LocalDateTime
	            Date createdDate = mediaFile.getCreatedOn();
	            LocalDateTime createdOn = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());


	            return fileOutputWebModel;
	        } catch (Exception e) {
	            logger.error("Error at transformData() -> {}", e.getMessage());
	            e.printStackTrace();
	        }
	        return fileOutputWebModel;
	    }


	    @Override
	    public List<FileOutputWebModel> getMediaFilesByCategoryAndRefId(MediaFileCategory category, Integer refId) {
	        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
	        try {
	            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByCategoryAndRefId(category, refId);
	            if (!Utility.isNullOrEmptyList(mediaFiles)) {
	                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
	            }
	        } catch (Exception e) {
	            logger.error("Error at getMediaFilesByCategoryAndRefId() -> {}", e.getMessage());
	            e.printStackTrace();
	        }
	        return outputWebModelList;
	    }


	    @Override
	    public void deleteMediaFilesByCategoryAndRefIds(MediaFileCategory category, List<Integer> idList) {
	        List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByCategoryAndRefIds(category, idList);
	        this.deleteMediaFiles(mediaFiles);
	    }
	    
	    private void deleteMediaFiles(List<MediaFiles> mediaFiles) {
	        try {
	            if (!Utility.isNullOrEmptyList(mediaFiles)) {
	                mediaFiles.forEach(mediaFile -> {
	                    mediaFile.setStatus(false); // 1. Deactivating the MediaFiles
	                    mediaFilesRepository.saveAndFlush(mediaFile);
	                    fileUtil.deleteFile(mediaFile.getFilePath() + mediaFile.getFileType()); // 2. Deleting the S3 Objects
	                });
	            }
	        } catch (Exception e) {
	            logger.error("Error at deleteMediaFiles() -> [{}]", e.getMessage());
	            e.printStackTrace();
	        }
	    }

}