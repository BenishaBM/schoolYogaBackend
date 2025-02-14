package com.annular.SchoolYogaBackends.webModel;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DayDetailsWebModel {

	private Integer dayDetailsId;
	private String days;
	private Boolean dayDetailsIsActive;
	private Integer createdBy;
	private Date dayDetailsCreatedOn;
	private Integer dayDetailsUpdatedBy;
	private Date dayDetailsUpdatedOn;
    private Integer classDetailsId;
	private String classLevel;
	private Boolean classDetailsIsActive;
	private Date classDetailsCreatedOn;
	private Integer classDetailsUpdatedBy;
	private Date classDetailsUpdatedOn;

}
