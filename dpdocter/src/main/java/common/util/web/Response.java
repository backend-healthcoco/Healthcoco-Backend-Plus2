package common.util.web;

import java.util.List;

/**
 * 
 * @author veeraj
 *
 */

public class Response<T> {

	private T data;

	private List<?> dataList;

	private Integer count = 0;

	public Response() {
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<?> getDataList() {
		return dataList;
	}

	public void setDataList(List<?> dataList) {
		this.dataList = dataList;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Response [data=" + data + ", dataList=" + dataList + "]";
	}

}
