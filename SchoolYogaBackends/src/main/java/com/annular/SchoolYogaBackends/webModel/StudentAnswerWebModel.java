package com.annular.SchoolYogaBackends.webModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAnswerWebModel {
	
	private String newAnswer;
    private Integer updatedBy;
    private Integer createdBy;
    private Integer questionDetailsId;

}
