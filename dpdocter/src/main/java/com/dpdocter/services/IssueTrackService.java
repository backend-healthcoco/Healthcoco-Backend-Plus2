package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.IssueTrack;

public interface IssueTrackService {

    IssueTrack addEditIssue(IssueTrack request);

    List<IssueTrack> getIssues(long page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean dicarded,
	    List<String> scope);

    Boolean updateIssueStatus(String issueId, String status, String doctorId, String locationId, String hospitalId);

    Boolean updateIssueStatus(String issueId, String status);

    IssueTrack deleteIssue(String issueId, String doctorId, String locationId, String hospitalId, Boolean discarded);

}
