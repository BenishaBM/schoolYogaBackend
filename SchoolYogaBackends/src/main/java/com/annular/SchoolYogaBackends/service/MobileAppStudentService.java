package com.annular.SchoolYogaBackends.service;

import java.util.Map;

import com.annular.SchoolYogaBackends.model.StudentAnsReport;
import com.annular.SchoolYogaBackends.model.StudentTaskReports;
import com.annular.SchoolYogaBackends.webModel.StudentReportWebModel;

public interface MobileAppStudentService {

	Map<String, Object> getAllTaskDataByStdId(Integer stdId);

	Map<String, Object> getAllStdIdAndDay(Integer stdId, String day);

	StudentTaskReports saveStudentAns(StudentReportWebModel studentReportWebModel);

}
