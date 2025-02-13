package com.annular.SchoolYogaBackends.webModel;



import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class YogaWebModel {
	

	private Integer id;
	private String yogaId;
	private String description;
	private Boolean status;
	private Integer createdBy;
	private Date createdOn;
	private Integer updatedBy;
	private Date updatedOn;
	List<MultipartFile> files;
	private Integer userId;

}
