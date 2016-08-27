package com.dpdocter.beans;

public class PageSetup {

    private String layout;

    private String color;

    private String leftMargin;
    
    private String rightMargin;
    
    private String topMargin;
    
    private String bottomMargin;

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
    
    public String getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(String leftMargin) {
		this.leftMargin = leftMargin;
	}

	public String getRightMargin() {
		return rightMargin;
	}

	public void setRightMargin(String rightMargin) {
		this.rightMargin = rightMargin;
	}

	public String getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(String topMargin) {
		this.topMargin = topMargin;
	}

	public String getBottomMargin() {
		return bottomMargin;
	}

	public void setBottomMargin(String bottomMargin) {
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
