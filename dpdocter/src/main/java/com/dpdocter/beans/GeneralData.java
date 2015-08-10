package com.dpdocter.beans;

import com.dpdocter.enums.HistoryFilter;

public class GeneralData {
    private Object data;

    private HistoryFilter dataType;

    public Object getData() {
	return data;
    }

    public void setData(Object data) {
	this.data = data;
    }

    public HistoryFilter getDataType() {
	return dataType;
    }

    public void setDataType(HistoryFilter dataType) {
	this.dataType = dataType;
    }

    @Override
    public String toString() {
	return "GeneralData [data=" + data + ", dataType=" + dataType + "]";
    }

}