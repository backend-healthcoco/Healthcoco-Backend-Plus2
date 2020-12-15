package com.dpdocter.beans;

public class NDHMRecordDataTiming {

	private NDHMRecordDataRepeat repeat;

	public NDHMRecordDataRepeat getRepeat() {
		return repeat;
	}

	public void setRepeat(NDHMRecordDataRepeat repeat) {
		this.repeat = repeat;
	}

	@Override
	public String toString() {
		return "NDHMRecordDataTiming [repeat=" + repeat + "]";
	}
}
