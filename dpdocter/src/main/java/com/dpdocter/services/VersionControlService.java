package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.VersionControl;

public interface VersionControlService {

	public Integer checkVersion(VersionControl versionControl);

	public VersionControl changeVersion(VersionControl versionControl);

	List<VersionControl> getVersionsList(long page, int size, String searchTerm);

}
