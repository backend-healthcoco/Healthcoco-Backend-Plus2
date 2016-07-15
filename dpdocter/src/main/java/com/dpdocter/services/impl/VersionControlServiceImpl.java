package com.dpdocter.services.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.VersionControl;
import com.dpdocter.collections.VersionControlCollection;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.VersionControlRepository;
import com.dpdocter.services.VersionControlService;

@Service
public class VersionControlServiceImpl implements VersionControlService{
	
	private static Logger logger = Logger.getLogger(VersionControlServiceImpl.class.getName());
	
	@Autowired
	private VersionControlRepository versionControlRepository;
	
	@Override
	@Transactional
	public Integer checkVersion(VersionControl versionControl)
	{
		Integer versionControlCode = 0; // default value for success - no change
		VersionControlCollection versionControlCollection = versionControlRepository.findByApplicationType(versionControl.getAppType().toString() , versionControl.getDeviceType().toString());
		if(versionControl != null || versionControlCollection != null)
		{
			if(versionControlCollection.getMajorVersion() > versionControl.getMajorVersion())
			{
				versionControlCode = 3; // major version change - forced update
			}
			else if(versionControlCollection.getMajorVersion() == versionControl.getMajorVersion() && versionControlCollection.getMinorVersion() > versionControl.getMinorVersion())
			{
				versionControlCode = 2; // minor version change - forced update
			}
			else if(versionControlCollection.getMajorVersion() == versionControl.getMajorVersion() && versionControlCollection.getMinorVersion() ==  versionControl.getMinorVersion() && versionControlCollection.getPatchVersion() > versionControl.getPatchVersion())
			{
				versionControlCode = 1; // minor version change - optional update
			}
			
		}
		return versionControlCode;
	}
	
	@Override
	@Transactional
	public VersionControl changeVersion(VersionControl versionControl)
	{
		VersionControl response = null;
		VersionControlCollection versionControlCollection = versionControlRepository.findByApplicationType(versionControl.getAppType().toString() , versionControl.getDeviceType().toString());
		if(versionControl != null )
		{
			if(versionControlCollection == null)
			{
				versionControlCollection = new VersionControlCollection();
			}
			else
			{
				versionControl.setId(versionControlCollection.getId().toString());
			}
			BeanUtil.map(versionControl, versionControlCollection);
			try {
				versionControlCollection = versionControlRepository.save(versionControlCollection);
				if(versionControlCollection != null)
				{
					response = new VersionControl();
					BeanUtil.map(versionControlCollection, response);
				}
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		return response;
		
	}

}
