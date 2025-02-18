package com.annular.SchoolYogaBackends.webModel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentReportWebModel {

	private Integer studentTaskReportId;
	private Integer yogaId;
	private Integer userId;
	private Boolean studentTaskReportIsActive;
	private Integer createdBy;
	private Date studentTaskReportCreatedOn;
	private Integer studentTaskReportUpdatedBy;
	private Date studentTaskReportUpdatedOn;
	private Integer classDetailsId;
	private Boolean completedStatus;
	private String ans;
	private Integer questionDetailsId;

}
