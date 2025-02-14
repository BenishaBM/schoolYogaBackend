package com.annular.SchoolYogaBackends.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "studentCategoryDetails")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudentCategoryDetails {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "studentCategoryIsActive")
	private Boolean studentCategoryIsActive;

	@Column(name = "studentCategorycreated_by")
	private Integer studentCategoryCreatedBy;

	@CreationTimestamp
	@Column(name = "studentCategory_created_on")
	private Date studentCategoryCreatedOn;

	@Column(name = "studentCategory_updated_by")
	private Integer studentCategoryUpdatedBy;

	@Column(name = "studentCategory_updated_on")
	@CreationTimestamp
	private Date studentCategoryUpdatedOn;
	
    // Replace the plain categoryId field with a Many-to-One relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")  // This column will hold the foreign key to Category
    private Category category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;


	   
	

}
