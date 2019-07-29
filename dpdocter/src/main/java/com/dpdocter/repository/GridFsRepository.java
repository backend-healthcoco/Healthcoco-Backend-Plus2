package com.dpdocter.repository;

import java.io.InputStream;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

@Repository("gridfsRepository")
public class GridFsRepository {
	@Autowired
	GridFsTemplate gridFsTemplate;

	public boolean save(InputStream content, String fileName, String ContentType) {

		ObjectId file = gridFsTemplate.store(content, fileName, ContentType);
		if (file == null)
			return false;
		return true;

	}

	public ObjectId save(InputStream content, String fileName) {

		return gridFsTemplate.store(content, fileName);

	}

	public com.mongodb.client.gridfs.model.GridFSFile read(ObjectId id) {

		return gridFsTemplate.findOne(new Query(new Criteria("_id").is(id)));

	}

	public void delete(ObjectId id) {

		gridFsTemplate.delete(new Query(new Criteria("_id").is(id)));

	}

}
