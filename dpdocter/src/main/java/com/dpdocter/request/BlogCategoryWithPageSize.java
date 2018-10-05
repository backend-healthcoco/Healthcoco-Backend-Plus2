package com.dpdocter.request;

import com.dpdocter.enums.BlogSuperCategoryType;

public class BlogCategoryWithPageSize {

	BlogSuperCategoryType superCategory;
	
	long page = 0;
	
	int size = 0;

	public BlogSuperCategoryType getSuperCategory() {
		return superCategory;
	}

	public void setSuperCategory(BlogSuperCategoryType superCategory) {
		this.superCategory = superCategory;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "BlogCategoryWithPageSize [superCategory=" + superCategory + ", page=" + page + ", size=" + size + "]";
	}
}
