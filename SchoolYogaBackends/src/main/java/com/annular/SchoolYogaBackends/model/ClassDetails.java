package com.annular.SchoolYogaBackends.model;

import java.util.Date;

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
@Table(name = "classDetails")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ClassDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "classDetailsId")
	private Integer classDetailsId;
	
	@Column(name = "classLevel")
	private String classLevel;
	
	@Column(name = "classDetailsIsActive")
	private Boolean classDetailsIsActive;

	@Column(name = "created_by")
	private Integer createdBy;

	@CreationTimestamp
	@Column(name = "classDetails_created_on")
	private Date classDetailsCreatedOn;

	@Column(name = "classDetails_updated_by")
	private Integer classDetailsUpdatedBy;

	@Column(name = "classDetails_updated_on")
	@CreationTimestamp
	private Date classDetailsUpdatedOn;

}
