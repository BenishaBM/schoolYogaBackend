package com.annular.SchoolYogaBackends.webModel;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.annular.SchoolYogaBackends.model.MediaFileCategory;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileInputWebModel {

	// For save purpose
	private Integer userId;
	private MediaFileCategory category;
	private Integer categoryRefId;
	private List<MultipartFile> files;
	private String description;

	// For read purpose
	private String fileId;
	private String fileType;
	private String filePath;
	private String type;

}