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
@Table(name = "schoolDetails")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SchoolDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schoolDetails_Id")
	private Integer schoolDetailsId;
	
	@Column(name = "schoolDetails_name")
	private String schoolDetailsName;
	
	@Column(name = "schoolDetailsIsActive")
	private Boolean schoolDetailsIsActive;

	@Column(name = "schoolDetailscreated_by")
	private Integer schoolDetailscreatedBy;

	@CreationTimestamp
	@Column(name = "schoolDetails_created_on")
	private Date schoolDetailsCreatedOn;

	@Column(name = "schoolDetails_updated_by")
	private Integer schoolDetails_updated_by;

	@Column(name = "schoolDetails_updated_on")
	@CreationTimestamp
	private Date schoolDetails_updated_byUpdatedOn;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "region")
	private Integer regionId;

}
