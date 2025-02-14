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
	private String userType;
	private String day;
	private Integer classDetailsId;
	  private List<QuestionInputModel> questions; // Correct type for questions
	private List<FileOutputWebModel> postFiles;
//	private Integer questionDetailsId;
//	private String questionDetails;
//	private String questionType;
//	private String answerA;
//	private String answerB;
//	private String answerC;
//	private String answerD;
//	private Boolean questionDetailsIsActive;
//	private Integer questionDetailscreatedBy;
//	private Date questionDetailsCreatedOn;
//	private Integer questionDetails_updated_by;
	//private Date questionDetailsbyUpdatedOn;
	private List<QuestionWebModel> questionss;



}
