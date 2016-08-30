package com.dpdocter.beans;

public class PageSetup {

    private String layout;

    private String color;

    private Integer leftMargin;
    
    private Integer rightMargin;
    
    private Integer topMargin;
    
    private Integer bottomMargin;

    private String pageSize;

    public String getLayout() {
	return layout;
    }

    public void setLayout(String layout) {
	this.layout = layout;
    }

    public String getColor() {
	return color;
    }

    public void setColor(String color) {
	this.color = color;
    }
    
    public Integer getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(Integer leftMargin) {
		this.leftMargin = leftMargin;
	}

	public Integer getRightMargin() {
		return rightMargin;
	}

	public void setRightMargin(Integer rightMargin) {
		this.rightMargin = rightMargin;
	}

	public Integer getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(Integer topMargin) {
		this.topMargin = topMargin;
	}

	public Integer getBottomMargin() {
		return bottomMargin;
	}

	public void setBottomMargin(Integer bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public String getPageSize() {
	return pageSize;
    }

    public void setPageSize(String pageSize) {
	this.pageSize = pageSize;
    }

	@Override
	public String toString() {
		return "PageSetup [layout=" + layout + ", color=" + color + ", leftMargin=" + leftMargin + ", rightMargin="
				+ rightMargin + ", topMargin=" + topMargin + ", bottomMargin=" + bottomMargin + ", pageSize=" + pageSize
				+ "]";
	}

    
}
