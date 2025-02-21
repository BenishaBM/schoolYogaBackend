package com.annular.SchoolYogaBackends.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "studentTaskReports")
@Builder
@Getter
@Setter
@ToString(exclude = "studentAnsReports") // Prevent infinite recursion
@AllArgsConstructor
@NoArgsConstructor
public class StudentTaskReports {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "studentTaskReportId")
	private Integer studentTaskReportId;
	
	@Column(name = "yogaId")
	private Integer yogaId;
	
	@Column(name = "userId")
	private Integer userId;
	
	@Column(name = "studentTaskReportIsActive")
	private Boolean studentTaskReportIsActive;

	@Column(name = "created_by")
	private Integer createdBy;

	@CreationTimestamp
	@Column(name = "studentTaskReport_created_on")
	private Date studentTaskReportCreatedOn;

	@Column(name = "studentTaskReport_updated_by")
	private Integer studentTaskReportUpdatedBy;

	@Column(name = "studentTaskReport_updated_on")
	@CreationTimestamp
	private Date studentTaskReportUpdatedOn;
	
	@Column(name = "classDetailsId")
	private Integer classDetailsId;
	
	@Column(name = "completedStatus")
	private Boolean completedStatus;
	
	
	// One-to-Many relationship with StudentAnsReport
    @OneToMany(mappedBy = "studentTaskReport")
    private List<StudentAnsReport> studentAnsReports;
    
    @OneToMany(mappedBy = "studentTaskReport")
    private List<StudentMediaReport> studentMediaReports;


	

}
