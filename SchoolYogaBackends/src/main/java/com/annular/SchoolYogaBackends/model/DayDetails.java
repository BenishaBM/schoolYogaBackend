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
@Table(name = "dayDetails")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DayDetails {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dayDetailsId")
	private Integer dayDetailsId;
	
	@Column(name = "days")
	private String days;
	
	@Column(name = "dayDetailsIsActive")
	private Boolean dayDetailsIsActive;

	@Column(name = "created_by")
	private Integer createdBy;

	@CreationTimestamp
	@Column(name = "dayDetails_created_on")
	private Date dayDetailsCreatedOn;

	@Column(name = "dayDetails_updated_by")
	private Integer dayDetailsUpdatedBy;

	@Column(name = "dayDetails_updated_on")
	@CreationTimestamp
	private Date dayDetailsUpdatedOn;

}
