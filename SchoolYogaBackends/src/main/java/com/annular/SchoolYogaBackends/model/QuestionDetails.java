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
@Table(name = "questionDetails")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_details_id")
	private Integer questionDetailsId;
	
	@Column(name = "question_details")
	private String questionDetails;
	
	@Column(name = "questionType")
	private String questionType;
	
	@Column(name = "answerA")
	private String answerA;
	
	@Column(name = "answerB")
	private String answerB;
	
	@Column(name = "answerC")
	private String answerC;
	
	@Column(name = "answerD")
	private String answerD;
	
	@Column(name = "question_details_is_active")
	private Boolean questionDetailsIsActive;

	@Column(name = "question_details_created_by")
	private Integer questionDetailscreatedBy;

	@CreationTimestamp
	@Column(name = "question_details_created_on")
	private Date questionDetailsCreatedOn;

	@Column(name = "question_details_updated_by")
	private Integer questionDetails_updated_by;

	@Column(name = "question_details_updated_on")
	@CreationTimestamp
	private Date questionDetailsbyUpdatedOn;
	
	@Column(name = "yogaId")
	private Integer yogaId;

}
