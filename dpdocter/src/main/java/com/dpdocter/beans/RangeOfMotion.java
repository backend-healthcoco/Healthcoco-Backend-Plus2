package com.dpdocter.beans;

public class RangeOfMotion {

	private Shoulder shoulder;
	private Elbow elbow;
	private Wrist wrist;
	private HipJoint hipJoint;
	private KneeJoints kneeJoint;
	private Ankle ankle;

	public Shoulder getShoulder() {
		return shoulder;
	}

	public void setShoulder(Shoulder shoulder) {
		this.shoulder = shoulder;
	}

	public Elbow getElbow() {
		return elbow;
	}

	public void setElbow(Elbow elbow) {
		this.elbow = elbow;
	}

	public Wrist getWrist() {
		return wrist;
	}

	public void setWrist(Wrist wrist) {
		this.wrist = wrist;
	}

	public HipJoint getHipJoint() {
		return hipJoint;
	}

	public void setHipJoint(HipJoint hipJoint) {
		this.hipJoint = hipJoint;
	}

	public KneeJoints getKneeJoint() {
		return kneeJoint;
	}

	public void setKneeJoint(KneeJoints kneeJoint) {
		this.kneeJoint = kneeJoint;
	}

	public Ankle getAnkle() {
		return ankle;
	}

	public void setAnkle(Ankle ankle) {
		this.ankle = ankle;
	}

}
