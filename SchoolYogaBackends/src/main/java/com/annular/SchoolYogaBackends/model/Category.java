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
@Table(name = "category")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_Id")
	private Integer categoryId;
	
	@Column(name = "category_name")
	private String categoryName;
	
	@Column(name = "categoryIsActive")
	private Boolean categoryIsActive;

	@Column(name = "categorycreated_by")
	private Integer categorycreatedBy;

	@CreationTimestamp
	@Column(name = "category_created_on")
	private Date categoryCreatedOn;

	@Column(name = "category_updated_by")
	private Integer categoryUpdatedBy;

	@Column(name = "user_updated_on")
	@CreationTimestamp
	private Date categoryUpdatedOn;

}
