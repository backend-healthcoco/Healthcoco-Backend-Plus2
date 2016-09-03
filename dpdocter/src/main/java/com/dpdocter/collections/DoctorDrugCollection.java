package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "doctor_drug_cl")
public class DoctorDrugCollection extends GenericCollection{

	@Id
    private ObjectId id;

	@Field
    private ObjectId drugId;
	
	@Field
    private ObjectId doctorId;

    @Field
    private ObjectId hospitalId;

    @Field
    private ObjectId locationId;

    @Field
    private long rankingCount = 0;

    @Field
    private Boolean discarded = false;

    @Field
    private List<String> genericCodes;
    
	public DoctorDrugCollection() {
	}

	public DoctorDrugCollection(ObjectId drugId, ObjectId doctorId, ObjectId hospitalId, ObjectId locationId,
			long rankingCount, Boolean discarded, List<String> genericCodes) {
		this.drugId = drugId;
		this.doctorId = doctorId;
		this.hospitalId = hospitalId;
		this.locationId = locationId;
		this.rankingCount = rankingCount;
		this.discarded = discarded;
		this.genericCodes = genericCodes;
	}


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDrugId() {
		return drugId;
	}

	public void setDrugId(ObjectId drugId) {
		this.drugId = drugId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<String> getGenericCodes() {
		return genericCodes;
	}

	public void setGenericCodes(List<String> genericCodes) {
		this.genericCodes = genericCodes;
	}

	@Override
	public String toString() {
		return "DoctorDrugCollection [id=" + id + ", drugId=" + drugId + ", doctorId=" + doctorId + ", hospitalId="
				+ hospitalId + ", locationId=" + locationId + ", rankingCount=" + rankingCount + ", discarded="
				+ discarded + ", genericCodes=" + genericCodes + "]";
	}
}
