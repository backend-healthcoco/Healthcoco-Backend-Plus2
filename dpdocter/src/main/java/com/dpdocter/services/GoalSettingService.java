package com.dpdocter.services;

import com.dpdocter.beans.ConsultationCall;
import com.dpdocter.beans.GoalSetting;

public interface GoalSettingService {

	GoalSetting addEditGoalSetting(GoalSetting goalSetting);

	GoalSetting getGoalSetting(String patientId);

	ConsultationCall addEditConsultationCall(ConsultationCall consultationCall);

	ConsultationCall getConsultationCall(String id);

}
