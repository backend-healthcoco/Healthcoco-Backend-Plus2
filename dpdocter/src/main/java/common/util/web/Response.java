package common.util.web;

import java.util.List;

/**
 * 
 * @author veeraj
 *
 */
public class Response<T> {

    private T data;

    private List<T> dataList;

    public T getData() {
	return data;
    }

    public void setData(T data) {
	this.data = data;
    }

    public List<T> getDataList() {
	return dataList;
    }

    public void setDataList(List<T> dataList) {
	this.dataList = dataList;
    }

    @Override
    public String toString() {
	return "Response [data=" + data + ", dataList=" + dataList + "]";
    }

}
