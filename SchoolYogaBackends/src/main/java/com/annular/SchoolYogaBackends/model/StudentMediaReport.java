package com.annular.SchoolYogaBackends.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "studentMediaReport")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudentMediaReport {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "studentMediaReportId")
	private Integer studentMediaReportId;
	
	@Column(name = "mediaFileId")
	private Integer mediaFileId;
	
	@Column(name = "viewedMediaFiles")
	private Float viewedMediaFilesId;
	
	@Column(name = "seen")
	private Boolean seen;
	
	@Column(name = "studentMediaIsActive")
	private Boolean studentMediaIsActive;

	@Column(name = "created_by")
	private Integer createdBy;

	@CreationTimestamp
	@Column(name = "studentMediacreated_on")
	private Date studentMediaCreatedOn;

	@Column(name = "studentMediaupdated_by")
	private Integer studentMediaUpdatedBy;

	@Column(name = "studentTaskReport_updated_on")
	@CreationTimestamp
	private Date studentTaskReportUpdatedOn;

}
