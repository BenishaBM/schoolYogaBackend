package com.annular.SchoolYogaBackends.webModel;

import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class QuestionWebModel {
    private Integer questionDetailsId;
    private String questionDetails;
    private String questionType;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private Boolean questionDetailsIsActive;
    private Date questionDetailsCreatedOn;
}
