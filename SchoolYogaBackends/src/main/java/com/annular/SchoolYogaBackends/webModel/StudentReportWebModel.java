package com.annular.SchoolYogaBackends.webModel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentReportWebModel {

	 private Integer yogaId;
	    private Integer userId;
	    private Integer createdBy;
	    private Integer classDetailsId;
	    private Boolean completedStatus;
	    private List<StudentAnswerWebModel> studentReports;


}
