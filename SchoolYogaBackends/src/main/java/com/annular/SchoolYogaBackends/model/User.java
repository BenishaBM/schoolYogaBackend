package com.annular.SchoolYogaBackends.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer userId;

	@Column(name = "emailId")
	private String emailId;

	@JsonIgnore
	@Column(name = "password")
	private String password;

	@Column(name = "user_type")
	private String userType; // student//teacher//superAdmin

	@Column(name = "userIsActive")
	private Boolean userIsActive;

	@Column(name = "created_by")
	private Integer createdBy;

	@CreationTimestamp
	@Column(name = "user_created_on")
	private Date userCreatedOn;

	@Column(name = "user_updated_by")
	private Integer userUpdatedBy;

	@Column(name = "user_updated_on")
	@CreationTimestamp
	private Date userUpdatedOn;

	@Column(name = "userName")
	private String userName;

	@Column(name = "gender")
	private String gender;

	@Column(name = "rollNo")
	private String rollNo;

	@Column(name = "schoolName")
	private Integer schoolName;
	
	@Column(name = "std")
	private Integer std;
	
	@Column(name = "profilePic")
	private Integer profilePic;
	
	@Column(name = "smilePic")
	private Integer smilePic;
	
	@Column(name = "frdname")
	private String frdName;
	
	@Column(name = "age")
	private Integer age;
	
	@Column(name = "frdDescription")
	private String frdDescription;
	
	@Column(name = "empId")
	private String empId;
	
    // Corrected One-to-Many relationship mapping
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentCategoryDetails> studentCategoryDetails;

}
