package com.annular.SchoolYogaBackends.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "studentAnsReport")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class StudentAnsReport {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "studentAnsReportId")
	private Integer studentAnsReportId;
	
	@Column(name = "studentAnsReportIsActive")
	private Boolean studentAnsReportIsActive;

	@Column(name = "created_by")
	private Integer createdBy;

	@CreationTimestamp
	@Column(name = "studentAnsReport_created_on")
	private Date studentAnsReportCreatedOn;

	@Column(name = "studentTaskReport_updated_by")
	private Integer studentTaskReportUpdatedBy;

	@Column(name = "studentTaskReport_updated_on")
	@CreationTimestamp
	private Date studentTaskReportUpdatedOn;
	
	@Column(name = "ans")
	private String ans;
	
	@Column(name = "questionDetailsId")
	private Integer questionDetailsId;
	
	 // Map the relationship to StudentTaskReports entity
    @ManyToOne
    @JoinColumn(name = "studentTaskReportId", referencedColumnName = "studentTaskReportId")
    private StudentTaskReports studentTaskReport;
	


}
