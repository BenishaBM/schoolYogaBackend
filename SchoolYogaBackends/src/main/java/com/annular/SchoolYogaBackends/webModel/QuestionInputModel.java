package com.annular.SchoolYogaBackends.webModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionInputModel {

    private String questionDetails;
    private String questionType;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;

}