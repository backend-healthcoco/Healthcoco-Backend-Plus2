package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.EmailTrack;
import com.dpdocter.collections.EmailTrackCollection;

public interface EmailTackService {

    void saveEmailTrack(EmailTrackCollection emailTrack);

    List<EmailTrack> getEmailDetails(String patientId, String doctorId, String locationId, String hospitalId, long page, int size);

}
