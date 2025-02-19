package com.annular.SchoolYogaBackends.webModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentMediaReportWebModel {
	
    private Integer mediaFileId;
    private Float viewedMediaFilesId;
    private Boolean seen;
    private Boolean studentMediaIsActive;
    private Integer createdBy;
    private Integer studentMediaUpdatedBy;

}
